package com.ilson.spotwork.domain.job.dto;

import com.ilson.spotwork.domain.job.entity.Job;
import com.ilson.spotwork.domain.job.entity.JobCategory;
import com.ilson.spotwork.domain.job.entity.JobStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class JobResponseDto {

    private Long id;
    private String title;
    private String description;
    private JobCategory category;
    private int hourlyWage;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int headcount;
    private String address;
    private double latitude;
    private double longitude;
    private JobStatus status;
    private EmployerInfo employer;

    // 사업자 정보 (중첩 DTO)
    @Getter
    @Builder
    public static class EmployerInfo {
        private Long id;
        private String nickname;
        private Double avgRating;
    }

    public static JobResponseDto from(Job job) {
        return JobResponseDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .category(job.getCategory())
                .hourlyWage(job.getHourlyWage())
                .workDate(job.getWorkDate())
                .startTime(job.getStartTime())
                .endTime(job.getEndTime())
                .headcount(job.getHeadcount())
                .address(job.getAddress())
                .latitude(job.getLatitude())
                .longitude(job.getLongitude())
                .status(job.getStatus())
                .employer(EmployerInfo.builder()
                        .id(job.getEmployer().getId())
                        .nickname(job.getEmployer().getNickname())
                        .avgRating(job.getEmployer().getAvgRating() != null
                                ? job.getEmployer().getAvgRating().doubleValue()
                                : null)
                        .build())
                .build();
    }
}