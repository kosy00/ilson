package com.ilson.spotwork.domain.application.entity;

public enum ApplicationStatus {
    PENDING,  // 지원 완료, 검토 중..
    ACCEPTED,  // 수락
    REJECTED,  // 거절
    COMPLETED;  // 근무 완료

    public boolean canTransitionTo(ApplicationStatus next) {
        return switch (this) {
            case PENDING -> next == ACCEPTED || next == REJECTED;
            case ACCEPTED -> next == COMPLETED;
            case REJECTED, COMPLETED -> false;
        };
    }
}
