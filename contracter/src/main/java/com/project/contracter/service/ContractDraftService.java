package com.project.contracter.service;

import com.project.contracter.dtos.contractDraft.ContractDraftCreateDTO;
import com.project.contracter.dtos.contractDraft.ContractDraftDTO;
import com.project.contracter.enums.ParticipantRole;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.model.Contract;
import com.project.contracter.model.ContractDraft;
import com.project.contracter.model.ContractParticipant;
import com.project.contracter.repository.ContractDraftRepository;
import com.project.contracter.repository.ContractParticipantRepository;
import com.project.contracter.repository.ContractRepository;
import com.project.contracter.service.serviceInterface.ContractDraftServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ContractDraftService implements ContractDraftServiceI {

    private final ContractDraftRepository draftRepository;
    private final ContractRepository contractRepository;
    private final ContractParticipantRepository participantRepository;

    @Override
    @Transactional
    public ContractDraftDTO createDraft(ContractDraftCreateDTO dto, Long editorId)
            throws ResourceNotFoundException, ConflictException {

        // Validate contract exists
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        // Check if editor is a participant with EDITOR or CREATOR role
        ContractParticipant editor = participantRepository
                .findByContractIdAndUserId(dto.getContractId(), editorId)
                .orElseThrow(() -> new ResourceNotFoundException("You are not a participant in this contract"));

        // Only EDITOR, CREATOR, or contract creator can create drafts
        if (editor.getRole() != ParticipantRole.EDITOR &&
                editor.getRole() != ParticipantRole.CREATOR &&
                !contract.getCreator().getId().equals(editorId)) {
            throw new ConflictException("Only editors or contract creator can create drafts");
        }

        // Get latest version
        Integer latestVersion = draftRepository.findLatestVersionByContractId(contract.getId())
                .orElse(0);

        // Create new draft
        ContractDraft draft = new ContractDraft();
        draft.setContract(contract);
        draft.setContent(dto.getContent());
        draft.setVersion(latestVersion + 1);
        draft.setEditor(editor);
        draft.setCreatedAt(Instant.now());

        ContractDraft saved = draftRepository.save(draft);
        return mapToDTO(saved);
    }

    @Override
    public List<ContractDraftDTO> listDraftsForContract(Long contractId)
            throws ResourceNotFoundException {

        // Verify contract exists
        contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        List<ContractDraft> drafts = draftRepository.findByContractIdOrderByVersionDesc(contractId);
        return drafts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContractDraftDTO getDraft(Long draftId) throws ResourceNotFoundException {
        ContractDraft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found"));
        return mapToDTO(draft);
    }

    @Override
    public ContractDraftDTO getLatestDraft(Long contractId) throws ResourceNotFoundException {
        contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        return draftRepository.findByContractIdOrderByVersionDesc(contractId)
                .stream()
                .findFirst()
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No drafts found for this contract"));
    }

    @Override
    public ContractDraftDTO getDraftByVersion(Long contractId, Integer version)
            throws ResourceNotFoundException {

        contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        ContractDraft draft = draftRepository.findByContractIdAndVersion(contractId, version)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Draft version %d not found", version)));

        return mapToDTO(draft);
    }

    @Override
    @Transactional
    public ContractDraftDTO updateDraft(Long contractId, ContractDraftDTO dto)
            throws ResourceNotFoundException, ConflictException {

        // Validate draft exists and belongs to contract
        ContractDraft draft = draftRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found"));

        if (!draft.getContract().getId().equals(contractId)) {
            throw new ConflictException("Draft does not belong to this contract");
        }

        // Update content
        draft.setContent(dto.getContent());
        draft.setCreatedAt(Instant.now()); // Update timestamp

        ContractDraft updated = draftRepository.save(draft);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteDraftById(Long draftId) throws ResourceNotFoundException {
        ContractDraft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found"));

        // Cannot delete if it's the only draft
        long draftCount = draftRepository.countByContractId(draft.getContract().getId());
        if (draftCount <= 1) {
            throw new ConflictException("Cannot delete the only draft of a contract");
        }

        draftRepository.delete(draft);
    }

    @Override
    @Transactional
    public void deleteDraftByVersion(Long contractId, Integer version)
            throws ResourceNotFoundException, ConflictException {

        ContractDraft draft = draftRepository.findByContractIdAndVersion(contractId, version)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Draft version %d not found", version)));

        // Cannot delete if it's the only draft
        long draftCount = draftRepository.countByContractId(contractId);
        if (draftCount <= 1) {
            throw new ConflictException("Cannot delete the only draft of a contract");
        }

        draftRepository.delete(draft);
    }

    @Override
    public List<ContractDraftDTO> getDraftHistory(Long contractId)
            throws ResourceNotFoundException {

        contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        return draftRepository.findByContractIdOrderByVersionAsc(contractId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ContractDraftDTO mapToDTO(ContractDraft draft) {
        ContractDraftDTO dto = new ContractDraftDTO();
        dto.setId(draft.getId());
        dto.setContractId(draft.getContract().getId());
        dto.setVersion(draft.getVersion());
        dto.setContent(draft.getContent());
        dto.setEditorId(draft.getEditor() != null ? draft.getEditor().getId() : null);
        dto.setEditorUsername(draft.getEditor() != null && draft.getEditor().getUser() != null ?
                draft.getEditor().getUser().getFirstName() + " " + draft.getEditor().getUser().getLastName() :
                null);
        dto.setCreatedAt(draft.getCreatedAt());
        return dto;
    }
}