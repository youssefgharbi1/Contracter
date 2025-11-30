package com.project.contracter.service.serviceInterface;

import com.project.contracter.dtos.contractDraft.ContractDraftCreateDTO;
import com.project.contracter.dtos.contractDraft.ContractDraftDTO;
import com.project.contracter.exception.ResourceNotFoundException;

import java.util.List;

public interface ContractDraftServiceI {
    ContractDraftDTO createDraft(ContractDraftCreateDTO dto, Long editorId) throws ResourceNotFoundException;
    List<ContractDraftDTO> listDraftsForContract(Long contractId) throws ResourceNotFoundException;
    ContractDraftDTO getDraft(Long draftId) throws ResourceNotFoundException;
}
