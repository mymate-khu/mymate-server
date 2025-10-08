package com.mymate.mymate.account.service;

import com.mymate.mymate.account.dto.AccountCreateRequest;
import com.mymate.mymate.account.dto.AccountListResponse;
import com.mymate.mymate.account.dto.AccountResponse;
import com.mymate.mymate.account.dto.AccountStatusUpdateRequest;
import com.mymate.mymate.account.dto.AccountUpdateRequest;
import com.mymate.mymate.account.enums.AccountStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AccountService {

    // 정산 생성
    AccountResponse createAccount(Long memberId, AccountCreateRequest request);

    // 정산 상세 조회
    AccountResponse getAccount(Long memberId, Long accountId);

    // 정산 목록 조회 (페이징)
    AccountListResponse getAccounts(Long memberId, Pageable pageable);

    // 정산 수정
    AccountResponse updateAccount(Long memberId, Long accountId, AccountUpdateRequest request);

    // 정산 삭제
    void deleteAccount(Long memberId, Long accountId);

    // 정산 상태 변경
    AccountResponse updateAccountStatus(Long memberId, Long accountId, AccountStatusUpdateRequest request);

    // 월별 정산 조회
    AccountListResponse getAccountsByMonth(Long memberId, int year, int month, Pageable pageable);

    // 카테고리별 정산 조회
    AccountListResponse getAccountsByCategory(Long memberId, String category, Pageable pageable);

    // 상태별 정산 조회 (미완료 정산 포함)
    AccountListResponse getAccountsByStatus(Long memberId, AccountStatus status, Pageable pageable);

    // 날짜 범위 정산 조회
    List<AccountResponse> getAccountsByDateRange(Long memberId, LocalDate startDate, LocalDate endDate);

    // 텍스트 검색
    AccountListResponse searchAccounts(Long memberId, String searchText, Pageable pageable);

    // 그룹의 카테고리 목록 조회
    List<String> getCategories(Long memberId);
}