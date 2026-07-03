package com.ilson.spotwork.domain.job.repository;

import com.ilson.spotwork.domain.job.entity.Job;
import com.ilson.spotwork.domain.job.entity.JobStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long>, JobQueryRepository {
    // 공고 목록 (OPEN 상태만)
    Slice<Job> findByStatus(JobStatus status, Pageable pageable);

    // 자동 마감용 (날짜 지난 OPEN 공고)
    List<Job> findByStatusAndWorkDateBefore(JobStatus status, LocalDate date);

    // 내 공고 목록
    Slice<Job> findByEmployerId(Long employerId, Pageable pageable);
}
