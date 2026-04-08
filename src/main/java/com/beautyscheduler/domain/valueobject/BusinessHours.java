package com.beautyscheduler.domain.valueobject;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class BusinessHours {

    private final DayOfWeek dayOfWeek;
    private final LocalTime openTime;
    private final LocalTime closeTime;

    public BusinessHours(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
        if (openTime.isAfter(closeTime)) {
            throw new IllegalArgumentException("Open time must be before close time.");
        }
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public boolean isOpen(LocalTime time) {
        return !time.isBefore(openTime) && !time.isAfter(closeTime);
    }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getOpenTime() { return openTime; }
    public LocalTime getCloseTime() { return closeTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessHours)) return false;
        BusinessHours that = (BusinessHours) o;
        return dayOfWeek == that.dayOfWeek
                && Objects.equals(openTime, that.openTime)
                && Objects.equals(closeTime, that.closeTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, openTime, closeTime);
    }
}
