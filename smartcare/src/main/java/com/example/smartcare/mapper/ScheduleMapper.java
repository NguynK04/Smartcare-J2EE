package com.example.smartcare.mapper;

import com.example.smartcare.dto.ScheduleResponse;
import com.example.smartcare.entity.Schedule;

public class ScheduleMapper {
    public static ScheduleResponse toResponse(Schedule entity) {
        if (entity == null) {
            return null;
        }
        return ScheduleResponse.builder()
                .id(entity.getId())
                .workDate(entity.getWorkDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .isBooked(entity.isBooked()) // Lấy isBooked từ entity
                .build();
    }
}