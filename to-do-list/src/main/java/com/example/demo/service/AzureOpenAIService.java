package com.example.demo.service;

import com.example.demo.model.Task;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.stereotype.Service;


@Service
public class AzureOpenAIService {

    private final AzureOpenAiChatModel azureOpenAiChatModel;


    public AzureOpenAIService(AzureOpenAiChatModel azureOpenAiChatModel) {
        this.azureOpenAiChatModel = azureOpenAiChatModel;
    }

    public String generateTaskDetails(String inputPrompt) {
        String msg = azureOpenAiChatModel.call(inputPrompt);
        System.out.println(msg);
        return msg;
    }
}
