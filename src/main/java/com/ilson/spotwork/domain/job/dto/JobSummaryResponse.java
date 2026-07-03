package com.ilson.spotwork.domain.job.dto;

import com.ilson.spotwork.domain.job.entity.Job;
import com.ilson.spotwork.domain.job.entity.JobCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class JobSummaryResponse {

    private Long id;
    private String title;
    private JobCategory category;
    private int hourlyWage;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String address;
    private int headcount;

    public static JobSummaryResponse from(Job job) {
        return JobSummaryResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .category(job.getCategory())
                .hourlyWage(job.getHourlyWage())
                .workDate(job.getWorkDate())
                .startTime(job.getStartTime())
                .endTime(job.getEndTime())
                .address(job.getAddress())
                .headcount(job.getHeadcount())
                .build();
    }
}