package com.ilson.spotwork.domain.application.controller;

import com.ilson.spotwork.common.response.ApiResponse;
import com.ilson.spotwork.domain.application.dto.ApplicationResponse;
import com.ilson.spotwork.domain.application.service.ApplicationService;
import com.ilson.spotwork.infra.security.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Application", description = "지원 API")
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "지원하기 (WORKER)")
    @PostMapping("/jobs/{jobId}")
    @PreAuthorize(("hasRole('WORKER')"))
    public ApiResponse<ApplicationResponse> apply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long jobId) {
        return ApiResponse.success(applicationService.apply(userDetails.getUserId(), jobId));
    }

    @Operation(summary = "지원 수락 (EMPLOYER)")
    @PatchMapping("/{applicationId}/accept")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<ApplicationResponse> accept(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId) {
        return ApiResponse.success(applicationService.accept(userDetails.getUserId(), applicationId));
    }

    @Operation(summary = "지원 거절 (EMPLOYER)")
    @PatchMapping("/{applicationId}/reject")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<ApplicationResponse> reject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId) {
        return ApiResponse.success(applicationService.reject(userDetails.getUserId(), applicationId));
    }

    @Operation(summary = "근무 완료 처리 (EMPLOYER)")
    @PatchMapping("/{applicationId}/complete")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<ApplicationResponse> complete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId) {
        return ApiResponse.success(applicationService.complete(userDetails.getUserId(), applicationId));
    }

    @Operation(summary = "지원 취소 (WORKER)")
    @DeleteMapping("/{applicationId}")
    @PreAuthorize("hasRole('WORKER')")
    public ApiResponse<Void> cancel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long applicationId) {
        applicationService.cancel(userDetails.getUserId(), applicationId);
        return ApiResponse.success();
    }

    @Operation(summary = "내 지원 목록 (WORKER)")
    @GetMapping("/me")
    @PreAuthorize("hasRole('WORKER')")
    public ApiResponse<Slice<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.success(applicationService.getMyApplications(userDetails.getUserId(), pageable));
    }

    @Operation(summary = "지원자 목록 (EMPLOYER)")
    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<List<ApplicationResponse>> getApplicants(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long jobId) {
        return ApiResponse.success(applicationService.getApplicants(userDetails.getUserId(), jobId));
    }
}
