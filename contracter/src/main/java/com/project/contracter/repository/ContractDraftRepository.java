package com.project.contracter.repository;

import com.project.contracter.model.ContractDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractDraftRepository extends JpaRepository<ContractDraft, Long> {
    List<ContractDraft> findByContractId(Long contractId);

    List<ContractDraft> findByContractIdOrderByVersionDesc(Long contractId);

    Optional<ContractDraft> findByContractIdAndVersion(Long contractId, Integer version);

    @Query("SELECT MAX(cd.version) FROM ContractDraft cd WHERE cd.contract.id = :contractId")
    Optional<Integer> findLatestVersionByContractId(@Param("contractId") Long contractId);
}
