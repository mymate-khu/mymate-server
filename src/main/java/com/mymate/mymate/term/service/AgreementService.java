package com.mymate.mymate.term.service;

import com.mymate.mymate.common.exception.term.TermHandler;
import com.mymate.mymate.common.exception.term.status.TermErrorStatus;
import com.mymate.mymate.term.dto.AgreementRequest;
import com.mymate.mymate.term.dto.AgreementResponse;
import com.mymate.mymate.term.entity.Term;
import com.mymate.mymate.term.entity.TermAgreement;
import com.mymate.mymate.term.repository.TermAgreementRepository;
import com.mymate.mymate.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AgreementService {

    private final TermRepository termRepository;
    private final TermAgreementRepository termAgreementRepository;

    @Transactional
    public AgreementResponse agree(Long memberId, AgreementRequest request) {
        // 필수 약관 목록 조회
        List<Term> requiredTerms = termRepository.findByRequiredTrue();
        if (requiredTerms == null || requiredTerms.isEmpty()) {
            throw new TermHandler(TermErrorStatus.TERM_NOT_FOUND);
        }

        // 최신 버전 옵션 검증 시 코드별 최신 버전인지 확인
        if (Boolean.TRUE.equals(request.getVerifyLatestVersion())) {
            for (Term term : requiredTerms) {
                Term latest = termRepository.findTopByCodeOrderByVersionDesc(term.getCode())
                        .orElseThrow(() -> new TermHandler(TermErrorStatus.TERM_NOT_FOUND));
                if (!latest.getVersion().equals(term.getVersion())) {
                    throw new TermHandler(TermErrorStatus.NOT_LATEST_TERM_VERSION);
                }
            }
        }

        Map<String, Boolean> codeToAgreed = mapAgreementByCode(request);

        boolean missingService = isMissing(codeToAgreed, "SERVICE");
        boolean missingPrivacy = isMissing(codeToAgreed, "PRIVACY");
        boolean missingAgeOver14 = isMissing(codeToAgreed, "AGE_OVER_14");

        boolean missingRequired = missingService || missingPrivacy || missingAgeOver14;

        if (missingRequired) {
            return AgreementResponse.builder()
                    .message("필수 약관 동의가 누락되었습니다.")
                    .missingRequired(true)
                    .missingService(missingService)
                    .missingPrivacy(missingPrivacy)
                    .missingAgeOver14(missingAgeOver14)
                    .build();
        }

        // upsert 수행: 필수/선택 모두 반영
        upsertAgreement(memberId, "SERVICE", getOrDefault(codeToAgreed, "SERVICE"));
        upsertAgreement(memberId, "PRIVACY", getOrDefault(codeToAgreed, "PRIVACY"));
        upsertAgreement(memberId, "AGE_OVER_14", getOrDefault(codeToAgreed, "AGE_OVER_14"));
        upsertAgreement(memberId, "THIRD_PARTY", getOrDefault(codeToAgreed, "THIRD_PARTY"));
        upsertAgreement(memberId, "MARKETING", getOrDefault(codeToAgreed, "MARKETING"));

        return AgreementResponse.builder()
                .message("약관 동의가 저장되었습니다.")
                .missingRequired(false)
                .missingService(false)
                .missingPrivacy(false)
                .missingAgeOver14(false)
                .build();
    }

    private void upsertAgreement(Long memberId, String code, boolean agreed) {
        Term latest = termRepository.findTopByCodeOrderByVersionDesc(code)
                .orElseThrow(() -> new TermHandler(TermErrorStatus.TERM_NOT_FOUND));

        TermAgreement existing = termAgreementRepository.findByMemberIdAndTermId(memberId, latest.getId())
                .orElse(null);

        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            TermAgreement created = TermAgreement.builder()
                    .memberId(memberId)
                    .termId(latest.getId())
                    .agreed(agreed)
                    .agreedAt(agreed ? now : null)
                    .withdrawnAt(agreed ? null : now)
                    .build();
            termAgreementRepository.save(created);
            return;
        }

        existing.setAgreed(agreed);
        if (agreed) {
            existing.setAgreedAt(now);
            existing.setWithdrawnAt(null);
        } else {
            existing.setWithdrawnAt(now);
        }
        termAgreementRepository.save(existing);
    }

    private static boolean getOrDefault(Map<String, Boolean> map, String key) {
        return map.getOrDefault(key, false);
    }

    private static boolean isMissing(Map<String, Boolean> codeToAgreed, String code) {
        return !codeToAgreed.getOrDefault(code, false);
    }

    private static Map<String, Boolean> mapAgreementByCode(AgreementRequest request) {
        Map<String, Boolean> codeToAgreed = new HashMap<>();
        codeToAgreed.put("SERVICE", request.getAgreeService());
        codeToAgreed.put("PRIVACY", request.getAgreePrivacy());
        codeToAgreed.put("AGE_OVER_14", request.getAgreeAgeOver14());
        codeToAgreed.put("THIRD_PARTY", request.getAgreeThirdParty() != null && request.getAgreeThirdParty());
        codeToAgreed.put("MARKETING", request.getAgreeMarketing() != null && request.getAgreeMarketing());
        return codeToAgreed;
    }
}


