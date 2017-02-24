package com.controller;

import com.OSS.PostObjectPolicy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by sa on 2017-02-22.
 */

@Controller
public class PolicyController {

    @RequestMapping(value = "/createPolicy/{dir}")
    @ResponseBody
    public String createPolicy(@PathVariable("dir") String dir){
        return PostObjectPolicy.createPolicy(dir+"/").toJSONString();
    }
}
