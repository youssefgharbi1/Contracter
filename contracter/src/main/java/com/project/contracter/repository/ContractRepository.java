package com.project.contracter.repository;

import com.project.contracter.enums.ContractStatus;
import com.project.contracter.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByCreatorId(Long creatorId);
    List<Contract> findByStatus(ContractStatus status);
    List<Contract> findByCreatorIdAndStatus(Long creatorId, ContractStatus status);

    @Query("SELECT c FROM Contract c JOIN c.participants cp WHERE cp.user.id = :userId")
    List<Contract> findContractsByParticipantId(@Param("userId") Long userId);

    @Query("SELECT c FROM Contract c JOIN c.participants cp WHERE cp.user.id = :userId AND c.status = :status")
    List<Contract> findContractsByParticipantIdAndStatus(@Param("userId") Long userId, @Param("status") ContractStatus status);

    Long countByCreatorId(Long creatorId);
}

