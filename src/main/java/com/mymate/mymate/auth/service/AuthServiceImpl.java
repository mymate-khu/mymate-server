package com.mymate.mymate.auth.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mymate.mymate.auth.dto.LocalLoginRequest;
import com.mymate.mymate.auth.dto.SignUpRequest;
import com.mymate.mymate.auth.dto.TokenResponse;
import com.mymate.mymate.auth.enums.AuthProvider;
import com.mymate.mymate.auth.jwt.JwtProvider;
import com.mymate.mymate.auth.oauth.SocialTokenVerifier;
import com.mymate.mymate.auth.token.RefreshTokenStore;
import com.mymate.mymate.common.exception.member.MemberHandler;
import com.mymate.mymate.common.exception.member.status.MemberErrorStatus;
import com.mymate.mymate.common.exception.phone.PhoneHandler;
import com.mymate.mymate.common.exception.phone.status.PhoneErrorStatus;
import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.enums.Role;
import com.mymate.mymate.member.repository.MemberRepository;
import com.mymate.mymate.term.dto.AgreementRequest;
import com.mymate.mymate.term.service.AgreementService;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final SocialTokenVerifier socialTokenVerifier;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AgreementService agreementService;
    private final PhoneVerificationService phoneVerificationService;

    public AuthServiceImpl(JwtProvider jwtProvider, RefreshTokenStore refreshTokenStore, SocialTokenVerifier socialTokenVerifier, MemberRepository memberRepository, PasswordEncoder passwordEncoder, AgreementService agreementService, PhoneVerificationService phoneVerificationService) {
        this.jwtProvider = jwtProvider;
        this.refreshTokenStore = refreshTokenStore;
        this.socialTokenVerifier = socialTokenVerifier;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.agreementService = agreementService;
        this.phoneVerificationService = phoneVerificationService;
    }

    @Override
    public TokenPair issueTokensOnLogin(Long id, String email, String name, Role role, boolean signUpCompleted) {
        String access = jwtProvider.createAccessToken(id, email, name, role, signUpCompleted);
        
        String uid = String.valueOf(id);
        String sid = UUID.randomUUID().toString();
        String familyId = UUID.randomUUID().toString();
        
        // sid와 familyId가 포함된 리프레시 토큰 생성
        String refresh = jwtProvider.createRefreshToken(id, email, name, role, signUpCompleted, sid, familyId);

        long iat = Instant.now().getEpochSecond();
        long exp = iat + jwtProvider.getRemainingValidity(refresh); // seconds

        refreshTokenStore.saveOnLogin(refresh, uid, sid, familyId, iat, exp);

        return new TokenPair(access, refresh, sid, familyId);
    }

    @Override
    public TokenResponse socialLogin(String provider, String token) {
        try {
            SocialTokenVerifier.SocialUserInfo info = socialTokenVerifier.verify(provider, token);
            String email = info.getEmail();
            String name = info.getName();
            String providerUserId = info.getProviderUserId();

            // 기존 회원 조회 (email 기준). 없으면 임시 id 생성 및 가입 미완으로 간주
            Member member = memberRepository.findByEmail(email).orElse(null);
            long id = (member != null) ? member.getId() : Math.abs((long) email.hashCode());
            boolean isSignUpCompleted = (member != null) && member.isSignUpCompleted();

            // member가 null이면 임시 Access 토큰 발급 (signup scope)
            if (member == null) {
                String accessToken = jwtProvider.createTemporaryAccessToken(id, email, name, Role.USER, provider, providerUserId);
                return new TokenResponse(accessToken, email, name, false);
            }

            // member가 존재해도 가입 미완이면 임시 Access만 발급
            if (!isSignUpCompleted) {
                String accessToken = jwtProvider.createTemporaryAccessToken(id, email, name, Role.USER);
                return new TokenResponse(accessToken, email, name, false);
            }

            // 가입 완료면 액세스/리프레시 모두 발급
            TokenPair tokens = issueTokensOnLogin(id, email, name, Role.USER, true);
            return new TokenResponse(tokens.accessToken(), tokens.refreshToken(), tokens.sessionId(), tokens.familyId(), email, name, true);
        } catch (Exception e) {
            throw new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND);
        }
    }

    @Override
    public TokenResponse localLogin(LocalLoginRequest request) {
        Member member = memberRepository.findFirstByUserId(request.userId).orElse(null);
        if (member == null || member.getPasswordHash() == null) {
            throw new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(request.passwordEncrypted, member.getPasswordHash())) {
            throw new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND);
        }

        boolean isSignUpCompleted = member.isSignUpCompleted();
        if (!isSignUpCompleted) {
            // 로컬 정책상 없겠지만 방어적으로 임시 토큰 발급 가능
            String accessToken = jwtProvider.createTemporaryAccessToken(member.getId(), member.getEmail(), member.getUsername(), Role.USER);
            return new TokenResponse(accessToken, member.getEmail(), member.getUsername(), false);
        }

        TokenPair tokens = issueTokensOnLogin(member.getId(), member.getEmail(), member.getUsername(), Role.USER, true);
        return new TokenResponse(tokens.accessToken(), tokens.refreshToken(), tokens.sessionId(), tokens.familyId(), member.getEmail(), member.getUsername(), true);
    }

    @Override
    public TokenResponse signUp(SignUpRequest request) {
        // 소셜/로컬 분기: Authorization 헤더로 전달된 임시 토큰 기반
        String signUpToken = request.token;
        String scope = signUpToken != null ? jwtProvider.getScope(signUpToken) : null;
        String providerClaim = signUpToken != null ? jwtProvider.getProvider(signUpToken) : null;
        boolean isSocial = signUpToken != null && "signup".equals(scope) && providerClaim != null;

        Member member;
        if (isSocial) {
            // 소셜 가입: 폰 인증 필수
            if (!phoneVerificationService.isPhoneVerified(request.phone)) {
                throw new PhoneHandler(PhoneErrorStatus.NOT_VERIFIED);
            }
            // 비밀번호는 불필요
            // 임시 토큰에서 provider/providerUserId/email/name 추출
            String provider = jwtProvider.getProvider(signUpToken);
            String providerUserId = jwtProvider.getProviderUserId(signUpToken);
            String emailFromToken = jwtProvider.getEmail(signUpToken);
            String nameFromToken = jwtProvider.getMemberName(signUpToken);
            AuthProvider authProvider = null;
            try { authProvider = provider != null ? AuthProvider.valueOf(provider) : AuthProvider.LOCAL; } catch (Exception ignore) { authProvider = AuthProvider.LOCAL; }

            member = Member.builder()
                    .provider(authProvider)
                    .providerUserId(providerUserId != null ? providerUserId : request.email)
                    .email(emailFromToken != null ? emailFromToken : request.email)
                    .userId(null)
                    .username(null)
                    .passwordHash(null)
                    .isSignUpCompleted(true)
                    .build();
        } else {
            // userId 중복 체크
            if (memberRepository.findFirstByUserId(request.userId).isPresent()) {
                throw new MemberHandler(MemberErrorStatus.MEMBER_ALREADY_EXISTS);
            }

            // 휴대폰 인증 확인
            if (!phoneVerificationService.isPhoneVerified(request.phone)) {
                throw new PhoneHandler(PhoneErrorStatus.NOT_VERIFIED);
            }

            String passwordHash = passwordEncoder.encode(request.passwordEncrypted);
            member = Member.builder()
                    .provider(AuthProvider.LOCAL)
                    .providerUserId(request.userId)
                    .email(request.email)
                    .username(request.name)
                    .passwordHash(passwordHash)
                    .userId(request.userId)
                    .isSignUpCompleted(true)
                    .build();
        }
        member = memberRepository.save(member);

        // 약관 동의 저장
        AgreementRequest agreementRequest = AgreementRequest.builder()
                .agreeService(Boolean.TRUE.equals(request.agreeService))
                .agreePrivacy(Boolean.TRUE.equals(request.agreePrivacy))
                .agreeAgeOver14(Boolean.TRUE.equals(request.agreeAgeOver14))
                .agreeThirdParty(Boolean.TRUE.equals(request.agreeThirdParty))
                .agreeMarketing(Boolean.TRUE.equals(request.agreeMarketing))
                .verifyLatestVersion(true)
                .build();
        agreementService.agree(member.getId(), agreementRequest);

        // 최종 토큰 발급
        TokenPair tokens = issueTokensOnLogin(member.getId(), member.getEmail(), member.getUsername(), Role.USER, true);
        return new TokenResponse(tokens.accessToken(), tokens.refreshToken(), tokens.sessionId(), tokens.familyId(), member.getEmail(), member.getUsername(), true);
    }
     
     @Override
     public TokenResponse refreshToken(String oldRefreshToken) {
         try {
             // 토큰에서 사용자 정보 파싱
             String email = jwtProvider.getEmail(oldRefreshToken);
             String name = jwtProvider.getMemberName(oldRefreshToken);
             Long id = jwtProvider.getId(oldRefreshToken);
             String roleStr = jwtProvider.getRole(oldRefreshToken);
             Role role = Role.valueOf(roleStr);
             
             // 리프레시 토큰에서 기존 sid와 familyId 추출
             String sid = jwtProvider.getSessionId(oldRefreshToken);
             String familyId = jwtProvider.getFamilyId(oldRefreshToken);
             
             // JWT 토큰 기반 리프레시 토큰 회전
             String uid = String.valueOf(id);
             RefreshTokenStore.RotateResult result = refreshTokenStore.rotateWithJwt(oldRefreshToken, uid, sid, familyId, id, email, name, role, true);
             
             // 새 액세스 토큰 생성
             String newAccessToken = jwtProvider.createAccessToken(id, email, name, role, true);
             
             return new TokenResponse(newAccessToken, result.newRefreshRaw(), result.sid(), result.familyId(), email, name, true);
         } catch (Exception e) {
             throw new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND);
         }
     }
 }
