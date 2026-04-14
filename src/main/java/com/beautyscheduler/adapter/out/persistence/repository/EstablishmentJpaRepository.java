package com.beautyscheduler.adapter.out.persistence.repository;

import com.beautyscheduler.adapter.out.persistence.entity.EstablishmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EstablishmentJpaRepository extends JpaRepository<EstablishmentJpaEntity, UUID> {
    List<EstablishmentJpaEntity> findByOwnerId(UUID ownerId);

    @Query("""
        SELECT DISTINCT e FROM EstablishmentJpaEntity e
        WHERE e.active = true
        AND (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:city IS NULL OR LOWER(e.city) LIKE LOWER(CONCAT('%', :city, '%')))
        AND (:serviceName IS NULL OR EXISTS (
            SELECT s FROM BeautyServiceJpaEntity s
            WHERE s.establishmentId = e.id
            AND LOWER(s.name) LIKE LOWER(CONCAT('%', :serviceName, '%'))
        ))
        AND (:minPrice IS NULL OR EXISTS (
            SELECT s FROM BeautyServiceJpaEntity s
            WHERE s.establishmentId = e.id AND s.price >= :minPrice
        ))
        AND (:maxPrice IS NULL OR EXISTS (
            SELECT s FROM BeautyServiceJpaEntity s
            WHERE s.establishmentId = e.id AND s.price <= :maxPrice
        ))
    """)
    List<EstablishmentJpaEntity> search(
            @Param("name") String name,
            @Param("city") String city,
            @Param("serviceName") String serviceName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}
