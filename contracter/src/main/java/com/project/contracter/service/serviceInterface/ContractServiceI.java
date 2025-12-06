package com.project.contracter.service.serviceInterface;

import com.project.contracter.dtos.contract.ContractCreateDTO;
import com.project.contracter.dtos.contract.ContractDTO;
import com.project.contracter.dtos.contract.ContractUpdateDTO;
import com.project.contracter.exception.ResourceNotFoundException;
import com.project.contracter.exception.UnauthorizedException;
import com.project.contracter.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContractServiceI {

    @Transactional
    ContractDTO updateContract(Long contractId, ContractUpdateDTO dto)
            throws ResourceNotFoundException, BadRequestException;

    @Transactional
    ContractDTO updateContractStatus(Long contractId, String status)
            throws ResourceNotFoundException, BadRequestException;

    @Transactional
    void deleteContract(Long contractId, Long actorId)
            throws ResourceNotFoundException, UnauthorizedException, BadRequestException;

    ContractDTO publishContract(Long contractId, Long userId) throws ResourceNotFoundException, UnauthorizedException, BadRequestException;

    @Transactional
    ContractDTO createContract(ContractCreateDTO dto, Long creatorId) throws ResourceNotFoundException;

    ContractDTO getContract(Long contractId) throws ResourceNotFoundException;

    @Transactional
    ContractDTO archiveContract(Long contractId, Long userId)
            throws ResourceNotFoundException, UnauthorizedException;

    @Transactional
    ContractDTO unarchiveContract(Long contractId, Long userId)
            throws ResourceNotFoundException, UnauthorizedException, BadRequestException;

    List<ContractDTO> listContractsByCreator(Long creatorId);
    List<ContractDTO> listContractsByParticipant(Long creatorId);

    Page<ContractDTO> listAllContracts(Pageable pageable, String status, String search);

    List<ContractDTO> getContractHistory(Long contractId) throws ResourceNotFoundException;

    List<ContractDTO> searchContracts(String query, String scope, Long userId);

    Object getContractStats(Long userId);
}
