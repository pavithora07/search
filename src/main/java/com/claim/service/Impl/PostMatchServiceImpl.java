package com.claim.service.Impl;

import com.claim.Exception.TooManyRowsException;
import com.claim.dto.PostMatchSearchRequest;
import com.claim.projection.PostMatchDetailView;
import com.claim.projection.PostMatchSearchView;
import com.claim.repository.PostMatchRepository;
import com.claim.service.PostMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMatchServiceImpl implements PostMatchService {

    private static final int MAX_ALLOWED_ROWS = 1000;

    private final PostMatchRepository repository;

    @Override
    public Page<PostMatchSearchView> search(PostMatchSearchRequest r, Pageable pageable)
            throws TooManyRowsException {

        log.info("PostMatch search initiated | request={}", r);

        validateSearchCriteria(r);
        validateDateConstraints(r);

        Page<PostMatchSearchView> page = repository.search(
                r.getMpiClaimId(),
                normalize(r.getClientClaimId()),
                normalize(r.getInsuredId()),
                normalize(r.getLastName()),
                normalize(r.getFirstName()),
                r.getDateOfBirth(),
                normalizeExact(r.getGender()),
                normalize(r.getPolicy()),
                normalize(r.getCcode()),
                normalizeExact(r.getNetwork()),
                normalize(r.getMatchedBy()),
                normalizeExact(r.getMatchType()),
                normalizeExact(r.getClaimType()),
                r.getMatchDateFrom(),
                r.getMatchDateTo(),
                pageable
        );

        long total = page.getTotalElements();
        log.info("PostMatch search completed | total={}", total);

        if (total > MAX_ALLOWED_ROWS) {
            log.warn("Row cap exceeded: {} > {}", total, MAX_ALLOWED_ROWS);
            throw new TooManyRowsException(
                    "Warning: You’ve reached the maximum limit of 1,000 rows. Please refine your search criteria."
            );
        }

        return page;
    }

    @Override
    public List<PostMatchDetailView> getHistory(Long claimId) {
        if (claimId == null) {
            throw new IllegalArgumentException("claimId cannot be null");
        }

        log.info("Fetching PostMatch history | claimId={}", claimId);
        return repository.fetchDetails(claimId);
    }

    @Override
    public int getMaxAllowedRows() {
        return MAX_ALLOWED_ROWS;
    }

    private void validateSearchCriteria(PostMatchSearchRequest r) {
        boolean any =
                r.getMpiClaimId() != null ||
                        has(r.getClientClaimId()) ||
                        has(r.getInsuredId()) ||
                        has(r.getLastName()) ||
                        has(r.getFirstName()) ||
                        r.getDateOfBirth() != null ||
                        has(r.getGender()) ||
                        has(r.getPolicy()) ||
                        has(r.getCcode()) ||
                        has(r.getNetwork()) ||
                        has(r.getMatchedBy()) ||
                        has(r.getMatchType()) ||
                        has(r.getClaimType()) ||
                        r.getMatchDateFrom() != null ||
                        r.getMatchDateTo() != null;

        if (!any) {
            throw new IllegalArgumentException("At least one search criterion is required.");
        }
    }

    private void validateDateConstraints(PostMatchSearchRequest r) {
        LocalDate from = r.getMatchDateFrom();
        LocalDate to   = r.getMatchDateTo();

        if (from != null && to != null && to.isAfter(from.plusMonths(3))) {
            boolean hasIdFilter =
                    r.getMpiClaimId() != null ||
                            has(r.getClientClaimId()) ||
                            has(r.getInsuredId());

            if (!hasIdFilter) {
                throw new IllegalArgumentException(
                        "If match date range exceeds 3 months, provide mpiClaimId, clientClaimId, or insuredId."
                );
            }
        }
    }

    private static boolean has(String s) {
        return s != null && !s.isBlank();
    }

    private static String normalize(String s) {
        return has(s) ? s.trim() : null;
    }

    private static String normalizeExact(String s) {
        return has(s) ? s.trim() : null;
    }
}
