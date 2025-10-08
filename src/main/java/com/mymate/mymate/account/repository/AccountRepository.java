package com.mymate.mymate.account.repository;

import com.mymate.mymate.account.entity.Account;
import com.mymate.mymate.account.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 그룹별 정산 조회
    Page<Account> findByGroupId(Long groupId, Pageable pageable);

    // 그룹별 정산 조회 (권한 확인용)
    Optional<Account> findByIdAndGroupId(Long id, Long groupId);

    // 그룹별 상태별 정산 조회
    Page<Account> findByGroupIdAndStatus(Long groupId, AccountStatus status, Pageable pageable);

    // 그룹별 카테고리별 정산 조회
    Page<Account> findByGroupIdAndCategory(Long groupId, String category, Pageable pageable);

    // 그룹별 월별 정산 조회
    @Query("SELECT a FROM Account a WHERE a.groupId = :groupId " +
            "AND YEAR(a.expenseDate) = :year AND MONTH(a.expenseDate) = :month " +
            "ORDER BY a.expenseDate DESC, a.createdAt DESC")
    Page<Account> findByGroupIdAndYearMonth(@Param("groupId") Long groupId,
                                            @Param("year") int year,
                                            @Param("month") int month,
                                            Pageable pageable);

    // 그룹별 날짜 범위 정산 조회
    List<Account> findByGroupIdAndExpenseDateBetween(Long groupId, LocalDate startDate, LocalDate endDate);

    // 그룹별 특정 날짜 정산 조회
    List<Account> findByGroupIdAndExpenseDate(Long groupId, LocalDate expenseDate);

    // 그룹별 정산 개수 조회
    long countByGroupId(Long groupId);

    // 그룹별 미완료 정산 개수 조회
    long countByGroupIdAndStatus(Long groupId, AccountStatus status);

    // 그룹별 카테고리 목록 조회
    @Query("SELECT DISTINCT a.category FROM Account a WHERE a.groupId = :groupId ORDER BY a.category")
    List<String> findDistinctCategoriesByGroupId(@Param("groupId") Long groupId);

    // 텍스트 검색
    @Query("SELECT a FROM Account a WHERE a.groupId = :groupId " +
            "AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "ORDER BY a.expenseDate DESC, a.createdAt DESC")
    Page<Account> searchByText(@Param("groupId") Long groupId,
                               @Param("searchText") String searchText,
                               Pageable pageable);
}