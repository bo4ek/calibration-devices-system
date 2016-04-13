package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DuplicateCalibrationTestException extends Exception{
    public DuplicateCalibrationTestException(String format) {
        super(format);
    }
}
