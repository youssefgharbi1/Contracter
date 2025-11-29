package com.project.contracter.model;

import com.project.contracter.enums.ParticipantRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "contract_participants")
@Getter
@Setter
public class ContractParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ParticipantRole role;

    private Boolean requiredToSign = true;

    private Integer orderToSign;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // Getters and setters...
}
