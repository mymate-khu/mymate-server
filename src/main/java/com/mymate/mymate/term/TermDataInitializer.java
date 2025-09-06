package com.mymate.mymate.term;

import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.mymate.mymate.term.entity.Term;
import com.mymate.mymate.term.repository.TermRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class TermDataInitializer implements CommandLineRunner {

    private final TermRepository termRepository;

    @Override
    public void run(String... args) {
        // 기본 약관 코드와 메타 정의
        Map<String, TermMeta> defaults = Map.of(
                "SERVICE", new TermMeta("v1.0", "서비스 이용약관", "https://example.com/terms/service", true),
                "PRIVACY", new TermMeta("v1.0", "개인정보 처리방침", "https://example.com/terms/privacy", true),
                "AGE_OVER_14", new TermMeta("v1.0", "만 14세 이상 확인", "https://example.com/terms/age", true),
                "THIRD_PARTY", new TermMeta("v1.0", "제3자 정보 제공 동의", "https://example.com/terms/third-party", false),
                "MARKETING", new TermMeta("v1.0", "마케팅 정보 수신 동의", "https://example.com/terms/marketing", false)
        );

        defaults.forEach((code, meta) -> {
            termRepository.findTopByCodeOrderByVersionDesc(code)
                    .ifPresentOrElse(existing -> {
                        if (!existing.getVersion().equals(meta.version)) {
                            // 새 버전 발행
                            saveTerm(code, meta);
                            log.info("TERM:INIT:::new version added code({}) version({})", code, meta.version);
                        }
                    }, () -> {
                        // 최초 생성
                        saveTerm(code, meta);
                        log.info("TERM:INIT:::seed created code({}) version({})", code, meta.version);
                    });
        });
    }

    private void saveTerm(String code, TermMeta meta) {
        termRepository.save(Term.builder()
                .code(code)
                .version(meta.version)
                .title(meta.title)
                .contentUrl(meta.url)
                .required(meta.required)
                .build());
    }

    private record TermMeta(String version, String title, String url, boolean required) {}
}


