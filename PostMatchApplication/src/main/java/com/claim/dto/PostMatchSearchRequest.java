package com.claim.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class PostMatchSearchRequest {
    private Long mpiClaimId; // aka EDP_CLAIM_ID in legacy
    private String clientClaimId;
    private String insuredId; // memberId
    private String lastName;
    private String firstName;
    private LocalDate dateOfBirth;
    private String gender;
    private String policy;
    private String ccode;
    private String network; // claimStream (HEOS/ALC)
    private String matchedBy;
    private String matchType; // from MATCH_RULES.MATCH_TYPE
    private String claimType; // optional (HCFA/UB)
    private LocalDate matchDateFrom; // inclusive
    private LocalDate matchDateTo; // inclusive

}