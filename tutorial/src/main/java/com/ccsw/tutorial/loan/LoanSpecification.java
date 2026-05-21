package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public final class LoanSpecification {

    private LoanSpecification() {
    }

    public static Specification<Loan> withFilters(Long gameId, Long clientId, LocalDate onDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (gameId != null) {
                predicates.add(cb.equal(root.get("game").get("id"), gameId));
            }
            if (clientId != null) {
                predicates.add(cb.equal(root.get("client").get("id"), clientId));
            }
            if (onDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), onDate));
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), onDate));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
