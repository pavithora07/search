package com.claim.service.Impl;

import com.claim.dto.PostMatchSearchRequest;
import com.claim.dto.PostMatchSearchResponse;
import com.claim.dto.PostMatchSearchRowDto;
import com.claim.projection.PostMatchDetailView;
import com.claim.projection.PostMatchSearchView;
import com.claim.repository.PostMatchRepository;
import com.claim.service.PostMatchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMatchServiceImpl implements PostMatchService {

    private static final int MAX_ALLOWED_ROWS = 1000;
    private static final List<Integer> ALLOWED_PAGE_SIZES = List.of(20, 50, 100, 500);

    private final PostMatchRepository repository;

    @Override
    public PostMatchSearchResponse search(PostMatchSearchRequest r, int page, int size) {

        log.info("PostMatch search started | page={}, size={}, request={}", page, size, r);

        // enforce allowed page sizes
        if (!ALLOWED_PAGE_SIZES.contains(size)) {
            size = 20;
        }

        validateCriteria(r);
        validateDateRangeRule(r);

        Pageable pageable = PageRequest.of(page, size);

        Page<PostMatchSearchView> pageResult = repository.search(
                r.getMpiClaimId(),
                r.getClientClaimId(),
                normalize(r.getInsuredId()),
                normalize(r.getLastName()),
                normalize(r.getFirstName()),
                r.getDob(),
                normalizeExact(r.getGender()),
                normalize(r.getPolicy()),
                normalizeExact(r.getCcode()),
                normalizeExact(r.getNetwork()),
                normalize(r.getMatchedBy()),
                normalizeExact(r.getMatchType()),
                normalizeExact(r.getClaimType()),
                r.getDateFrom(),
                r.getDateTo(),
                pageable
        );

        long total = pageResult.getTotalElements();
        boolean reachedLimit = total > MAX_ALLOWED_ROWS;
        long effectiveTotal = reachedLimit ? MAX_ALLOWED_ROWS : total;

        String warning = reachedLimit
                ? "Warning: You’ve reached the maximum limit of 1,000 rows. Please refine your search criteria."
                : null;

        return PostMatchSearchResponse.builder()
                .items(pageResult.getContent().stream()
                        .map(v -> PostMatchSearchRowDto.builder()
                                .claimId(v.getClaimId())
                                .clientClaimId(v.getClientClaimId())
                                .pended(v.getPended())
                                .insuredId(v.getInsuredId())
                                .lastName(v.getLastName())
                                .firstName(v.getFirstName())
                                .dateOfBirth(v.getDateOfBirth())
                                .gender(v.getGender())
                                .policy(v.getPolicy())
                                .claimType(v.getClaimType())
                                .network(v.getNetwork())
                                .claimReceivedDate(v.getClaimReceivedDate())
                                .build()
                        ).toList())
                .page(page)
                .size(size)
                .totalElements(effectiveTotal)
                .totalPages((int) Math.ceil((double) effectiveTotal / size))
                .reachedLimit(reachedLimit)
                .warning(warning)
                .allowedPageSizes(ALLOWED_PAGE_SIZES)
                .build();
    }

    @Override
    public List<PostMatchDetailView> getHistory(Long claimId) {
        if (claimId == null) {
            throw new IllegalArgumentException("claimId cannot be null");
        }
        return repository.fetchDetails(claimId);
    }

    @Override
    public int getMaxAllowedRows() {
        return MAX_ALLOWED_ROWS;
    }

    // --------------------- Validation ------------------------

    private void validateCriteria(PostMatchSearchRequest r) {
        boolean any =
                r.getMpiClaimId() != null ||
                        r.getClientClaimId() != null ||
                        has(r.getInsuredId()) ||
                        has(r.getLastName()) ||
                        has(r.getFirstName()) ||
                        r.getDob()!= null ||
                        has(r.getGender()) ||
                        has(r.getPolicy()) ||
                        has(r.getCcode()) ||
                        has(r.getNetwork()) ||
                        has(r.getMatchedBy()) ||
                        has(r.getMatchType()) ||
                        has(r.getClaimType()) ||
                        r.getDateFrom() != null ||
                        r.getDateTo() != null;

        if (!any) {
            throw new IllegalArgumentException("Provide at least one search criterion.");
        }
    }

    private void validateDateRangeRule(PostMatchSearchRequest r) {
        LocalDate from = r.getDateFrom();
        LocalDate to = r.getDateTo();

        if (from == null || to == null) return;

        if (to.isAfter(from.plusMonths(3))) {

            int additionalFilters = 0;

            additionalFilters += (r.getMpiClaimId() != null ? 1 : 0);
            additionalFilters += (r.getClientClaimId() != null ? 1 : 0);
            additionalFilters += (has(r.getInsuredId()) ? 1 : 0);
            additionalFilters += (has(r.getLastName()) ? 1 : 0);
            additionalFilters += (has(r.getFirstName()) ? 1 : 0);
            additionalFilters += (has(r.getGender()) ? 1 : 0);
            additionalFilters += (has(r.getPolicy()) ? 1 : 0);
            additionalFilters += (has(r.getCcode()) ? 1 : 0);
            additionalFilters += (has(r.getNetwork()) ? 1 : 0);
            additionalFilters += (has(r.getMatchedBy()) ? 1 : 0);
            additionalFilters += (has(r.getMatchType()) ? 1 : 0);
            additionalFilters += (has(r.getClaimType()) ? 1 : 0);

            if (additionalFilters < 1) {
                throw new IllegalArgumentException(
                        "If match date range exceeds 3 months, provide at least one additional non-date filter."
                );
            }
        }
    }

    // --------------------- Helpers ------------------------

    private static boolean has(String s) {
        return s != null && !s.isBlank();
    }

    private static String normalize(String s) {
        return has(s) ? s.trim() : null;
    }

    private static String normalizeExact(String s) {
        return normalize(s);
    }
}
