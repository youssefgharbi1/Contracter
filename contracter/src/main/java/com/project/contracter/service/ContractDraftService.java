package com.project.contracter.service;

import com.project.contracter.dtos.contractDraft.ContractDraftCreateDTO;
import com.project.contracter.dtos.contractDraft.ContractDraftDTO;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.model.Contract;
import com.project.contracter.model.ContractDraft;
import com.project.contracter.model.User;
import com.project.contracter.repository.ContractDraftRepository;
import com.project.contracter.repository.ContractRepository;
import com.project.contracter.repository.UserRepository;
import com.project.contracter.service.serviceInterface.ContractDraftServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContractDraftService implements ContractDraftServiceI {

    private final ContractDraftRepository draftRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    

    @Override
    @Transactional
    public ContractDraftDTO createDraft(ContractDraftCreateDTO dto, Long editorId) throws ResourceNotFoundException {
        Contract c = contractRepository.findById(dto.getContractId()).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        User editor = userRepository.findById(editorId).orElseThrow(() -> new ResourceNotFoundException("Editor not found"));
        Integer latestVersion = draftRepository.findLatestVersionByContractId(c.getId()).orElse(0);
        ContractDraft d = new ContractDraft();
        d.setContract(c);
        d.setContent(dto.getContent());
        d.setVersion(latestVersion + 1);
        d.setEditor(editor);
        d.setCreatedAt(Instant.now());
        ContractDraft saved = draftRepository.save(d);
        return mapToDTO(saved);
    }

    @Override
    public List<ContractDraftDTO> listDraftsForContract(Long contractId) throws ResourceNotFoundException {
        contractRepository.findById(contractId).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        List<ContractDraft> drafts = draftRepository.findByContractIdOrderByVersionDesc(contractId);
        return drafts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public ContractDraftDTO getDraft(Long draftId) throws ResourceNotFoundException {
        ContractDraft d = draftRepository.findById(draftId).orElseThrow(() -> new ResourceNotFoundException("Draft not found"));
        return mapToDTO(d);
    }

    private ContractDraftDTO mapToDTO(ContractDraft d) {
        ContractDraftDTO dto = new ContractDraftDTO();
        dto.setId(d.getId());
        dto.setContractId(d.getContract().getId());
        dto.setVersion(d.getVersion());
        dto.setContent(d.getContent());
        dto.setEditorId(d.getEditor() != null ? d.getEditor().getId() : null);
        dto.setCreatedAt(d.getCreatedAt());
        return dto;
    }
}
