package com.mymate.mymate.account.service;

import com.mymate.mymate.account.dto.AccountCreateRequest;
import com.mymate.mymate.account.dto.AccountListResponse;
import com.mymate.mymate.account.dto.AccountResponse;
import com.mymate.mymate.account.dto.AccountStatusUpdateRequest;
import com.mymate.mymate.account.dto.AccountUpdateRequest;
import com.mymate.mymate.account.entity.Account;
import com.mymate.mymate.account.entity.AccountParticipant;
import com.mymate.mymate.account.enums.AccountStatus;
import com.mymate.mymate.account.repository.AccountParticipantRepository;
import com.mymate.mymate.account.repository.AccountRepository;
import com.mymate.mymate.common.exception.account.AccountHandler;
import com.mymate.mymate.common.exception.account.status.AccountErrorStatus;
import com.mymate.mymate.common.exception.group.GroupHandler;
import com.mymate.mymate.group.entity.GroupMember;
import com.mymate.mymate.group.repository.GroupMemberRepository;
import com.mymate.mymate.group.status.GroupErrorStatus;
import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountParticipantRepository participantRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public AccountResponse createAccount(Long memberId, AccountCreateRequest request) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 정산 생성
        Account account = Account.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .totalAmount(request.getTotalAmount())
                .receiveAmount(request.getReceiveAmount())
                .groupId(groupId)
                .createdBy(memberId)
                .build();

        Account savedAccount = accountRepository.save(account);

        // 참여자들의 지불 금액 계산 (받을 금액을 참여자 수로 나눔)
        BigDecimal paymentAmountPerPerson = request.getReceiveAmount()
                .divide(BigDecimal.valueOf(request.getParticipantIds().size()), 2, RoundingMode.HALF_UP);

        // 참여자 추가
        List<AccountParticipant> participants = request.getParticipantIds().stream()
                .map(participantId -> AccountParticipant.builder()
                        .accountId(savedAccount.getId())
                        .memberId(participantId)
                        .paymentAmount(paymentAmountPerPerson)
                        .build())
                .collect(Collectors.toList());

        participantRepository.saveAll(participants);

        log.info("정산 생성 완료: accountId={}, groupId={}, createdBy={}",
                savedAccount.getId(), groupId, memberId);

        return convertToResponse(savedAccount, participants);
    }

    @Override
    public AccountResponse getAccount(Long memberId, Long accountId) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 정산 조회 (같은 그룹인지 확인)
        Account account = accountRepository.findByIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new AccountHandler(AccountErrorStatus.ACCOUNT_NOT_FOUND));

        // 참여자 조회
        List<AccountParticipant> participants = participantRepository.findByAccountId(accountId);

        return convertToResponse(account, participants);
    }

    @Override
    public AccountListResponse getAccounts(Long memberId, Pageable pageable) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 그룹의 모든 정산 조회
        Page<Account> accountPage = accountRepository.findByGroupId(groupId, pageable);

        List<AccountResponse> accountResponses = accountPage.getContent().stream()
                .map(account -> {
                    List<AccountParticipant> participants = participantRepository.findByAccountId(account.getId());
                    return convertToResponse(account, participants);
                })
                .collect(Collectors.toList());

        return AccountListResponse.from(
                accountResponses,
                accountPage.getTotalElements(),
                accountPage.getTotalPages(),
                accountPage.getNumber(),
                accountPage.getSize(),
                accountPage.isFirst(),
                accountPage.isLast()
        );
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(Long memberId, Long accountId, AccountUpdateRequest request) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 정산 조회 (같은 그룹인지 확인)
        Account account = accountRepository.findByIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new AccountHandler(AccountErrorStatus.ACCOUNT_NOT_FOUND));

        // 수정할 필드들 업데이트
        if (request.getTitle() != null) {
            account.updateTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            account.updateDescription(request.getDescription());
        }
        if (request.getExpenseDate() != null) {
            account.updateExpenseDate(request.getExpenseDate());
        }
        if (request.getCategory() != null) {
            account.updateCategory(request.getCategory());
        }
        if (request.getImageUrl() != null) {
            account.updateImageUrl(request.getImageUrl());
        }
        if (request.getTotalAmount() != null) {
            account.updateTotalAmount(request.getTotalAmount());
        }
        if (request.getReceiveAmount() != null) {
            account.updateReceiveAmount(request.getReceiveAmount());
        }

        // 참여자 목록이 변경되었다면 업데이트
        if (request.getParticipantIds() != null) {
            updateParticipants(accountId, request.getParticipantIds(), account.getReceiveAmount());
        }

        Account savedAccount = accountRepository.save(account);
        List<AccountParticipant> participants = participantRepository.findByAccountId(accountId);

        log.info("정산 수정 완료: accountId={}, groupId={}, updatedBy={}",
                accountId, groupId, memberId);

        return convertToResponse(savedAccount, participants);
    }

    @Override
    @Transactional
    public void deleteAccount(Long memberId, Long accountId) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 정산 조회 (같은 그룹인지 확인)
        Account account = accountRepository.findByIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new AccountHandler(AccountErrorStatus.ACCOUNT_NOT_FOUND));

        // 참여자 먼저 삭제
        participantRepository.deleteByAccountId(accountId);

        // 정산 삭제
        accountRepository.delete(account);

        log.info("정산 삭제 완료: accountId={}, groupId={}, deletedBy={}",
                accountId, groupId, memberId);
    }

    @Override
    @Transactional
    public AccountResponse updateAccountStatus(Long memberId, Long accountId, AccountStatusUpdateRequest request) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 정산 조회 (같은 그룹인지 확인)
        Account account = accountRepository.findByIdAndGroupId(accountId, groupId)
                .orElseThrow(() -> new AccountHandler(AccountErrorStatus.ACCOUNT_NOT_FOUND));

        if (request.getStatus() == AccountStatus.COMPLETED) {
            if (account.getStatus() == AccountStatus.COMPLETED) {
                throw new AccountHandler(AccountErrorStatus.ACCOUNT_ALREADY_COMPLETED);
            }
            account.complete();
        } else if (request.getStatus() == AccountStatus.PENDING) {
            account.reopen();
        }

        Account savedAccount = accountRepository.save(account);
        List<AccountParticipant> participants = participantRepository.findByAccountId(accountId);

        log.info("정산 상태 변경 완료: accountId={}, status={}, updatedBy={}",
                accountId, request.getStatus(), memberId);

        return convertToResponse(savedAccount, participants);
    }

    @Override
    public AccountListResponse getAccountsByMonth(Long memberId, int year, int month, Pageable pageable) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 월별 정산 조회
        Page<Account> accountPage = accountRepository.findByGroupIdAndYearMonth(groupId, year, month, pageable);

        List<AccountResponse> accountResponses = accountPage.getContent().stream()
                .map(account -> {
                    List<AccountParticipant> participants = participantRepository.findByAccountId(account.getId());
                    return convertToResponse(account, participants);
                })
                .collect(Collectors.toList());

        return AccountListResponse.from(
                accountResponses,
                accountPage.getTotalElements(),
                accountPage.getTotalPages(),
                accountPage.getNumber(),
                accountPage.getSize(),
                accountPage.isFirst(),
                accountPage.isLast()
        );
    }

    @Override
    public AccountListResponse getAccountsByCategory(Long memberId, String category, Pageable pageable) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 카테고리별 정산 조회
        Page<Account> accountPage = accountRepository.findByGroupIdAndCategory(groupId, category, pageable);

        List<AccountResponse> accountResponses = accountPage.getContent().stream()
                .map(account -> {
                    List<AccountParticipant> participants = participantRepository.findByAccountId(account.getId());
                    return convertToResponse(account, participants);
                })
                .collect(Collectors.toList());

        return AccountListResponse.from(
                accountResponses,
                accountPage.getTotalElements(),
                accountPage.getTotalPages(),
                accountPage.getNumber(),
                accountPage.getSize(),
                accountPage.isFirst(),
                accountPage.isLast()
        );
    }

    @Override
    public AccountListResponse getAccountsByStatus(Long memberId, AccountStatus status, Pageable pageable) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 상태별 정산 조회
        Page<Account> accountPage = accountRepository.findByGroupIdAndStatus(groupId, status, pageable);

        List<AccountResponse> accountResponses = accountPage.getContent().stream()
                .map(account -> {
                    List<AccountParticipant> participants = participantRepository.findByAccountId(account.getId());
                    return convertToResponse(account, participants);
                })
                .collect(Collectors.toList());

        return AccountListResponse.from(
                accountResponses,
                accountPage.getTotalElements(),
                accountPage.getTotalPages(),
                accountPage.getNumber(),
                accountPage.getSize(),
                accountPage.isFirst(),
                accountPage.isLast()
        );
    }

    @Override
    public List<AccountResponse> getAccountsByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 날짜 범위 정산 조회
        List<Account> accounts = accountRepository.findByGroupIdAndExpenseDateBetween(groupId, startDate, endDate);

        return accounts.stream()
                .map(account -> {
                    List<AccountParticipant> participants = participantRepository.findByAccountId(account.getId());
                    return convertToResponse(account, participants);
                })
                .collect(Collectors.toList());
    }

    @Override
    public AccountListResponse searchAccounts(Long memberId, String searchText, Pageable pageable) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 텍스트 검색
        Page<Account> accountPage = accountRepository.searchByText(groupId, searchText, pageable);

        List<AccountResponse> accountResponses = accountPage.getContent().stream()
                .map(account -> {
                    List<AccountParticipant> participants = participantRepository.findByAccountId(account.getId());
                    return convertToResponse(account, participants);
                })
                .collect(Collectors.toList());

        return AccountListResponse.from(
                accountResponses,
                accountPage.getTotalElements(),
                accountPage.getTotalPages(),
                accountPage.getNumber(),
                accountPage.getSize(),
                accountPage.isFirst(),
                accountPage.isLast()
        );
    }

    @Override
    public List<String> getCategories(Long memberId) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 그룹의 카테고리 목록 조회
        return accountRepository.findDistinctCategoriesByGroupId(groupId);
    }

    /**
     * 참여자를 안전하게 업데이트하는 헬퍼 메서드
     */
    private void updateParticipants(Long accountId, List<Long> newParticipantIds, BigDecimal receiveAmount) {
        // 기존 참여자 조회
        List<AccountParticipant> existingParticipants = participantRepository.findByAccountId(accountId);
        
        // 기존 참여자 ID 목록
        Set<Long> existingParticipantIds = existingParticipants.stream()
                .map(AccountParticipant::getMemberId)
                .collect(Collectors.toSet());
        
        // 새 참여자 ID 목록
        Set<Long> newParticipantIdSet = new HashSet<>(newParticipantIds);
        
        // 삭제할 참여자들 (기존에 있지만 새 목록에 없는)
        Set<Long> toDeleteIds = existingParticipantIds.stream()
                .filter(id -> !newParticipantIdSet.contains(id))
                .collect(Collectors.toSet());
        
        // 추가할 참여자들 (새 목록에 있지만 기존에 없는)
        Set<Long> toAddIds = newParticipantIdSet.stream()
                .filter(id -> !existingParticipantIds.contains(id))
                .collect(Collectors.toSet());
        
        // 삭제할 참여자들 제거
        if (!toDeleteIds.isEmpty()) {
            participantRepository.deleteByAccountIdAndMemberIdIn(accountId, toDeleteIds);
        }
        
        // 추가할 참여자들 추가
        if (!toAddIds.isEmpty()) {
            BigDecimal paymentAmountPerPerson = receiveAmount
                    .divide(BigDecimal.valueOf(newParticipantIds.size()), 2, RoundingMode.HALF_UP);
            
            List<AccountParticipant> newParticipants = toAddIds.stream()
                    .map(participantId -> AccountParticipant.builder()
                            .accountId(accountId)
                            .memberId(participantId)
                            .paymentAmount(paymentAmountPerPerson)
                            .build())
                    .collect(Collectors.toList());
            
            participantRepository.saveAll(newParticipants);
        }
        
        // 기존 참여자들의 지불 금액 업데이트 (참여자 수가 변경되었을 수 있음)
        if (!existingParticipants.isEmpty() && !toDeleteIds.isEmpty() && !toAddIds.isEmpty()) {
            BigDecimal updatedPaymentAmountPerPerson = receiveAmount
                    .divide(BigDecimal.valueOf(newParticipantIds.size()), 2, RoundingMode.HALF_UP);
            
            existingParticipants.stream()
                    .filter(p -> !toDeleteIds.contains(p.getMemberId()))
                    .forEach(p -> p.updatePaymentAmount(updatedPaymentAmountPerPerson));
        }
    }

    /**
     * 사용자의 그룹 ID를 조회하는 헬퍼 메서드
     */
    private Long getUserGroupId(Long memberId) {
        List<GroupMember> groupMembers = groupMemberRepository.findByMemberId(memberId);
        if (groupMembers.isEmpty()) {
            throw new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND);
        }

        // 사용자당 하나의 그룹만 가질 수 있으므로 첫 번째 그룹 반환
        return groupMembers.get(0).getGroupId();
    }

    /**
     * Account와 AccountParticipant를 AccountResponse로 변환하는 헬퍼 메서드
     */
    private AccountResponse convertToResponse(Account account, List<AccountParticipant> participants) {
        // 참여자 ID 목록 조회
        List<Long> memberIds = participants.stream()
                .map(AccountParticipant::getMemberId)
                .collect(Collectors.toList());

        // 멤버 정보 조회
        Map<Long, String> memberNameMap = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(
                        Member::getId,
                        member -> member.getUsername() != null ? member.getUsername() : member.getEmail()
                ));

        // ParticipantResponse 생성
        List<AccountResponse.ParticipantResponse> participantResponses = participants.stream()
                .map(participant -> AccountResponse.ParticipantResponse.builder()
                        .id(participant.getId())
                        .memberId(participant.getMemberId())
                        .memberName(memberNameMap.get(participant.getMemberId()))
                        .paymentAmount(participant.getPaymentAmount())
                        .isPaid(participant.getIsPaid())
                        .build())
                .collect(Collectors.toList());

        return AccountResponse.from(account, participantResponses);
    }
}