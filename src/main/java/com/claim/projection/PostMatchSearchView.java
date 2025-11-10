package com.claim.projection;

import java.time.LocalDate;

public interface PostMatchSearchView {
    Long getClaimId();
    String getClientClaimId();
    String getPended();             // 'Y'/'N'
    String getInsuredId();
    String getLastName();
    String getFirstName();
    LocalDate getDateOfBirth();     // casted to DATE in SQL
    String getGender();
    String getPolicy();
    String getClaimType();
    String getNetwork();
    LocalDate getClaimReceivedDate(); // casted to DATE in SQL
}