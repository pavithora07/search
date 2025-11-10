package com.claim.repository;

import com.claim.entity.Claim;
import com.claim.projection.PostMatchDetailView;
import com.claim.projection.PostMatchSearchView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostMatchRepository extends JpaRepository<Claim, Long> {

    // ================= POST MATCH SEARCH =================
    @Query(
            value = """
            SELECT
                c.MPI_CLAIM_ID                               AS claimId,
                c.CLIENT_CLAIM_ID                            AS clientClaimId,
                IFNULL(cma.USER_PEND, 'N')                   AS pended,
                em.MEMBER_ID                                 AS insuredId,
                em.LAST_NAME                                 AS lastName,
                em.FIRST_NAME                                AS firstName,
                DATE(em.DT_OF_BIRTH)                         AS dateOfBirth,
                em.GENDER                                    AS gender,
                em.POLICY                                    AS policy,
                c.CLAIM_TYPE                                 AS claimType,
                c.CLAIM_STREAM                               AS network,
                DATE(c.CLIENT_RECEIVED_DATE)                 AS claimReceivedDate
            FROM CLAIM c
            JOIN CLIENT_MATCH_ACTION cma
                ON c.MPI_CLAIM_ID = cma.MPI_CLAIM_ID          -- <== FIX: MySQL column
            LEFT JOIN ECM_ELIG_MEMBER em
                ON cma.ECM_ELIG_MEMBER_ID = em.ELIG_MEMBER_ID
            LEFT JOIN MATCH_RULES r
                ON cma.RULE_CODE = r.RULE_CODE
            WHERE 1=1
              AND (:mpiClaimId  IS NULL OR c.MPI_CLAIM_ID = :mpiClaimId)
              AND (:clientClaimId IS NULL OR UPPER(c.CLIENT_CLAIM_ID) LIKE CONCAT(UPPER(:clientClaimId), '%'))
              AND (:insuredId   IS NULL OR UPPER(em.MEMBER_ID)       LIKE CONCAT(UPPER(:insuredId), '%'))
              AND (:lastName    IS NULL OR UPPER(em.LAST_NAME)       LIKE CONCAT(UPPER(:lastName), '%'))
              AND (:firstName   IS NULL OR UPPER(em.FIRST_NAME)      LIKE CONCAT(UPPER(:firstName), '%'))
              AND (:dob         IS NULL OR DATE(em.DT_OF_BIRTH)      = :dob)
              AND (:gender      IS NULL OR UPPER(em.GENDER)          = UPPER(:gender))
              AND (:policy      IS NULL OR UPPER(em.POLICY)          LIKE CONCAT(UPPER(:policy), '%'))
              AND (:ccode       IS NULL OR UPPER(em.CCODE)           LIKE CONCAT(UPPER(:ccode), '%'))
              AND (:network     IS NULL OR UPPER(c.CLAIM_STREAM)     LIKE CONCAT(UPPER(:network), '%'))
              AND (:matchedBy   IS NULL OR UPPER(cma.MODIFIED_BY)    LIKE CONCAT(UPPER(:matchedBy), '%'))
              AND (:matchType   IS NULL OR UPPER(r.MATCH_TYPE)       = UPPER(:matchType))
              AND (:claimType   IS NULL OR UPPER(c.CLAIM_TYPE)       = UPPER(:claimType))
              AND (:dateFrom    IS NULL OR DATE(c.LOAD_DATE)         >= :dateFrom)
              AND (:dateTo      IS NULL OR DATE(c.LOAD_DATE)         <= :dateTo)
            ORDER BY c.LOAD_DATE DESC, c.MPI_CLAIM_ID DESC
            """,
            countQuery = """
            SELECT COUNT(1)
            FROM CLAIM c
            JOIN CLIENT_MATCH_ACTION cma
                ON c.MPI_CLAIM_ID = cma.MPI_CLAIM_ID
            LEFT JOIN ECM_ELIG_MEMBER em
                ON cma.ECM_ELIG_MEMBER_ID = em.ELIG_MEMBER_ID
            LEFT JOIN MATCH_RULES r
                ON cma.RULE_CODE = r.RULE_CODE
            WHERE 1=1
              AND (:mpiClaimId  IS NULL OR c.MPI_CLAIM_ID = :mpiClaimId)
              AND (:clientClaimId IS NULL OR UPPER(c.CLIENT_CLAIM_ID) LIKE CONCAT(UPPER(:clientClaimId), '%'))
              AND (:insuredId   IS NULL OR UPPER(em.MEMBER_ID)       LIKE CONCAT(UPPER(:insuredId), '%'))
              AND (:lastName    IS NULL OR UPPER(em.LAST_NAME)       LIKE CONCAT(UPPER(:lastName), '%'))
              AND (:firstName   IS NULL OR UPPER(em.FIRST_NAME)      LIKE CONCAT(UPPER(:firstName), '%'))
              AND (:dob         IS NULL OR DATE(em.DT_OF_BIRTH)      = :dob)
              AND (:gender      IS NULL OR UPPER(em.GENDER)          = UPPER(:gender))
              AND (:policy      IS NULL OR UPPER(em.POLICY)          LIKE CONCAT(UPPER(:policy), '%'))
              AND (:ccode       IS NULL OR UPPER(em.CCODE)           LIKE CONCAT(UPPER(:ccode), '%'))
              AND (:network     IS NULL OR UPPER(c.CLAIM_STREAM)     LIKE CONCAT(UPPER(:network), '%'))
              AND (:matchedBy   IS NULL OR UPPER(cma.MODIFIED_BY)    LIKE CONCAT(UPPER(:matchedBy), '%'))
              AND (:matchType   IS NULL OR UPPER(r.MATCH_TYPE)       = UPPER(:matchType))
              AND (:claimType   IS NULL OR UPPER(c.CLAIM_TYPE)       = UPPER(:claimType))
              AND (:dateFrom    IS NULL OR DATE(c.LOAD_DATE)         >= :dateFrom)
              AND (:dateTo      IS NULL OR DATE(c.LOAD_DATE)         <= :dateTo)
            """,
            nativeQuery = true
    )
    Page<PostMatchSearchView> search(
            @Param("mpiClaimId")   Long mpiClaimId,
            @Param("clientClaimId") String clientClaimId,
            @Param("insuredId")    String insuredId,
            @Param("lastName")     String lastName,
            @Param("firstName")    String firstName,
            @Param("dob")          LocalDate dob,
            @Param("gender")       String gender,
            @Param("policy")       String policy,
            @Param("ccode")        String ccode,
            @Param("network")      String network,
            @Param("matchedBy")    String matchedBy,
            @Param("matchType")    String matchType,
            @Param("claimType")    String claimType,
            @Param("dateFrom")     LocalDate dateFrom,
            @Param("dateTo")       LocalDate dateTo,
            Pageable pageable
    );

    // ================= POST MATCH HISTORY (expand row) =================
    @Query(
            value = """
            SELECT
                cma.MPI_CLAIM_ID                 AS claimId,        -- <== FIX here too
                cma.MATCHED_CLIENT_CODE          AS ccode,
                r.SCENARIO                       AS scenario,
                r.MATCH_TYPE                     AS matchType,
                IFNULL(cma.MATCHED_MEMBER_ID, em.MEMBER_ID) AS memberId,
                cma.MODIFIED_BY                  AS matchedBy,
                cma.CLAIM_RECEIVED_AT            AS actionDate,
                pn.NOTETEXT                      AS denialNotes,
                cma.CLIENT_MATCH_ACTION_ID       AS sortField
            FROM CLIENT_MATCH_ACTION cma
            JOIN MATCH_RULES r
              ON cma.RULE_CODE = r.RULE_CODE
            LEFT JOIN ECM_ELIG_MEMBER em
              ON cma.ECM_ELIG_MEMBER_ID = em.ELIG_MEMBER_ID
            LEFT JOIN PEND_NOTE pn
              ON pn.CLAIM_NUMBER = cma.MPI_CLAIM_ID
            WHERE cma.MPI_CLAIM_ID = :claimId
            ORDER BY cma.CLAIM_RECEIVED_AT, cma.CLIENT_MATCH_ACTION_ID, pn.NOTESEQ
            """,
            nativeQuery = true
    )
    List<PostMatchDetailView> fetchDetails(@Param("claimId") Long claimId);
}
