package com.atguigu.gmall.cas.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "oauth.weibo")
@Configuration
@Data
public class WeiboAuthConfig {

    private String appKey;
    private String appSecret;
    private String authSuccessUrl;
    private String authFailUrl;
    private String authPage;
    private String accessTokenPage;
}
