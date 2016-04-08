package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IncorrectOrganizationException extends Exception {
    public IncorrectOrganizationException(String format) {
        super(format);
    }
}
