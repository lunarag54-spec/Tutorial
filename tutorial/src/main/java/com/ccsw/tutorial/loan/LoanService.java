package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface LoanService {

    Page<Loan> find(Long gameId, Long clientId, LocalDate date, Pageable pageable);

    Loan create(LoanRequestDto dto);

    Loan update(Long id, LoanRequestDto dto);

    void delete(Long id);
}
