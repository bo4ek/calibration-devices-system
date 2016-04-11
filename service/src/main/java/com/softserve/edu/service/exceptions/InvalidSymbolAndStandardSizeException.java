package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidSymbolAndStandardSizeException extends Exception{
    public InvalidSymbolAndStandardSizeException(String format) {
        super(format);
    }
}
