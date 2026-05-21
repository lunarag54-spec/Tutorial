package com.ccsw.tutorial.loan.exception;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(Long id) {
        super("No existe un préstamo con el identificador " + id);
    }
}
