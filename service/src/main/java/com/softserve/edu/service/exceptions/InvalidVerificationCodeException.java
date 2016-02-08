package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidVerificationCodeException extends Exception {
    public InvalidVerificationCodeException(String format) {
        super(format);
    }
}
