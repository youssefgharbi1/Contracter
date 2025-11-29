package com.project.contracter.model;


import com.project.contracter.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "contracts")
@Getter
@Setter
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private ContractStatus status = ContractStatus.DRAFT;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<ContractDraft> drafts;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<ContractParticipant> participants;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<Signature> signatures;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<ContractAttachment> attachments;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant publishedAt;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();
}