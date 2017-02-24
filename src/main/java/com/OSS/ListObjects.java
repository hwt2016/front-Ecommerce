package com.OSS;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sa on 2017-02-24.
 */
public class ListObjects {

    public List<String> SelectImagesByUserDir(String dir){
        List <String> Images= new ArrayList();
        OSSClient ossClient = new OSSClient(Constant.ENDPOINT, Constant.ACCESS_ID, Constant.ACCESS_KEY);
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest("testthem");
        listObjectsRequest.setPrefix(dir+"/");
        // 递归列出fun目录下的所有文件
        ObjectListing listing = ossClient.listObjects(listObjectsRequest);
        // 遍历所有Object
        System.out.println("Objects:");
        int count=0;
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            System.out.println(objectSummary.getKey());
            Images.add("http://testthem.oss-cn-hangzhou.aliyuncs.com/"+objectSummary.getKey().toString());
        }

        // 关闭client
        ossClient.shutdown();
        return Images;

    }
}
