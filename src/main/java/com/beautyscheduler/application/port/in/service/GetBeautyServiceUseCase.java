package com.beautyscheduler.application.port.in.service;

import com.beautyscheduler.domain.entity.BeautyService;

import java.util.List;
import java.util.UUID;

public interface GetBeautyServiceUseCase {

    BeautyService findById(UUID id);

    List<BeautyService> findByEstablishment(UUID establishmentId);
}
