package com.springboot.chatgpt.Service;

import com.springboot.chatgpt.DTO.ChatGPTRequest;
import com.springboot.chatgpt.DTO.ChatGPTResponse;
import com.springboot.chatgpt.DTO.PromptRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ChatGPTService {   //to make the ChatGPT Api call

    private final RestClient restClient;

    public ChatGPTService(RestClient restClient){
        this.restClient = restClient;
    }

    private static final Logger logger = LoggerFactory.getLogger(ChatGPTService.class);

    @Value("${openapi.api.key}")
    private String apiKey;

    @Value("${openapi.api.model}")
    private String model;

    // method for calling the ChatGPT API
    public String getChatResponse(PromptRequest promptRequest){

        logger.info("Received prompt: " + promptRequest.prompt());

        ChatGPTRequest chatGPTRequest = new ChatGPTRequest(
                model,
                List.of(new ChatGPTRequest.Message("user", promptRequest.prompt())) //.prompt() ???
        );

        try {

            ChatGPTResponse response = restClient.post()
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(chatGPTRequest)
                    .retrieve()
                    .body(ChatGPTResponse.class);

            String chatResponse = response.choices().get(0).message().content();
            logger.info("Generated response: " + chatResponse);
            return chatResponse;
        }
        catch (Exception e){
            logger.error("Error while calling OpenAI API", e);
            throw e;
        }
    }
}
