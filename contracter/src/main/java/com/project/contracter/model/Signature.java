package com.project.contracter.model;

import com.project.contracter.enums.SignatureType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "signatures")
@Getter
@Setter
public class Signature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String signatureValue;

    @Column(nullable = false)
    private Instant signedAt = Instant.now();

    @Enumerated(EnumType.STRING)
    private SignatureType signatureType;

}