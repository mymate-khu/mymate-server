package com.mymate.mymate.auth.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.token.status.TokenErrorStatus;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PREFIX = "Bearer ";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 헤더에서 토큰 추출
            String token = jwtProvider.resolveHeaderToken(request.getHeader(HttpHeaders.AUTHORIZATION), PREFIX);

            // 토큰이 유효하면 인증 정보 저장 (sign-up 토큰 개념 제거)
            if (token != null && jwtProvider.validateToken(token)) {
                UserPrincipal userPrincipal = setAuthentication(token);
                log.info("JWT_:FLT_:AUTH:::Authentication established successfully,id({}),email({}),name({}),role({})",
                        userPrincipal.getId(), userPrincipal.getEmail(), userPrincipal.getMemberName(), userPrincipal.getRole());
            }

            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            handleExpiredJwtException(response, e);
        } catch (JwtException e) {
            handleJwtException(response, e);
        }
    }

    private UserPrincipal setAuthentication(String token) {

        // UserPrincipal을 생성하고, 인증 정보를 SecurityContextHolder에 설정
        UserPrincipal userPrincipal = jwtProvider.getUserPrincipal(token);

        // scope 클레임을 권한으로 매핑
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(userPrincipal.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getAuthority())).toList());
        String scope = jwtProvider.getScope(token);
        if (scope != null && !scope.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
        }

        // 비밀번호는 필요하지 않으므로 null로 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userPrincipal;
    }

    private void handleExpiredJwtException(HttpServletResponse response, ExpiredJwtException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.onFailure(TokenErrorStatus.ACCESS_TOKEN_EXPIRED, e.getMessage())
                )
        );
    }

    private void handleJwtException(HttpServletResponse response, JwtException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.onFailure(TokenErrorStatus.INVALID_ACCESS_TOKEN, e.getMessage())
                )
        );
    }
}

