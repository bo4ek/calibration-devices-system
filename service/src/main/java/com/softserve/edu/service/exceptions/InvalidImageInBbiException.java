package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidImageInBbiException extends Exception {
    public InvalidImageInBbiException(String format) {
        super(format);
    }
}
