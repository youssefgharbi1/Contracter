package com.project.contracter.repository;

import com.project.contracter.enums.ParticipantRole;
import com.project.contracter.model.ContractParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractParticipantRepository extends JpaRepository<ContractParticipant, Long> {
    List<ContractParticipant> findByContractId(Long contractId);

    List<ContractParticipant> findByUserId(Long userId);

    List<ContractParticipant> findByContractIdAndRole(Long contractId, ParticipantRole role);

    Optional<ContractParticipant> findByContractIdAndUserId(Long contractId, Long userId);

    @Query("SELECT cp FROM ContractParticipant cp WHERE cp.contract.id = :contractId AND cp.requiredToSign = true ORDER BY cp.orderToSign")
    List<ContractParticipant> findRequiredSignersByContractIdOrdered(@Param("contractId") Long contractId);

    Boolean existsByContractIdAndUserId(Long contractId, Long userId);

    @Query("SELECT COUNT(cp) FROM ContractParticipant cp WHERE cp.contract.id = :contractId AND cp.role = :role")
    Long countByContractIdAndRole(@Param("contractId") Long contractId, @Param("role") ParticipantRole role);
}
