package com.softserve.edu.documents.document;

import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;

/**
 * Represents a verification certificate.
 */
@com.softserve.edu.documents.document.meta.Document
public class VerificationCertificate extends BaseCertificate {

    /**
     * Constructor for VerificationCertificate
     *
     * @param verification    entity to get document's data from
     * @param calibrationTest one of calibration test that is assigned to
     *                        the verification
     */
    public VerificationCertificate(Verification verification, CalibrationTest calibrationTest) {
        super(verification, calibrationTest);
    }
}