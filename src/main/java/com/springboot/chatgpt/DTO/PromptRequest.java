package com.springboot.chatgpt.DTO;
import jakarta.validation.constraints.NotBlank;

public record PromptRequest(
        @NotBlank(message = "Prompt cannot be blank")
        String prompt
) {
}