
package com.claim.projection;

import java.time.LocalDateTime;

public interface PostMatchDetailView {
    Long getClaimId();
    String getCcode();
    String getScenario();
    String getMatchType();
    String getMemberId();
    String getMatchedBy();
    LocalDateTime getActionDate();  // keeps timestamp fidelity
    String getDenialNotes();
    Long getSortField();
}
