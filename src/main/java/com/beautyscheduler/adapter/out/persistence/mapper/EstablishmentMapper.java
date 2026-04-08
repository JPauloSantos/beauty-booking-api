package com.beautyscheduler.adapter.out.persistence.mapper;

import com.beautyscheduler.adapter.out.persistence.entity.EstablishmentJpaEntity;
import com.beautyscheduler.domain.entity.Establishment;
import com.beautyscheduler.domain.valueobject.Address;

import java.util.ArrayList;

public class EstablishmentMapper {

    private EstablishmentMapper() {}

    public static Establishment toDomain(EstablishmentJpaEntity e) {
        Address address = new Address(e.getStreet(), e.getNumber(), e.getComplement(),
                e.getNeighborhood(), e.getCity(), e.getState(),
                e.getZipCode(), e.getLatitude(), e.getLongitude());
        return new Establishment(e.getId(), e.getName(), e.getDescription(), address,
                new ArrayList<>(), new ArrayList<>(e.getPhotoUrls()),
                e.getOwnerId(), e.isActive(), e.getCreatedAt());
    }

    public static EstablishmentJpaEntity toJpa(Establishment d) {
        EstablishmentJpaEntity e = new EstablishmentJpaEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        if (d.getAddress() != null) {
            e.setStreet(d.getAddress().getStreet());
            e.setNumber(d.getAddress().getNumber());
            e.setComplement(d.getAddress().getComplement());
            e.setNeighborhood(d.getAddress().getNeighborhood());
            e.setCity(d.getAddress().getCity());
            e.setState(d.getAddress().getState());
            e.setZipCode(d.getAddress().getZipCode());
            e.setLatitude(d.getAddress().getLatitude());
            e.setLongitude(d.getAddress().getLongitude());
        }
        e.setPhotoUrls(new ArrayList<>(d.getPhotoUrls()));
        e.setOwnerId(d.getOwnerId());
        e.setActive(d.isActive());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}
