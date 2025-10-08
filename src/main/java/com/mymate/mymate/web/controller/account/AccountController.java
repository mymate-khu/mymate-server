package com.mymate.mymate.web.controller.account;

import com.mymate.mymate.account.dto.*;
import com.mymate.mymate.account.enums.AccountStatus;
import com.mymate.mymate.account.service.AccountService;
import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.ApiErrorCodeExample;
import com.mymate.mymate.common.exception.ApiErrorCodeExamples;
import com.mymate.mymate.common.exception.account.status.AccountErrorStatus;
import com.mymate.mymate.common.exception.account.status.AccountSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account", description = "정산 관리 API")
@SecurityRequirement(name = "accessToken")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(
        summary = "정산 생성", 
        description = "새로운 정산을 생성합니다. 그룹 내에서 발생한 지출에 대한 정산을 등록할 때 사용합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"GROUP_NOT_FOUND", "MEMBER_NOT_IN_GROUP", "INVALID_PARTICIPANT", "INVALID_AMOUNT"}
        )
    })
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody AccountCreateRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        AccountResponse response = accountService.createAccount(userPrincipal.getId(), request);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_CREATED, response);
    }

    @GetMapping("/{accountId}")
    @Operation(
        summary = "정산 상세 조회", 
        description = "특정 정산의 상세 정보를 조회합니다. 정산 내용과 참여자 정보를 확인할 때 사용합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"ACCOUNT_NOT_FOUND", "FORBIDDEN"}
        )
    })
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @Parameter(description = "정산 ID", required = true)
            @PathVariable Long accountId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        AccountResponse response = accountService.getAccount(userPrincipal.getId(), accountId);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_FOUND, response);
    }

    @GetMapping
    @Operation(
        summary = "정산 목록 조회", 
        description = "사용자가 참여한 정산 목록을 페이징하여 조회합니다. 기본적으로 최신 순으로 정렬됩니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"GROUP_NOT_FOUND"}
        )
    })
    public ResponseEntity<ApiResponse<AccountListResponse>> getAccounts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "정렬 방향 (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        AccountListResponse response = accountService.getAccounts(userPrincipal.getId(), pageable);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_LIST_FOUND, response);
    }

    @PutMapping("/{accountId}")
    @Operation(
        summary = "정산 수정", 
        description = "기존 정산의 정보를 수정합니다. 정산 상태가 PENDING일 때만 수정 가능합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"ACCOUNT_NOT_FOUND", "FORBIDDEN", "ACCOUNT_ALREADY_COMPLETED", "INVALID_PARTICIPANT", "INVALID_AMOUNT"}
        )
    })
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @Parameter(description = "정산 ID", required = true)
            @PathVariable Long accountId,
            @Valid @RequestBody AccountUpdateRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        AccountResponse response = accountService.updateAccount(userPrincipal.getId(), accountId, request);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_UPDATED, response);
    }

    @DeleteMapping("/{accountId}")
    @Operation(
        summary = "정산 삭제", 
        description = "정산을 삭제합니다. 정산 생성자만 삭제할 수 있으며, 정산 상태가 COMPLETED인 경우 삭제할 수 없습니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"ACCOUNT_NOT_FOUND", "FORBIDDEN", "ACCOUNT_ALREADY_COMPLETED"}
        )
    })
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @Parameter(description = "정산 ID", required = true)
            @PathVariable Long accountId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        accountService.deleteAccount(userPrincipal.getId(), accountId);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_DELETED);
    }

    @PatchMapping("/{accountId}/status")
    @Operation(
        summary = "정산 상태 변경", 
        description = "정산의 상태를 변경합니다. PENDING ↔ COMPLETED 간의 상태 변경이 가능합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"ACCOUNT_NOT_FOUND", "FORBIDDEN"}
        )
    })
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccountStatus(
            @Parameter(description = "정산 ID", required = true)
            @PathVariable Long accountId,
            @Valid @RequestBody AccountStatusUpdateRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        AccountResponse response = accountService.updateAccountStatus(userPrincipal.getId(), accountId, request);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_STATUS_UPDATED, response);
    }

    @GetMapping("/month")
    @Operation(
        summary = "월별 정산 조회", 
        description = "특정 월의 정산 목록을 조회합니다. 월별 정산 현황을 확인할 때 사용합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"GROUP_NOT_FOUND"}
        )
    })
    public ResponseEntity<ApiResponse<AccountListResponse>> getAccountsByMonth(
            @Parameter(description = "조회할 연도", example = "2024")
            @RequestParam int year,
            @Parameter(description = "조회할 월 (1-12)", example = "1")
            @RequestParam int month,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "expenseDate"));
        AccountListResponse response = accountService.getAccountsByMonth(userPrincipal.getId(), year, month, pageable);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_LIST_FOUND, response);
    }

    @GetMapping("/category")
    @Operation(
        summary = "카테고리별 정산 조회", 
        description = "특정 카테고리의 정산 목록을 조회합니다. 카테고리별 지출 현황을 확인할 때 사용합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"GROUP_NOT_FOUND"}
        )
    })
    public ResponseEntity<ApiResponse<AccountListResponse>> getAccountsByCategory(
            @Parameter(description = "조회할 카테고리", example = "식비")
            @RequestParam String category,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "expenseDate"));
        AccountListResponse response = accountService.getAccountsByCategory(userPrincipal.getId(), category, pageable);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_LIST_FOUND, response);
    }

    @GetMapping("/status")
    @Operation(
        summary = "상태별 정산 조회", 
        description = "특정 상태의 정산 목록을 조회합니다. 미완료 정산이나 완료된 정산을 확인할 때 사용합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"GROUP_NOT_FOUND"}
        )
    })
    public ResponseEntity<ApiResponse<AccountListResponse>> getAccountsByStatus(
            @Parameter(description = "조회할 정산 상태", example = "PENDING")
            @RequestParam AccountStatus status,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "expenseDate"));
        AccountListResponse response = accountService.getAccountsByStatus(userPrincipal.getId(), status, pageable);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_LIST_FOUND, response);
    }

    @GetMapping("/date-range")
    @Operation(
        summary = "날짜 범위 정산 조회", 
        description = "특정 날짜 범위의 정산 목록을 조회합니다. 기간별 정산 현황을 확인할 때 사용합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"GROUP_NOT_FOUND", "INVALID_DATE_RANGE"}
        )
    })
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountsByDateRange(
            @Parameter(description = "시작 날짜", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료 날짜", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        List<AccountResponse> response = accountService.getAccountsByDateRange(userPrincipal.getId(), startDate, endDate);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_LIST_FOUND, response);
    }

    @GetMapping("/search")
    @Operation(
        summary = "정산 검색", 
        description = "제목이나 설명에서 특정 텍스트를 검색하여 정산 목록을 조회합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"GROUP_NOT_FOUND"}
        )
    })
    public ResponseEntity<ApiResponse<AccountListResponse>> searchAccounts(
            @Parameter(description = "검색할 텍스트", example = "회식")
            @RequestParam String searchText,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        AccountListResponse response = accountService.searchAccounts(userPrincipal.getId(), searchText, pageable);
        return ApiResponse.onSuccess(AccountSuccessStatus.ACCOUNT_LIST_FOUND, response);
    }

    @GetMapping("/categories")
    @Operation(
        summary = "카테고리 목록 조회", 
        description = "사용자가 참여한 정산에서 사용된 카테고리 목록을 조회합니다.",
        tags = {"Account"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = AccountErrorStatus.class,
            codes = {"GROUP_NOT_FOUND"}
        )
    })
    public ResponseEntity<ApiResponse<List<String>>> getCategories(
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        List<String> response = accountService.getCategories(userPrincipal.getId());
        return ApiResponse.onSuccess(AccountSuccessStatus.CATEGORIES_FOUND, response);
    }
}
