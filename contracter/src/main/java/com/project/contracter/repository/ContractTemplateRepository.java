package com.project.contracter.repository;

import com.project.contracter.model.ContractTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractTemplateRepository extends JpaRepository<ContractTemplate, Long> {
    List<ContractTemplate> findByCreatorId(Long creatorId);

    Optional<ContractTemplate> findByName(String name);

    @Query("SELECT ct FROM ContractTemplate ct WHERE ct.name LIKE %:keyword% OR ct.description LIKE %:keyword%")
    List<ContractTemplate> searchByNameOrDescription(@Param("keyword") String keyword);

    Boolean existsByName(String name);
}
