package com.claim.controller;

import com.claim.dto.PostMatchSearchRequest;
import com.claim.dto.PostMatchSearchResponse;
import com.claim.service.PostMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post-match")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PostMatchController {

    private final PostMatchService service;

    /**
     * POST /post-match/_search?page=0&size=20
     */
    @PostMapping("/_search")
    public ResponseEntity<PostMatchSearchResponse> search(
            @Valid @RequestBody PostMatchSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("PostMatchController: search request received page={}, size={}", page, size);

        PostMatchSearchResponse response = service.search(request, page,size);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /post-match/{claimId}/history
     */
    @GetMapping("/{claimId}/history")
    public ResponseEntity<?> history(@PathVariable Long claimId) {
        log.info("PostMatchController: history for claim {}", claimId);
        return ResponseEntity.ok(service.getHistory(claimId));
    }
}
