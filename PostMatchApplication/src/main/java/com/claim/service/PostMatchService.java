package com.claim.service;

import com.claim.Exception.TooManyRowsException;
import com.claim.dto.PostMatchSearchRequest;
import com.claim.projection.PostMatchDetailView;
import com.claim.projection.PostMatchSearchView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PostMatchService {
    Page<PostMatchSearchView> search(PostMatchSearchRequest request, Pageable pageable) throws TooManyRowsException;
    List<PostMatchDetailView> getHistory(Long claimId);
    int getMaxAllowedRows();
}