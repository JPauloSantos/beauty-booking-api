package com.beautyscheduler.adapter.out.persistence.repository;

import com.beautyscheduler.adapter.out.persistence.entity.ReviewJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewJpaRepository extends JpaRepository<ReviewJpaEntity, UUID> {
    List<ReviewJpaEntity> findByEstablishmentId(UUID establishmentId);
    List<ReviewJpaEntity> findByProfessionalId(UUID professionalId);
    boolean existsByClientIdAndAppointmentId(UUID clientId, UUID appointmentId);
}
