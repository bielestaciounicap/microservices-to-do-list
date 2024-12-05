package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "azure_speech")
public class AzureSpeech {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String speechContent;

    private String status;

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")  // O nome da coluna de relacionamento
    private User user;
}