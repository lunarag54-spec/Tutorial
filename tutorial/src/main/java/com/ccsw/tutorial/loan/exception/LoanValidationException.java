package com.ccsw.tutorial.loan.exception;


public class LoanValidationException extends RuntimeException {

    private final LoanValidationErrorCode code;

    public LoanValidationException(LoanValidationErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public LoanValidationErrorCode getCode() {
        return code;
    }
}
