package com.ilson.spotwork.domain.application.entity;

import com.ilson.spotwork.common.entity.BaseEntity;
import com.ilson.spotwork.common.exception.CustomException;
import com.ilson.spotwork.common.exception.ErrorCode;
import com.ilson.spotwork.domain.job.entity.Job;
import com.ilson.spotwork.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "worker_id"}))
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    public void transitionTo(ApplicationStatus next) {
        if (!this.status.canTransitionTo(next)) {
            throw new CustomException(ErrorCode.INVALID_APPLICATION_STATUS);
        }
        this.status = next;
    }
}