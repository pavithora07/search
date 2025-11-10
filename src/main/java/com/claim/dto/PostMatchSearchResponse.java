
package com.claim.dto;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PostMatchSearchResponse {
    @Singular
    List<PostMatchSearchRowDto> items;

    int page;               // 0-based
    int size;               // 20/50/100/500
    long totalElements;
    int totalPages;

    boolean reachedLimit;   // true when we cap at 1000
    String warning;         // “You’ve reached the maximum limit of 1,000 rows…”

    // helpful for your UI filters/pill chips
    List<Integer> allowedPageSizes; // [20,50,100,500]
}
