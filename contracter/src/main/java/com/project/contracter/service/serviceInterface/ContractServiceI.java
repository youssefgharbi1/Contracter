package com.project.contracter.service.serviceInterface;

import com.project.contracter.dtos.contract.ContractCreateDTO;
import com.project.contracter.dtos.contract.ContractDTO;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.exception.UnauthorizedException;
import com.project.contracter.exception.BadRequestException;

import java.util.List;

public interface ContractServiceI {
    ContractDTO createContract(ContractCreateDTO dto) throws ResourceNotFoundException;
    ContractDTO updateContract(Long contractId, ContractCreateDTO dto, Long userId) throws ResourceNotFoundException, UnauthorizedException, BadRequestException;
    ContractDTO publishContract(Long contractId, Long userId) throws ResourceNotFoundException, UnauthorizedException, BadRequestException;
    ContractDTO getContract(Long contractId) throws ResourceNotFoundException;
    List<ContractDTO> listContractsByCreator(Long creatorId);
    List<ContractDTO> listContractsByParticipant(Long creatorId);
    void deleteContract(Long id) throws ResourceNotFoundException;

}
