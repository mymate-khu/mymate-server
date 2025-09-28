package com.mymate.mymate.puzzle.repository;

import com.mymate.mymate.puzzle.entity.Puzzle;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PuzzleRepositoryImpl implements PuzzleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Puzzle> findByConditions(Long memberId, PuzzleStatus status, String category,
                                        LocalDate startDate, LocalDate endDate, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Puzzle p WHERE p.memberId = :memberId");
        
        if (status != null) {
            jpql.append(" AND p.status = :status");
        }
        if (category != null && !category.trim().isEmpty()) {
            jpql.append(" AND p.category = :category");
        }
        if (startDate != null) {
            jpql.append(" AND p.scheduledDate >= :startDate");
        }
        if (endDate != null) {
            jpql.append(" AND p.scheduledDate <= :endDate");
        }
        
        jpql.append(" ORDER BY p.scheduledDate DESC, p.createdAt DESC");

        TypedQuery<Puzzle> query = entityManager.createQuery(jpql.toString(), Puzzle.class);
        query.setParameter("memberId", memberId);
        
        if (status != null) {
            query.setParameter("status", status);
        }
        if (category != null && !category.trim().isEmpty()) {
            query.setParameter("category", category);
        }
        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        // 페이징 적용
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Puzzle> puzzles = query.getResultList();
        
        // 전체 개수 조회
        String countJpql = jpql.toString().replace("SELECT p FROM", "SELECT COUNT(p) FROM");
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        countQuery.setParameter("memberId", memberId);
        
        if (status != null) {
            countQuery.setParameter("status", status);
        }
        if (category != null && !category.trim().isEmpty()) {
            countQuery.setParameter("category", category);
        }
        if (startDate != null) {
            countQuery.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            countQuery.setParameter("endDate", endDate);
        }

        long total = countQuery.getSingleResult();
        
        return new PageImpl<>(puzzles, pageable, total);
    }

    @Override
    public Page<Puzzle> searchByText(Long memberId, String searchText, Pageable pageable) {
        String jpql = "SELECT p FROM Puzzle p WHERE p.memberId = :memberId " +
                     "AND (LOWER(p.title) LIKE LOWER(:searchText) OR LOWER(p.description) LIKE LOWER(:searchText)) " +
                     "ORDER BY p.scheduledDate DESC, p.createdAt DESC";
        
        TypedQuery<Puzzle> query = entityManager.createQuery(jpql, Puzzle.class);
        query.setParameter("memberId", memberId);
        query.setParameter("searchText", "%" + searchText + "%");
        
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<Puzzle> puzzles = query.getResultList();
        
        // 전체 개수 조회
        String countJpql = "SELECT COUNT(p) FROM Puzzle p WHERE p.memberId = :memberId " +
                          "AND (LOWER(p.title) LIKE LOWER(:searchText) OR LOWER(p.description) LIKE LOWER(:searchText))";
        
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        countQuery.setParameter("memberId", memberId);
        countQuery.setParameter("searchText", "%" + searchText + "%");
        
        long total = countQuery.getSingleResult();
        
        return new PageImpl<>(puzzles, pageable, total);
    }

    @Override
    public Page<Puzzle> findByPriority(Long memberId, String priority, Pageable pageable) {
        String jpql = "SELECT p FROM Puzzle p WHERE p.memberId = :memberId AND p.priority = :priority " +
                     "ORDER BY p.scheduledDate DESC, p.createdAt DESC";
        
        TypedQuery<Puzzle> query = entityManager.createQuery(jpql, Puzzle.class);
        query.setParameter("memberId", memberId);
        query.setParameter("priority", priority);
        
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<Puzzle> puzzles = query.getResultList();
        
        // 전체 개수 조회
        String countJpql = "SELECT COUNT(p) FROM Puzzle p WHERE p.memberId = :memberId AND p.priority = :priority";
        
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        countQuery.setParameter("memberId", memberId);
        countQuery.setParameter("priority", priority);
        
        long total = countQuery.getSingleResult();
        
        return new PageImpl<>(puzzles, pageable, total);
    }

    @Override
    public List<Puzzle> findPuzzlesToGenerateRecurrence(LocalDate targetDate) {
        String jpql = "SELECT p FROM Puzzle p WHERE p.recurrenceType != 'NONE' " +
                     "AND p.recurrenceEndDate >= :targetDate " +
                     "AND p.parentPuzzleId IS NULL";
        
        TypedQuery<Puzzle> query = entityManager.createQuery(jpql, Puzzle.class);
        query.setParameter("targetDate", targetDate);
        
        return query.getResultList();
    }

    @Override
    public long countByMemberIdAndStatus(Long memberId, PuzzleStatus status) {
        String jpql = "SELECT COUNT(p) FROM Puzzle p WHERE p.memberId = :memberId AND p.status = :status";
        
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("memberId", memberId);
        query.setParameter("status", status);
        
        return query.getSingleResult();
    }

    @Override
    public long countByMemberIdAndDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        String jpql = "SELECT COUNT(p) FROM Puzzle p WHERE p.memberId = :memberId " +
                     "AND p.scheduledDate BETWEEN :startDate AND :endDate";
        
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("memberId", memberId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getSingleResult();
    }

    @Override
    public long countCompletedByMemberIdAndDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        String jpql = "SELECT COUNT(p) FROM Puzzle p WHERE p.memberId = :memberId " +
                     "AND p.scheduledDate BETWEEN :startDate AND :endDate AND p.status = 'DONE'";
        
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("memberId", memberId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getSingleResult();
    }
}
