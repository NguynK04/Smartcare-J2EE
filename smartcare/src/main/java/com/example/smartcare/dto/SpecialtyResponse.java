package com.example.smartcare.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SpecialtyResponse {
    private Long id;
    private String name;
    private String description;
}