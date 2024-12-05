package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.service.AzureSpeechService;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/{userId}/task/speech")
public class SpeechController {

    @Autowired
    private AzureSpeechService azureSpeechService;

    @Autowired
    private TaskService taskService;

    @PostMapping("/recognize")
    public ResponseEntity<Task> recognizeSpeech(@RequestParam("file") MultipartFile file,
                                                @PathVariable Long userId) {
        try {
            String recognizedText = azureSpeechService.recognizeSpeechFromAudio(file, userId);
            System.out.println("Texto reconhecido:" +recognizedText);
            Task createdTask = taskService.createTaskFromText(recognizedText, userId, 1L);
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }






    @PostMapping("/synthesize")
    public ResponseEntity<String> synthesizeSpeech(@RequestParam("text") String text,
                                                   @RequestParam("outputFilePath") String outputFilePath
            ,@PathVariable Long userId) {
        try {
            String message = azureSpeechService.textToSpeech(text, outputFilePath, userId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao converter texto para fala: " + e.getMessage());
        }
    }
}

