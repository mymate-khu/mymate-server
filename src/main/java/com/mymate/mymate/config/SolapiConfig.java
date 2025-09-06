package com.mymate.mymate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.service.DefaultMessageService;

@Configuration
@EnableConfigurationProperties(SolapiProperties.class)
public class SolapiConfig {

    @Bean
    public DefaultMessageService defaultMessageService(SolapiProperties properties) {
        return SolapiClient.INSTANCE.createInstance(properties.getApiKey(), properties.getApiSecret());
    }
}

@ConfigurationProperties(prefix = "solapi")
class SolapiProperties {
    private String apiKey;
    private String apiSecret;
    private String defaultFrom;

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }

    public String getDefaultFrom() { return defaultFrom; }
    public void setDefaultFrom(String defaultFrom) { this.defaultFrom = defaultFrom; }
}


