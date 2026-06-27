package com.ilson.spotwork.domain.job.dto;

import jakarta.validation.constraints.*;

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
    @FutureOrPresent(message = "근무 날짜는 오늘 이후여야 합니다.")
    private LocalDate workDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @Min(value = 1, message = "모집 인원은 1명 이상이어야 합니다.")
    private int headcount;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotNull(message = "위도는 필수입니다.")
    @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
    private Double longitude;
}