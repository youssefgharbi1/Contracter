package com.project.contracter.service;

import com.project.contracter.dtos.signature.SignatureCreateDTO;
import com.project.contracter.dtos.signature.SignatureDTO;
import com.project.contracter.enums.ContractStatus;
import com.project.contracter.enums.ParticipantRole;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.exception.UnauthorizedException;
import com.project.contracter.model.Contract;
import com.project.contracter.model.ContractParticipant;
import com.project.contracter.model.Signature;
import com.project.contracter.model.User;
import com.project.contracter.repository.ContractParticipantRepository;
import com.project.contracter.repository.ContractRepository;
import com.project.contracter.repository.SignatureRepository;
import com.project.contracter.repository.UserRepository;
import com.project.contracter.service.serviceInterface.SignatureServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SignatureService implements SignatureServiceI {

    private final SignatureRepository signatureRepository;
    private final ContractRepository contractRepository;
    private final ContractParticipantRepository participantRepository;
    private final UserRepository userRepository;

    

    @Override
    @Transactional
    public SignatureDTO signContract(SignatureCreateDTO dto, Long signerId) throws ResourceNotFoundException, UnauthorizedException, ConflictException {
        Contract c = contractRepository.findById(dto.getContractId()).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        if (c.getStatus() != ContractStatus.PUBLISHED) {
            throw new UnauthorizedException("Contract must be published before signing");
        }
        // ensure signer is participant and a signer
        ContractParticipant cp = participantRepository.findByContractIdAndUserId(c.getId(), signerId).orElseThrow(() -> new UnauthorizedException("User is not a participant"));
        if (cp.getRole() != ParticipantRole.SIGNER) {
            throw new UnauthorizedException("Participant is not a signer");
        }
        // ensure not already signed
        if (signatureRepository.existsByContractIdAndUserId(c.getId(), signerId)) {
            throw new ConflictException("User already signed this contract");
        }
        User signer = userRepository.findById(signerId).orElseThrow(() -> new ResourceNotFoundException("Signer not found"));
        Signature s = new Signature();
        s.setContract(c);
        s.setUser(signer);
        // Basic signatureValue: hash-like string (for demo). In production use real crypto.
        String sigVal = "SIG:" + signer.getId() + ":" + c.getId() + ":" + Instant.now().toEpochMilli();
        s.setSignatureValue(sigVal);
        s.setSignatureType(dto.getSignatureType());
        s.setSignedAt(Instant.now());
        Signature saved = signatureRepository.save(s);

        // Optionally update contract status if all required signers signed
        Long required = participantRepository.countByContractIdAndRole(c.getId(), ParticipantRole.SIGNER);
        Long signed = signatureRepository.countSignaturesByContractId(c.getId());
        if (required > 0 && signed >= required) {
            c.setStatus(ContractStatus.SIGNED);
            c.setUpdatedAt(Instant.now());
            contractRepository.save(c);
        }

        return mapToDTO(saved);
    }

    @Override
    public List<SignatureDTO> listSignatures(Long contractId) throws ResourceNotFoundException {
        contractRepository.findById(contractId).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        List<Signature> list = signatureRepository.findByContractId(contractId);
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private SignatureDTO mapToDTO(Signature s) {
        SignatureDTO dto = new SignatureDTO();
        dto.setId(s.getId());
        dto.setContractId(s.getContract().getId());
        dto.setUserId(s.getUser().getId());
        dto.setSignatureValue(s.getSignatureValue());
        dto.setSignatureType(s.getSignatureType());
        dto.setSignedAt(s.getSignedAt());
        return dto;
    }
}
