
package com.claim.dto;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;

@Value
@Builder
public class PostMatchSearchRowDto {
    Long claimId;
    String clientClaimId;
    String pended;
    String insuredId;
    String lastName;
    String firstName;
    LocalDate dateOfBirth;
    String gender;
    String policy;
    String claimType;
    String network;
    LocalDate claimReceivedDate;
}
