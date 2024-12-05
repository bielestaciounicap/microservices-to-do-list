package com.example.demo.Repository;

import com.example.demo.model.AzureSpeech;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AzureSpeechRepository extends JpaRepository<AzureSpeech, Long> {
}
