package com.atguigu.gmall.cas.controller;

import com.atguigu.gmall.cas.Config.WeiboAccessTokenVo;
import com.atguigu.gmall.cas.Config.WeiboAuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@CrossOrigin
@RequestMapping
@Controller
public class CasController {

    @Autowired
    private WeiboAuthConfig config;

    RestTemplate restTemplate = new RestTemplate();//发送请求的对象

    @GetMapping("/register/authentication")
    public String registerAuthentication(@RequestParam("authType") String authType){

        if("weibo".equals(authType)){
            return "redirect:"+config.getAuthPage();
        }
        return "redirect:"+config.getAuthPage();
    }

    @GetMapping("/success")
    public String codeGetToken(@RequestParam("code") String code){
        //获取到code
        System.out.println("获取到code==="+code);

        //使用code换取access_token
        String authPage = config.getAccessTokenPage()+"&code="+code;
        WeiboAccessTokenVo accessTokenVo = restTemplate.postForObject(authPage, null, WeiboAccessTokenVo.class);

        
        //将access_token转为自己系统的
        String token = UUID.randomUUID().toString();
        return code;
    }
}
