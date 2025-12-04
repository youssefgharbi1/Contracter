package com.project.contracter.service.serviceInterface;

import com.project.contracter.dtos.contractDraft.ContractDraftCreateDTO;
import com.project.contracter.dtos.contractDraft.ContractDraftDTO;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;

import java.util.List;

public interface ContractDraftServiceI {
    ContractDraftDTO createDraft(ContractDraftCreateDTO dto, Long editorId)
            throws ResourceNotFoundException, ConflictException;

    List<ContractDraftDTO> listDraftsForContract(Long contractId)
            throws ResourceNotFoundException;

    ContractDraftDTO getDraft(Long draftId) throws ResourceNotFoundException;

    ContractDraftDTO getLatestDraft(Long contractId) throws ResourceNotFoundException;

    ContractDraftDTO getDraftByVersion(Long contractId, Integer version)
            throws ResourceNotFoundException;

    ContractDraftDTO updateDraft(Long contractId, ContractDraftDTO dto)
            throws ResourceNotFoundException, ConflictException;

    void deleteDraftById(Long draftId) throws ResourceNotFoundException, ConflictException;

    void deleteDraftByVersion(Long contractId, Integer version)
            throws ResourceNotFoundException, ConflictException;

    List<ContractDraftDTO> getDraftHistory(Long contractId) throws ResourceNotFoundException;
}