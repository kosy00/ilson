package com.ilson.spotwork.domain.application.dto;

import com.ilson.spotwork.domain.application.entity.Application;
import com.ilson.spotwork.domain.application.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long workerId;
    private String workerName;
    private ApplicationStatus status;
    private LocalDateTime createdAt;

    public static ApplicationResponse from(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .workerId(application.getWorker().getId())
                .workerName(application.getWorker().getNickname())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }
}