package com.ly.xunwu.service.house;

import com.ly.xunwu.dto.HouseDTO;
import com.ly.xunwu.dto.ServiceMultiResult;
import com.ly.xunwu.dto.ServiceResult;
import com.ly.xunwu.from.DatatableSearch;
import com.ly.xunwu.from.HouseForm;
import com.ly.xunwu.from.RentSearch;

/**房屋管理服务接口
 * @author liyong
 * @date 2019/9/6
 */
public interface IHouseService {

    /**
     * 新增
     * @param houseForm
     * @return
     */
    ServiceResult<HouseDTO> save(HouseForm houseForm);

    ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody);

    ServiceResult update(HouseForm houseForm);
    /**
     * 查询完整房源信息
     * @param id
     * @return
     */
    ServiceResult<HouseDTO> findCompleteOne(Long id);


    /**
     * 移除图片
     * @param id
     * @return
     */
    ServiceResult removePhoto(Long id);

    /**
     * 更新封面
     * @param coverId
     * @param targetId
     * @return
     */
    ServiceResult updateCover(Long coverId, Long targetId);

    /**
     * 新增标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult addTag(Long houseId, String tag);

    /**
     * 移除标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult removeTag(Long houseId, String tag);

    /**
     * 更新房源状态
     * @param id
     * @param status
     * @return
     */
    ServiceResult updateStatus(Long id, int status);

    /**
     * 查询房源信息集
     * @param rentSearch
     * @return
     */
    ServiceMultiResult<HouseDTO> query(RentSearch rentSearch);
}
