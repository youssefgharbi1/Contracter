package com.project.contracter.repository;

import com.project.contracter.model.ContractAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractAttachmentRepository extends JpaRepository<ContractAttachment, Long> {
    List<ContractAttachment> findByContractId(Long contractId);

    List<ContractAttachment> findByUploadedById(Long uploadedById);

    Optional<ContractAttachment> findByContractIdAndFilename(Long contractId, String filename);

    @Query("SELECT ca FROM ContractAttachment ca WHERE ca.contract.id = :contractId AND ca.fileType = :fileType")
    List<ContractAttachment> findByContractIdAndFileType(@Param("contractId") Long contractId, @Param("fileType") String fileType);
}
