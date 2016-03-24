package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IncorrectStreetIdException extends Exception{
    public IncorrectStreetIdException(String format) {
        super(format);
    }
}
