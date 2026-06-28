package com.ilson.spotwork.domain.job.repository;

import com.ilson.spotwork.domain.job.dto.JobSearchCondition;
import com.ilson.spotwork.domain.job.dto.JobSummaryResponse;
import com.ilson.spotwork.domain.job.entity.JobCategory;
import com.ilson.spotwork.domain.job.entity.JobStatus;
import com.ilson.spotwork.domain.job.entity.QJob;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JobQueryRepositoryImpl implements JobQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QJob job = QJob.job;

    @Override
    public Page<JobSummaryResponse> search(JobSearchCondition condition, Pageable pageable) {
        List<JobSummaryResponse> content = queryFactory
                .selectFrom(job)
                .where(
                        onlyOpen(),
                        eqCategory(condition.getCategory()),
                        eqWorkDate(condition.getWorkDate()),
                        betweenWage(condition.getMinWage(), condition.getMaxWage())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(JobSummaryResponse::from)
                .toList();

        Long total = queryFactory
                .select(job.count())
                .from(job)
                .where(
                        onlyOpen(),
                        eqCategory(condition.getCategory()),
                        eqWorkDate(condition.getWorkDate()),
                        betweenWage(condition.getMinWage(), condition.getMaxWage())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression onlyOpen() {
        return job.status.eq(JobStatus.OPEN);
    }

    private BooleanExpression eqCategory(JobCategory category) {
        return category == null ? null : job.category.eq(category);
    }

    private BooleanExpression eqWorkDate(LocalDate workDate) {
        return workDate == null ? null : job.workDate.eq(workDate);
    }

    private BooleanExpression betweenWage(Integer minWage, Integer maxWage) {
        if (minWage == null && maxWage == null) return null;
        if (minWage == null) return job.hourlyWage.loe(maxWage);
        if (maxWage == null) return job.hourlyWage.goe(minWage);
        return job.hourlyWage.between(minWage, maxWage);
    }
}