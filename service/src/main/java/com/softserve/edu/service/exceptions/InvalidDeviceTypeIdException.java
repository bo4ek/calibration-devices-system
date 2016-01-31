package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidDeviceTypeIdException extends Exception {
    public InvalidDeviceTypeIdException(String format) {
        super(format);
    }
}
