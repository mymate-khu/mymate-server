package com.mymate.mymate.auth.token;

import com.mymate.mymate.member.enums.Role;

public interface RefreshTokenStore {

    void saveOnLogin(String refreshRaw, String uid, String sid, String familyId, long iat, long exp);

    RotateResult rotate(String oldRefreshRaw, String uid, String sid, String familyId);

    // JWT 토큰 기반 회전 메서드
    RotateResult rotateWithJwt(String oldRefreshRaw, String uid, String sid, String familyId, 
                              Long id, String email, String name, Role role, boolean isSignUpCompleted);

    void invalidateFamily(String uid, String familyId);

    record RotateResult(String newRefreshRaw, String tokenHash, String uid, String sid, String familyId) {}
}


