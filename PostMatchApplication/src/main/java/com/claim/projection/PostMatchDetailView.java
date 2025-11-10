package com.claim.projection;

import java.time.LocalDateTime;

public interface PostMatchDetailView {
    Long getClaimId();
    String getCcode(); // cma.MATCHED_CLIENT_CODE
    String getScenario(); // r.SCENARIO
    String getMatchType(); // r.MATCH_TYPE
    String getMemberId(); // cma.MATCHED_MEMBER_ID
    String getMatchedBy(); // cma.MODIFIED_BY
    LocalDateTime getActionDate(); // cma.CLAIM_RECEIVED_AT
    String getDenialNotes(); // pn.NOTETEXT
    Long getSortField(); // cma.CLIENT_MATCH_ACTION_ID or pn.NOTESEQ
}