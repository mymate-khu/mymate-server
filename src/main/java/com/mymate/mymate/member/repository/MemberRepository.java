package com.mymate.mymate.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mymate.mymate.auth.enums.AuthProvider;
import com.mymate.mymate.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByUserId(String userId);
    Optional<Member> findFirstByUserId(String userId);
    Optional<Member> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
    
    @Query("SELECT m FROM Member m WHERE m.username LIKE %:query% AND m.isSignUpCompleted = true")
    List<Member> findByUsernameOrNameContainingIgnoreCase(@Param("query") String query);
}


