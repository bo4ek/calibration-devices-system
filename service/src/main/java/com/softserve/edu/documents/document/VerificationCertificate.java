package com.softserve.edu.documents.document;

import com.softserve.edu.documents.document.meta.*;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;

/**
 * Represents a verification certificate.
 */
@com.softserve.edu.documents.document.meta.Document
public class VerificationCertificate extends BaseCertificate {

    public VerificationCertificate () {
    }

    public VerificationCertificate(Verification verification, CalibrationTest calibrationTest) {
        super(verification, calibrationTest);
    }

    /**
     * @return get the sign of the document, which contains the metrological characteristics
     */
    @Placeholder(name = "COUNTER_TYPE_GOST")
    public String getCounterTypeGost() {
        return getVerification().getCounter().getCounterType().getGost();
    }

    /**
     * @return get the name of the document, which contains the metrological characteristics
     */
    @Placeholder(name = "CALIBRATION_TYPE")
    public String getCalibrationType() {
        return getVerification().getTask().getModule().getCalibrationType();

    }
}
