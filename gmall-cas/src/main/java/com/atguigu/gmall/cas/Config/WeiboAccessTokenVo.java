package com.atguigu.gmall.cas.Config;

import lombok.Data;

@Data
public class WeiboAccessTokenVo {

//    {
//        "access_token": "2.00i_dKLHjnpLtD76361fcdf6hsoq1C",
//            "remind_in": "157679999",
//            "expires_in": 157679999,
//            "uid": "6578003938",
//            "isRealName": "true"
//    }

    private String access_token;
    private String remind_in;
    private String expires_in;
    private String uid;
    private  String isRealName;
}
