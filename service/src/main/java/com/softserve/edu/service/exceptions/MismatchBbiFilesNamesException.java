package com.softserve.edu.service.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MismatchBbiFilesNamesException extends Exception {
    public MismatchBbiFilesNamesException(String format) {
        super(format);
    }
}
