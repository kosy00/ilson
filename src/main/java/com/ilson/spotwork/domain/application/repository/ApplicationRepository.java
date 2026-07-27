package com.ilson.spotwork.domain.application.repository;

import com.ilson.spotwork.domain.application.entity.Application;
import com.ilson.spotwork.domain.application.entity.ApplicationStatus;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 중복 지원 체크
    boolean existsByJobIdAndWorkerId(Long jobId, Long workerId);

    // 내 지원 목록 (WORKER용)
    Slice<Application> findByWorkerIdOrderByCreatedAtDesc(Long workerId, Pageable pageable);

    // 지원자 목록 (EMPLOYER용)
    List<Application> findByJobId(Long jobId);

    // 공고의 수락된 지원자 수 (모집인원 초과 체크용)
//    int countByJobIdAndStatus(Long jobId, ApplicationStatus status);

}