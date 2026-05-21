package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    boolean existsByGame_Id(Long gameId);

    boolean existsByClient_Id(Long clientId);

    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Loan l WHERE l.game.id = :gameId AND l.startDate <= :rangeEnd AND l.endDate >= :rangeStart AND (:excludeId IS NULL OR l.id <> :excludeId)")
    boolean existsOverlappingLoanForGame(@Param("gameId") Long gameId, @Param("rangeStart") LocalDate rangeStart,
            @Param("rangeEnd") LocalDate rangeEnd, @Param("excludeId") Long excludeId);

    
    @Query("SELECT l FROM Loan l WHERE l.client.id = :clientId AND l.startDate <= :rangeEnd AND l.endDate >= :rangeStart AND (:excludeId IS NULL OR l.id <> :excludeId)")
    @EntityGraph(attributePaths = { "game", "client" })
    List<Loan> findOverlappingForClient(@Param("clientId") Long clientId, @Param("rangeStart") LocalDate rangeStart,
            @Param("rangeEnd") LocalDate rangeEnd, @Param("excludeId") Long excludeId);

    @EntityGraph(attributePaths = { "game", "client", "game.category", "game.author" })
    @Override
    java.util.Optional<Loan> findById(Long id);

    @EntityGraph(attributePaths = { "game", "client", "game.category", "game.author" })
    Page<Loan> findAll(Specification<Loan> spec, Pageable pageable);
}
