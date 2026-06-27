package com.ilson.spotwork.domain.job.dto;

import com.ilson.spotwork.domain.job.entity.JobCategory;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class JobCreateRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "업종은 필수입니다.")
    private JobCategory category;

    @Min(value = 10030, message = "최저시급 이상이어야 합니다.")
    private int hourlyWage;

    @NotNull(message = "근무 날짜는 필수입니다.")
    @FutureOrPresent(message = "근무 날짜는 오늘 이후여야 합니다.")
    private LocalDate workDate;

    @NotNull(message = "시작 시간은 필수입니다.")
    private LocalTime startTime;

    @NotNull(message = "종료 시간은 필수입니다.")
    private LocalTime endTime;

    @AssertTrue(message = "종료 시간은 시작 시간보다 늦어야 합니다.")
    private boolean isValidTimeRange() {
        return startTime == null || endTime == null || endTime.isAfter(startTime);
    }

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