package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidModuleIdException extends Exception {
    public InvalidModuleIdException(String format) {
        super(format);
    }
}
