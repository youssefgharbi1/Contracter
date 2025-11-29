package com.project.contracter.repository;

import com.project.contracter.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {
    List<Signature> findByContractId(Long contractId);

    List<Signature> findByUserId(Long userId);

    Optional<Signature> findByContractIdAndUserId(Long contractId, Long userId);

    @Query("SELECT s FROM Signature s WHERE s.contract.id = :contractId AND s.user.id IN :userIds")
    List<Signature> findByContractIdAndUserIds(@Param("contractId") Long contractId, @Param("userIds") List<Long> userIds);

    Boolean existsByContractIdAndUserId(Long contractId, Long userId);

    @Query("SELECT COUNT(s) FROM Signature s WHERE s.contract.id = :contractId")
    Long countSignaturesByContractId(@Param("contractId") Long contractId);
}
