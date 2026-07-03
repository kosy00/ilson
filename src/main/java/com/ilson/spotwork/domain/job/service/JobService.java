package com.ilson.spotwork.domain.job.service;

import com.ilson.spotwork.common.exception.CustomException;
import com.ilson.spotwork.common.exception.ErrorCode;
import com.ilson.spotwork.domain.job.dto.*;
import com.ilson.spotwork.domain.job.entity.Job;
import com.ilson.spotwork.domain.job.entity.JobStatus;
import com.ilson.spotwork.domain.job.repository.JobRepository;
import com.ilson.spotwork.domain.user.entity.Role;
import com.ilson.spotwork.domain.user.entity.User;
import com.ilson.spotwork.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    // 공고 등록
    @Transactional
    public JobResponseDto register(Long userId, JobCreateRequestDto request) {
        User employer = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (employer.getRole() != Role.EMPLOYER) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .hourlyWage(request.getHourlyWage())
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .headcount(request.getHeadcount())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(JobStatus.OPEN)
                .employer(employer)
                .build();
        Job saved = jobRepository.save(job);
        log.info("[Job] 공고 등록 완료 jobId = {}", saved.getId());
        return JobResponseDto.from(saved);
    }

    //공고 상세 조회
    public JobResponseDto getDetail(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        return JobResponseDto.from(job);
    }

    //공고 수정
    @Transactional
    public JobResponseDto update(Long userId, Long jobId, JobUpdateRequestDto request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));

        //본인 공고인지 검증
        if (!job.getEmployer().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // OPEN 상태인 공고만 수정 가능
        if (!job.getStatus().equals(JobStatus.OPEN)) {
            throw new CustomException(ErrorCode.JOB_CLOSED);
        }

        job.updateInfo(
                request.getTitle(),
                request.getDescription(),
                request.getHourlyWage(),
                request.getWorkDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getHeadcount(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude()
        );

        Job saved = jobRepository.save(job);
        log.info("[Job] 공고 수정 완료 jobId = {}", job.getId());
        return JobResponseDto.from(saved);
    }

    // 내 공고 목록 조회
    public Slice<JobResponseDto> getMyJobs(Long userId, Pageable pageable) {
        return jobRepository.findByEmployerId(userId, pageable)
                .map(JobResponseDto::from);
    }

    // 공고 삭제
    @Transactional
    public void delete(Long userId, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));

        if (!job.getEmployer().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        jobRepository.delete(job);
        log.info("[Job] 공고 삭제 완료 jobId = {}", jobId);
    }

    // 공고 마감(스케쥴러에서 호출)
    @Transactional
    public void closeJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        job.close();
        log.info("[Job] 공고 마감 처리됨 jobId = {}", jobId);
    }

    // 공고 목록 조회 (검색/필터링)
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> search(JobSearchCondition cond, Pageable pageable) {
        if ((cond.getMinWage() != null && cond.getMinWage() < 0)
                || (cond.getMaxWage() != null && cond.getMaxWage() < 0)
                || (cond.getMinWage() != null && cond.getMaxWage() != null)
                && cond.getMinWage() > cond.getMaxWage()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        return jobRepository.search(cond, pageable);
    }
}
