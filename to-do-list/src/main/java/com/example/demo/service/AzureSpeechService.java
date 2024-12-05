package com.example.demo.service;

import com.example.demo.Repository.AzureSpeechRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.model.AzureSpeech;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;

@Service
public class AzureSpeechService {

    @Autowired
    private AzureSpeechRepository azureSpeechRepository;

    @Autowired
    private UserRepository userRepository;

    private final String speechKey = "1gGUNQZNsS63PaazRyj4TSVgTg2WKEgZbccWpDFPlaAWRI5gZs2mJQQJ99AKACYeBjFXJ3w3AAAYACOGcVQo"; // Substitua com sua chave
    private final String region = "eastus"; // Substitua com sua região

    public String recognizeSpeechFromAudio(MultipartFile file, Long userID) throws Exception {
        // Criar arquivo temporário
        File tempFile = File.createTempFile("audio", ".mp3");
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Configuração do SDK
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, region);
        speechConfig.setSpeechRecognitionLanguage("pt-BR");

        String recognizedText;

        try (AudioConfig audioConfig = AudioConfig.fromWavFileInput(tempFile.getAbsolutePath());
             SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig)) {

            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                recognizedText = result.getText();
            } else if (result.getReason() == ResultReason.NoMatch) {
                recognizedText = "Não foi possível reconhecer a fala.";
            } else {
                CancellationDetails details = CancellationDetails.fromResult(result);
                recognizedText = "Erro no reconhecimento: " + details.getErrorDetails();
            }
        } finally {
            tempFile.delete(); // Apagar arquivo temporário
        }

        // Salvar no banco de dados
        AzureSpeech azureSpeech = new AzureSpeech();
        azureSpeech.setSpeechContent(recognizedText);
        azureSpeech.setStatus("SUCCESS");
        azureSpeech.setCreatedAt(LocalDate.now());
        azureSpeech.setUser(userRepository.findByID(userID));

        azureSpeechRepository.save(azureSpeech);

        return "Texto reconhecido: " + recognizedText;
    }

    public String textToSpeech(String text, String outputFilePath, Long userId) throws Exception {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, region);
        speechConfig.setSpeechSynthesisVoiceName("en-US-JessaNeural");

        String status;

        try (AudioConfig audioConfig = AudioConfig.fromWavFileOutput(outputFilePath);
             SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig, audioConfig)) {

            SpeechSynthesisResult result = synthesizer.SpeakTextAsync(text).get();

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                status = "Conversão bem-sucedida.";
            } else {
                status = "Erro na conversão: " + result.getResultId();
            }
        }

        // Salvar no banco de dados
        AzureSpeech azureSpeech = new AzureSpeech();
        azureSpeech.setSpeechContent(text);
        azureSpeech.setStatus(status.equals("Conversão bem-sucedida.") ? "SUCCESS" : "FAIL");
        azureSpeech.setCreatedAt(LocalDate.now());
        azureSpeech.setUser(userRepository.findByID(userId));

        azureSpeechRepository.save(azureSpeech);

        return status;
    }
}
