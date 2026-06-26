package com.ilson.spotwork.domain.job.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class JobUpdateRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String description;

    @Min(value = 10030, message = "최저시급 이상이어야 합니다.")
    private int hourlyWage;

    @NotNull
    private LocalDate workDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @Min(value = 1, message = "모집 인원은 1명 이상이어야 합니다.")
    private int headcount;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;
}