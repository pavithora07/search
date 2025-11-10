package com.claim.controller;


import com.claim.Exception.TooManyRowsException;
import com.claim.dto.PostMatchSearchRequest;
import com.claim.projection.PostMatchDetailView;
import com.claim.projection.PostMatchSearchView;
import com.claim.service.PostMatchService;
import java.util.List;

import com.claim.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/post-match")
@RequiredArgsConstructor
public class PostMatchController {


    private final PostMatchService service;


    @PostMapping("/search")
    public ResponseEntity<Page<PostMatchSearchView>> search(
            @RequestBody PostMatchSearchRequest request,
            @PageableDefault(size = 20) Pageable pageable) throws TooManyRowsException {
        Page<PostMatchSearchView> page = service.search(request, PageUtils.enforceAllowedSizes(pageable, 20, 50, 100, 500));


        HttpHeaders headers = new HttpHeaders();
        if (page.getTotalElements() > service.getMaxAllowedRows()) {
            headers.add("X-Result-Limit-Reached", "true");
        }
        return new ResponseEntity<>(page, headers, HttpStatus.OK);
    }


    @GetMapping("/{claimId}/history")
    public List<PostMatchDetailView> history(@PathVariable("claimId") Long claimId) {
        return service.getHistory(claimId);
    }
}