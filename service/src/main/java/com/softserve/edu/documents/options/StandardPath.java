package com.softserve.edu.documents.options;

/**
 * Constants for standard paths
 */
public enum StandardPath {
    DOCUMENTS_TEMPLATES {
        @Override
        public String toString() {
            return "documentsTemplates";
        }
    },
    FONTS {
        @Override
        public String toString() {
            return "fonts";
        }
    }
}