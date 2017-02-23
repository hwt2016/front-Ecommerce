package com.policy;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.util.Constant;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sa on 2017-02-22.
 */
public class PostObjectPolicy {

    private static Logger logger = Logger.getLogger(PostObjectPolicy.class);

    /**
     * 将生成的object直接返回给前端
     * @return
     */
    public static JSONObject createPolicy(String dir) {
        OSSClient client = new OSSClient(Constant.ENDPOINT, Constant.ACCESS_ID, Constant.ACCESS_KEY);
        JSONObject policyObject = null;
//        JSONObject callback_Json =null;
        try {
            long expireTime = 100;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = client.generatePostPolicy(expiration, policyConds);

            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

//            //生成callback
//            Map<String, Object> callback_param = new LinkedHashMap<String, Object>();
//            callback_param.put("callbackUrl","http://oss-demo.aliyuncs.com:23450");
//            callback_param.put("callbackBody","filename=${object}");
//            callback_param.put("callbackBodyType","application/x-www-form-urlencoded");
//            callback_Json =new JSONObject(callback_param);
//            String callback_string =callback_Json.toString();
//            byte[] binaryCallbackData = callback_string.getBytes();
//            String callbackbody = BinaryUtil.toBase64String(binaryCallbackData);
//            System.out.println("callback_string="+callback_string);
//            System.out.println("callbackbody="+callbackbody);



            Map<String, Object> respMap = new LinkedHashMap<String, Object>();
            respMap.put("accessid", Constant.ACCESS_ID);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", Constant.HOST);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
//            respMap.put("callback",callbackbody);
            policyObject = new JSONObject(respMap);
            System.out.println(policyObject.toString());
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return policyObject;
    }

}
