package com.mymate.mymate.member;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mymate.mymate.auth.enums.AuthProvider;
import com.mymate.mymate.member.enums.Role;
import com.mymate.mymate.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class MemberDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 개발 환경용 관리자 계정 초기화
        initializeAdminAccount();
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
}
