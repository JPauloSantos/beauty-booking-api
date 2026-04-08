package com.beautyscheduler.adapter.out.persistence.repository;

import com.beautyscheduler.adapter.out.persistence.entity.AppointmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpaEntity, UUID> {
    List<AppointmentJpaEntity> findByClientId(UUID clientId);
    List<AppointmentJpaEntity> findByProfessionalId(UUID professionalId);
    List<AppointmentJpaEntity> findByEstablishmentId(UUID establishmentId);

    @Query("""
        SELECT a FROM AppointmentJpaEntity a
        WHERE a.professionalId = :professionalId
        AND a.scheduledAt >= :from AND a.endAt <= :to
    """)
    List<AppointmentJpaEntity> findByProfessionalIdAndDateRange(
            @Param("professionalId") UUID professionalId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
        SELECT a FROM AppointmentJpaEntity a
        WHERE a.status = 'CONFIRMED'
        AND a.scheduledAt <= :dateTime
    """)
    List<AppointmentJpaEntity> findConfirmedBefore(@Param("dateTime") LocalDateTime dateTime);
}
