package com.project.contracter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String firstName;
    private String lastName;
    private String publicKey;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    // Relationships
    @OneToMany(mappedBy = "creator")
    private List<Contract> createdContracts;

    @OneToMany(mappedBy = "user")
    private List<Signature> signatures;

    @OneToMany(mappedBy = "user")
    private List<ContractParticipant> contractParticipants;

}