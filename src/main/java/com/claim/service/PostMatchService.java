package com.claim.service;

import com.claim.dto.PostMatchSearchRequest;
import com.claim.dto.PostMatchSearchResponse;
import com.claim.projection.PostMatchDetailView;

import java.util.List;

public interface PostMatchService {

    PostMatchSearchResponse search(PostMatchSearchRequest request,
                                   int page,
                                   int size);

    List<PostMatchDetailView> getHistory(Long claimId);

    int getMaxAllowedRows();
}
