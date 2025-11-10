package com.claim.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "CLAIM")
@Data
public class Claim {

    @Id
    @Column(name = "MPI_CLAIM_ID")
    private Long mpiClaimId;

    @Column(name = "CLIENT_CLAIM_ID")
    private String clientClaimId;

    @Column(name = "CLAIM_STREAM")
    private String claimStream;

    @Column(name = "CLAIM_TYPE")
    private String claimType;

    @Column(name = "CLIENT_RECEIVED_DATE")
    private LocalDate clientReceivedDate;

    @Column(name = "LOAD_DATE")
    private LocalDate loadDate;      // ✅ REQUIRED for post-match

    @Column(name = "GROUP_ID")
    private String groupId;

    @Column(name = "ALTERNATE_ACCT_ID")
    private String alternateAcctId;

    @Column(name = "STATUS_CD")
    private Long statusCode;
}
