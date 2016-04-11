package com.softserve.edu.documents.utils;

/**
 * Contains formatting tokens.
 */
public enum FormattingTokens {
    /**
     * The text after this token must be placed on the right side of the page.
     */
    RIGHT_SIDE("#"),
    /**
     * The text after this token is a column's name.
     */
    COLUMN("$"),

    BOLD("@");

    private String token;

    FormattingTokens(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
}
