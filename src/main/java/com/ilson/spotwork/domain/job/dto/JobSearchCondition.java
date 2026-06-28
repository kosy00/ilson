package com.ilson.spotwork.domain.job.dto;

import com.ilson.spotwork.domain.job.entity.JobCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchCondition {
    private JobCategory category;
    private LocalDate workDate;
    private Integer minWage;
    private Integer maxWage;
}
