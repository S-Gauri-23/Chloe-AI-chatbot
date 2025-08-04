package com.springboot.chatgpt.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration  //to make a class a java configuration
public class OpenAIConfiguration {

    @Value("${openapi.api.url}")
    private String apiUrl;

    @Bean
    public RestClient restClient(){
        return RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }
}
