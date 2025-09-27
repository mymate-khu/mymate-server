package com.mymate.mymate.member;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mymate.mymate.auth.enums.AuthProvider;
import com.mymate.mymate.member.enums.Role;
import com.mymate.mymate.member.repository.MemberRepository;
import com.mymate.mymate.group.service.GroupService;
import com.mymate.mymate.group.dto.GroupCreateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("dev")
@Component
@Order(2) // TermDataInitializer(Order=1) 다음에 실행
@RequiredArgsConstructor
public class MemberDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;

    @Override
    public void run(String... args) {
        // 개발 환경용 관리자 계정 초기화
        initializeAdminAccount();
        
        // 더미 사용자 및 그룹 데이터 초기화
        initializeDummyData();
    }

    private void initializeAdminAccount() {
        final String adminProviderUserId = "admin";
        final String adminEmail = "admin@local.dev";
        final String adminUserId = "admin";
        final String adminUsername = "Admin";
        final String adminPassword = "admin1234";

        memberRepository.findByProviderAndProviderUserId(AuthProvider.LOCAL, adminProviderUserId)
                .ifPresentOrElse(existing -> {
                    log.info("MEMBER:INIT:::admin account already exists - skipping creation");
                }, () -> {
                    // 관리자 계정 생성
                    Member adminMember = Member.builder()
                            .provider(AuthProvider.LOCAL)
                            .providerUserId(adminProviderUserId)
                            .email(adminEmail)
                            .userId(adminUserId)
                            .username(adminUsername)
                            .passwordHash(passwordEncoder.encode(adminPassword))
                            .isSignUpCompleted(true)
                            .role(Role.ADMIN)
                            .inactive(null)
                            .build();

                    memberRepository.save(adminMember);
                    log.info("MEMBER:INIT:::admin account created - email: {}, userId: {}", adminEmail, adminUserId);
                });
    }

    private void initializeDummyData() {
        // 더미 사용자 데이터
        String[][] dummyUsers = {
            {"user1", "user1@test.com", "김철수", "user1pass"},
            {"user2", "user2@test.com", "이영희", "user2pass"},
            {"user3", "user3@test.com", "박민수", "user3pass"},
            {"user4", "user4@test.com", "정수진", "user4pass"},
            {"user5", "user5@test.com", "최지훈", "user5pass"}
        };

        for (String[] userData : dummyUsers) {
            String userId = userData[0];
            String email = userData[1];
            String username = userData[2];
            String password = userData[3];

            memberRepository.findByProviderAndProviderUserId(AuthProvider.LOCAL, userId)
                    .ifPresentOrElse(existing -> {
                        log.info("MEMBER:INIT:::dummy user {} already exists - skipping creation", userId);
                    }, () -> {
                        // 더미 사용자 생성
                        Member dummyMember = Member.builder()
                                .provider(AuthProvider.LOCAL)
                                .providerUserId(userId)
                                .email(email)
                                .userId(userId)
                                .username(username)
                                .passwordHash(passwordEncoder.encode(password))
                                .isSignUpCompleted(true)
                                .role(Role.USER)
                                .inactive(null)
                                .build();

                        Member savedMember = memberRepository.save(dummyMember);

                        // 기본 그룹 생성
                        GroupCreateRequest groupCreateRequest = new GroupCreateRequest(username + "의 그룹");
                        groupService.createGroup(groupCreateRequest, savedMember.getId());

                        log.info("MEMBER:INIT:::dummy user created - email: {}, userId: {}", email, userId);
                    });
        }
    }
}
