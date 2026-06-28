package com.ilson.spotwork.domain.job.repository;

import com.ilson.spotwork.domain.job.dto.JobSearchCondition;
import com.ilson.spotwork.domain.job.dto.JobSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobQueryRepository {
    Page<JobSummaryResponse> search(JobSearchCondition cond, Pageable pageable);
}
