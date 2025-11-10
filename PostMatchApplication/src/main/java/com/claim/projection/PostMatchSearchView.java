package com.claim.projection;

import java.time.LocalDate;

public interface PostMatchSearchView {
    Long getClaimId(); // c.MPI_CLAIM_ID
    String getClientClaimId(); // c.CLIENT_CLAIM_ID
    String getPended(); // NVL(cma.USER_PEND,'N')
    String getInsuredId(); // em.MEMBER_ID
    String getLastName(); // em.LAST_NAME
    String getFirstName(); // em.FIRST_NAME
    LocalDate getDateOfBirth(); // em.DT_OF_BIRTH
    String getGender(); // em.GENDER
    String getPolicy(); // em.POLICY
    String getClaimType(); // c.CLAIM_TYPE
    String getNetwork(); // c.CLAIM_STREAM
    LocalDate getClaimReceivedDate(); // TRUNC(c.CLIENT_RECEIVED_DATE)
}