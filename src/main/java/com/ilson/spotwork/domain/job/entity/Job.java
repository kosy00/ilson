package com.ilson.spotwork.domain.job.entity;

import com.ilson.spotwork.common.entity.BaseEntity;
import com.ilson.spotwork.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "jobs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobCategory category;

    @Column
    private String description;

    @Column(nullable = false)
    private int hourlyWage;

    @Column(nullable = false)
    private LocalDate workDate; // 근무 날짜

    @Column(nullable = false)
    private LocalTime startTime;
    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private int headcount; // 모집인원

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(nullable = false)
    private double latitude; // 위도
    @Column(nullable = false)
    private double longitude; // 경도

    public void close() {
        this.status = JobStatus.CLOSED;
    }

    public void updateInfo(String title, String description, int hourlyWage) {
        this.title = title;
        this.description = description;
        this.hourlyWage = hourlyWage;
    }
}
