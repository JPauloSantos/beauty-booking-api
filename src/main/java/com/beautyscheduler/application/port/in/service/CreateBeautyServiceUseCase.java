package com.beautyscheduler.application.port.in.service;

import com.beautyscheduler.domain.entity.BeautyService;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateBeautyServiceUseCase {

    record Command(String name, String description, int durationMinutes,
                   BigDecimal price, UUID establishmentId) {}

    BeautyService execute(Command command);
}
