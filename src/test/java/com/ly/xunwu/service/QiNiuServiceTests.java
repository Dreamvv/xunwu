package com.ly.xunwu.service;

import com.ly.xunwu.XunwuApplicationTests;
import com.ly.xunwu.service.house.IQiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @author liyong
 * @date 2019/9/5
 */
public class QiNiuServiceTests extends XunwuApplicationTests {
    @Autowired
    private IQiNiuService qiNiuService;
    @Test
    public void testUploadFile() {
        String fileName = "E:/images/snow.jpg";
        File file = new File(fileName);

        Assert.assertTrue(file.exists());

        try {
            Response response = qiNiuService.uploadFile(file);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        String key = "FutoJR99Y-B2ux1fcmigN0UMpc4h";

        try {
            Response response = qiNiuService.delete(key);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }
}
