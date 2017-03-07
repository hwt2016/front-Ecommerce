package com.controller;

import com.OSS.PostObjectPolicy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


/**
 * Created by sa on 2017-02-22.
 */

@Controller
public class PolicyController {

    @RequestMapping(value = "/createPolicy/{dir}")
    @ResponseBody
    public String createPolicy(@PathVariable("dir") String dir, HttpSession session){
        String  nickname=session.getAttribute("nickname").toString();//获取用户nickname
        String userdir=nickname+"/"+dir;//按照nickname+commodityName即（nickname/commodityName）目录存储
        System.out.println("userdir="+userdir);
        return PostObjectPolicy.createPolicy(nickname+"/"+dir+"/").toJSONString();
    }
}
