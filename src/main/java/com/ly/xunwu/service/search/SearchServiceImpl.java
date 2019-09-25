package com.ly.xunwu.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.xunwu.dto.ServiceMultiResult;
import com.ly.xunwu.dto.ServiceResult;
import com.ly.xunwu.entity.House;
import com.ly.xunwu.entity.HouseDetail;
import com.ly.xunwu.entity.HouseTag;
import com.ly.xunwu.entity.SupportAddress;
import com.ly.xunwu.from.RentSearch;
import com.ly.xunwu.repository.HouseDetailRepository;
import com.ly.xunwu.repository.HouseRepository;
import com.ly.xunwu.repository.HouseTagRepository;
import com.ly.xunwu.repository.SupportAddressRepository;
import com.ly.xunwu.search.HouseIndexKey;
import com.ly.xunwu.search.HouseIndexMessage;
import com.ly.xunwu.search.HouseIndexTemplate;

import com.ly.xunwu.service.house.IAddressService;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liyong
 * @date 2019/9/10
 */
@Service
public class SearchServiceImpl implements ISearchService{

    private static Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
    private static final String INDEX_NAME = "xunwu";

    private static final String INDEX_TYPE = "house";

    private static final String INDEX_TOPIC = "house_build";
    @Autowired
    private HouseRepository houseRepository;


    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository tagRepository;



    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private IAddressService addressService;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Resource
    private KafkaTemplate<String,String> kafkaTemplate;

    @KafkaListener(topics = INDEX_TOPIC)
    private void handleMessage(String content){
        try {
            HouseIndexMessage message = objectMapper.readValue(content, HouseIndexMessage.class);

            switch (message.getOperation()) {
                case HouseIndexMessage.INDEX:
                    this.createOrUpdateIndex(message);
                    break;
                case HouseIndexMessage.REMOVE:
                    this.removeIndex(message);
                    break;
                default:
                    logger.warn("Not support message content " + content);
                    break;
            }
        } catch (IOException e) {
            logger.error("Cannot parse json for " + content, e);
        }
    }

    private void createOrUpdateIndex(HouseIndexMessage message) {
        Long houseId = message.getHouseId();

        House house = houseRepository.findOne(houseId);
        if (house == null){
            logger.error("Index house {} does not exist!",houseId);
            this.index(houseId, message.getRetry() + 1);

            return;
        }
        HouseIndexTemplate indexTemplate = new HouseIndexTemplate();
        modelMapper.map(house,indexTemplate);
        HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
        if(detail == null){

        }
        modelMapper.map(detail,indexTemplate);
        List<HouseTag> tags = tagRepository.findAllByHouseId(houseId);
        if (tags != null && !tags.isEmpty()){
            List<String> tagStrings = new ArrayList<>();
            tags.forEach(houseTag -> tagStrings.add(houseTag.getName()));
            indexTemplate.setTags(tagStrings);
        }
        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,houseId));

        logger.debug(requestBuilder.toString());
        SearchResponse searchResponse = requestBuilder.get();

        boolean success;
        long totalHit = searchResponse.getHits().getTotalHits();
        if (totalHit == 0){
            success =  create(indexTemplate);
        }else if (totalHit == 1){
            String esId = searchResponse.getHits().getAt(0).getId();
            success =    update(esId,indexTemplate);
        }else {
            success =   deleteAndCreate(totalHit,indexTemplate);
        }

        if (success){
            logger.debug("Index success with house"+houseId);
        }

    }
    private void removeIndex(HouseIndexMessage message) {
        Long houseId = message.getHouseId();
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId))
                .source(INDEX_NAME);

        logger.debug("Delete by query for house: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        logger.debug("Delete total " + deleted);

     /*   ServiceResult serviceResult = addressService.removeLbs(houseId);

        if (!serviceResult.isSuccess() || deleted <= 0) {
            logger.warn("Did not remove data from es for response: " + response);
            // 重新加入消息队列
            this.remove(houseId, message.getRetry() + 1);
        }*/
    }
    @Override
    public void index(Long houseId) {

        this.index(houseId, 0);
    }
    private void index(Long houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            logger.error("Retry index times over 3 for house: " + houseId + " Please check it!");
            return;
        }

        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry);
        try {
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("Json encode error for " + message);
        }

    }
    private boolean create(HouseIndexTemplate indexTemplate) {


        try {
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

            logger.debug("Create index with house: " + indexTemplate.getHouseId());
            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Error to index house " + indexTemplate.getHouseId(), e);
            return false;
        }
    }

    private boolean update(String esId, HouseIndexTemplate indexTemplate) {

        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId).setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

            logger.debug("Update index with house: " + indexTemplate.getHouseId());
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Error to index house " + indexTemplate.getHouseId(), e);
            return false;
        }
    }

    private boolean deleteAndCreate(long totalHit, HouseIndexTemplate indexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId()))
                .source(INDEX_NAME);

        logger.debug("Delete by query for house: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted != totalHit) {
            logger.warn("Need delete {}, but {} was deleted!", totalHit, deleted);
            return false;
        } else {
            return create(indexTemplate);
        }
    }


    @Override
    public void remove(Long houseId) {
     this.remove(houseId,0);
    }
    private void remove(Long houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            logger.error("Retry remove times over 3 for house: " + houseId + " Please check it!");
            return;
        }

        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
        try {
            this.kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("Cannot encode json for " + message, e);
        }
    }

    @Override
    public ServiceMultiResult<Long> query(RentSearch rentSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.filter(
                QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearch.getCityEnName())
        );

        if (rentSearch.getRegionEnName() != null && !"*".equals(rentSearch.getRegionEnName())) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearch.getRegionEnName())
            );
        }
        return null;
    }
}
