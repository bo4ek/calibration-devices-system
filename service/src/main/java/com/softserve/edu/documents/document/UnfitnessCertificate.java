package com.softserve.edu.documents.document;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.document.meta.Placeholder;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;

/**
 * Represents an unfitness certificate.
 */
@com.softserve.edu.documents.document.meta.Document
public class UnfitnessCertificate extends BaseCertificate {
    /**
     * Constructor for UnfitnessCertificate
     *
     * @param verification         entity, which contains document's data
     * @param calibrationTest      calibration test that is assigned to the verification
     */
    public UnfitnessCertificate(Verification verification, CalibrationTest calibrationTest) {
        super(verification, calibrationTest);
    }

    /**
     * @return Returns the identification number of the
     */
    @Override
    @Placeholder(name = "UNFITNESS_CERTIFICATE_NUMBER")
    public String getVerificationCertificateNumber() {
        return super.getVerificationCertificateNumber();
    }

    /**
     * @return Returns the reason of device's unsuitability
     */
    @Placeholder(name = "REASON_UNSUITABLE")
    public String getReasonUnusable() {
        String reasons = Constants.MEASURING_ERROR_MESSAGE;
        if (!calibrationTest.getCalibrationTestDataList().isEmpty()) {
            if (calibrationTest.getCalibrationTestDataList().get(0).getTestResult().equals(Verification.CalibrationTestResult.FAILED)) {
                return reasons + Constants.RATED_FLAW;
            } else if (calibrationTest.getCalibrationTestDataList().get(1).getTestResult().equals(Verification.CalibrationTestResult.FAILED)) {
                return reasons + Constants.TRANSIENT_FLAW;
            } else if (calibrationTest.getCalibrationTestDataList().get(2).getTestResult().equals(Verification.CalibrationTestResult.FAILED)) {
                return reasons + Constants.MINIMAL_FLAW;
            }
        } else {
            //todo get reason from view, if CalibrationTestResult is not SUCCESS OR FAILED
            return verification.getDevice().getUnsuitabilitySet().iterator().next().getName();
        }
        return Constants.NO_REASON;
    }
}