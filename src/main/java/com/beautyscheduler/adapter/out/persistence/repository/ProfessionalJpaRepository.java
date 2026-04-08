package com.beautyscheduler.adapter.out.persistence.repository;

import com.beautyscheduler.adapter.out.persistence.entity.ProfessionalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfessionalJpaRepository extends JpaRepository<ProfessionalJpaEntity, UUID> {
    List<ProfessionalJpaEntity> findByEstablishmentId(UUID establishmentId);
}
