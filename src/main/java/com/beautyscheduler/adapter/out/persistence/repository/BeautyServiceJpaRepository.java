package com.beautyscheduler.adapter.out.persistence.repository;

import com.beautyscheduler.adapter.out.persistence.entity.BeautyServiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BeautyServiceJpaRepository extends JpaRepository<BeautyServiceJpaEntity, UUID> {
    List<BeautyServiceJpaEntity> findByEstablishmentId(UUID establishmentId);
}
