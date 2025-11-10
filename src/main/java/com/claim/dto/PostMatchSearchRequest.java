package com.claim.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Builder
@Data
public class PostMatchSearchRequest {

    private Long mpiClaimId;          // EDP/MPI Claim Id
    private String clientClaimId;
    private String insuredId;

    private String lastName;
    private String firstName;

    private LocalDate dob;
    private String gender;
    private String policy;

    private String ccode;             // MATCHED_CLIENT_CODE
    private String network;           // CLAIM_STREAM

    private String matchedBy;
    private String matchType;
    private String claimType;

    private LocalDate dateFrom;
    private LocalDate dateTo;
}
