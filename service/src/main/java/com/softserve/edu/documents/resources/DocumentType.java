package com.softserve.edu.documents.resources;

/**
 * Contains types of documents supported by the application.
 */
public enum DocumentType {
    VERIFICATION_CERTIFICATE,
    UNFITNESS_CERTIFICATE,
    INFO_DOCUMENT,
    PROVIDER_EMPLOYEES_REPORTS,
    PROVIDER_CALIBRATORS_REPORTS,
    PROVIDER_VERIFICATION_RESULT_REPORTS,
    VERIFICATOR_VERIFICATION_RESULT_REPORTS,
    ADMIN_REPORT_BY_CALIBRATORS_MODULE,
    PROVIDER_VERIFICATION_REJECTED_REPORTS,
    PROVIDER_REPORTS,
    CALIBRATOR_VERIFICATION_RESULT_REPORTS;

    /**
     * @return the template's name with format
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }

}