package com.project.contracter.service.serviceInterface;

import com.project.contracter.dtos.signature.SignatureCreateDTO;
import com.project.contracter.dtos.signature.SignatureDTO;
import com.project.contracter.exception.ConflictException;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.exception.UnauthorizedException;

import java.util.List;

public interface SignatureServiceI {
    SignatureDTO signContract(SignatureCreateDTO dto, Long signerId) throws ResourceNotFoundException, UnauthorizedException, ConflictException;
    List<SignatureDTO> listSignatures(Long contractId) throws ResourceNotFoundException;
}
