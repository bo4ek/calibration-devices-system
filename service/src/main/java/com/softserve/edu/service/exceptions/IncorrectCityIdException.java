package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IncorrectCityIdException extends Exception{
    public IncorrectCityIdException(String format) {
        super(format);
    }
}

