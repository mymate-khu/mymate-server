package com.mymate.mymate.term.repository;

import com.mymate.mymate.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findTopByCodeOrderByVersionDesc(String code);
    List<Term> findByRequiredTrue();
}


