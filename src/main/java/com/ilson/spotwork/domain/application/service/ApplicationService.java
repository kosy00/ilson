package com.ilson.spotwork.domain.application.service;

import com.ilson.spotwork.common.exception.CustomException;
import com.ilson.spotwork.common.exception.ErrorCode;
import com.ilson.spotwork.domain.application.dto.ApplicationResponse;
import com.ilson.spotwork.domain.application.entity.Application;
import com.ilson.spotwork.domain.application.entity.ApplicationStatus;
import com.ilson.spotwork.domain.application.repository.ApplicationRepository;
import com.ilson.spotwork.domain.job.entity.Job;
import com.ilson.spotwork.domain.job.entity.JobStatus;
import com.ilson.spotwork.domain.job.repository.JobRepository;
import com.ilson.spotwork.domain.user.entity.User;
import com.ilson.spotwork.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    // 지원하기
    @Transactional
    public ApplicationResponse apply(Long workerId, Long jobId) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 중복 지원 체크
        if (applicationRepository.existsByJobIdAndWorkerId(jobId, workerId)) {
            throw new CustomException(ErrorCode.DUPLICATE_APPLICATION);
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));

        // 공고 상태 체크
        if (job.getStatus() != JobStatus.OPEN) {
            throw new CustomException(ErrorCode.JOB_CLOSED);
        }

        // 모집인원 초과 체크
        if (job.getAcceptedCount() >= job.getHeadcount()) {
            throw new CustomException(ErrorCode.JOB_CLOSED);
        }

        try {
            Application application = Application.builder()
                    .job(job)
                    .worker(worker)
                    .status(ApplicationStatus.PENDING)
                    .build();

            Application saved = applicationRepository.save(application);
            log.info("[Application] 지원 완료 applicationId = {}, jobId = {}, workerId = {}",
                    saved.getId(), jobId, workerId);
            return ApplicationResponse.from(saved);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        }
    }

    // 수락 (EMPLOYER)
    @Transactional
    public ApplicationResponse accept(Long employerId, Long applicationId) {
        try {
            Application application = getApplicationWithEmployerCheck(employerId, applicationId);
            Job job = application.getJob();

            if (job.getAcceptedCount() >= application.getJob().getHeadcount()) {
                throw new CustomException(ErrorCode.JOB_CLOSED);
            }
            application.transitionTo(ApplicationStatus.ACCEPTED);
            job.increaseAcceptedCount();

            log.info("[Application] 수락 완료 applicationId = {}", applicationId);
            return ApplicationResponse.from(application);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        }
    }

    // 거절 (EMPLOYER)
    @Transactional
    public ApplicationResponse reject(Long employerId, Long applicationId) {
        Application application = getApplicationWithEmployerCheck(employerId, applicationId);
        application.transitionTo(ApplicationStatus.REJECTED);
        log.info("[Application] 거절 완료 applicationId = {}", applicationId);
        return ApplicationResponse.from(application);
    }

    // 근무 완료 처리 (EMPLOYER)
    @Transactional
    public ApplicationResponse complete(Long employerId, Long applicationId) {
        Application application = getApplicationWithEmployerCheck(employerId, applicationId);
        application.transitionTo(ApplicationStatus.COMPLETED);
        log.info("[Application] 근무 완료 처리 applicationId = {}", applicationId);
        return ApplicationResponse.from(application);
    }

    // 지원 취소 (WORKER, PENDING 상태만 가능)
    @Transactional
    public void cancel(Long workerId, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getWorker().getId().equals(workerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_STATUS_TRANSITION);
        }

        applicationRepository.delete(application);
        log.info("[Application] 지원 취소 applicationId = {}", applicationId);
    }

    // 내 지원 목록 (WORKER)
    public Slice<ApplicationResponse> getMyApplications(Long workerId, Pageable pageable) {
        return applicationRepository.findByWorkerIdOrderByCreatedAtDesc(workerId, pageable)
                .map(ApplicationResponse::from);
    }

    // 지원자 목록 (EMPLOYER)
    public List<ApplicationResponse> getApplicants(Long employerId, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return applicationRepository.findByJobId(jobId).stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    // 공통 - 권한 체크 및 Application 조회
    private Application getApplicationWithEmployerCheck(Long employerId, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getJob().getEmployer().getId().equals(employerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return application;
    }
}