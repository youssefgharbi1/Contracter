package com.project.contracter.repository;

import com.project.contracter.model.Contract;
import com.project.contracter.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    // Find contracts by creator
    @Query("SELECT c FROM Contract c WHERE c.creator.id = :creatorId")
    List<Contract> findByCreatorId(@Param("creatorId") Long creatorId);

    // Find contracts where user is a participant
    @Query("SELECT DISTINCT c FROM Contract c " +
            "JOIN c.participants cp " +
            "WHERE cp.user.id = :participantId")
    List<Contract> findContractsByParticipantId(@Param("participantId") Long participantId);

    // Pagination and filtering methods
    Page<Contract> findByStatus(ContractStatus status, Pageable pageable);

    Page<Contract> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Contract> findByStatusAndTitleContainingIgnoreCase(
            ContractStatus status, String title, Pageable pageable);

    @Query("SELECT c FROM Contract c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.category) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Contract> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            @Param("search") String search, Pageable pageable);

    // Search methods (simplified - removed tag search)
    @Query("SELECT c FROM Contract c WHERE c.creator.id = :userId AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Contract> searchByCreator(@Param("userId") Long userId, @Param("query") String query);

    @Query("SELECT DISTINCT c FROM Contract c " +
            "JOIN c.participants p " +
            "WHERE p.user.id = :userId AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Contract> searchByParticipant(@Param("userId") Long userId, @Param("query") String query);

    @Query("SELECT c FROM Contract c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Contract> searchAll(@Param("query") String query);

    // Count methods for stats
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.creator.id = :creatorId AND c.status = :status")
    long countByCreatorIdAndStatus(@Param("creatorId") Long creatorId,
                                   @Param("status") ContractStatus status);

    @Query("SELECT COUNT(DISTINCT c) FROM Contract c " +
            "JOIN c.participants p " +
            "WHERE p.user.id = :participantId")
    long countContractsByParticipantId(@Param("participantId") Long participantId);

    // Optional: Find by category with pagination
    Page<Contract> findByCategory(String category, Pageable pageable);
}