package com.softserve.edu.documents.document;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.document.meta.Placeholder;
import com.softserve.edu.entity.device.UnsuitabilityReason;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTestData;

import java.util.List;

/**
 * Represents an unfitness certificate.
 */
@com.softserve.edu.documents.document.meta.Document
public class UnfitnessCertificate extends BaseCertificate {
    /**
     * List of performed tests
     */
    List<CalibrationTestData> calibrationTestDatas;

    /**
     * List of unsuitability reasons
     */
    List<UnsuitabilityReason> unsuitabilityReasons;

    /**
     * Constructor for UnfitnessCertificate
     *
     * @param verification         entity, which contains document's data
     * @param calibrationTest      calibration test that is assigned to the verification
     * @param calibrationTestDatas details about calibration test
     */
    public UnfitnessCertificate(Verification verification, CalibrationTest calibrationTest, List<CalibrationTestData> calibrationTestDatas) {
        super(verification, calibrationTest);
        this.calibrationTestDatas = calibrationTestDatas;
    }

    /**
     * @return Returns the identification number of the accreditation certificate,
     * that the calibrator's company owns.
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
        if (!calibrationTestDatas.isEmpty() && calibrationTestDatas.size() == 3) {
            if (calibrationTestDatas.get(0).getTestResult().equals(Verification.CalibrationTestResult.FAILED)) {
                return reasons + Constants.RATED_FLAW;
            } else if (calibrationTestDatas.get(1).getTestResult().equals(Verification.CalibrationTestResult.FAILED)) {
                return reasons + Constants.TRANSIENT_FLAW;
            } else if (calibrationTestDatas.get(2).getTestResult().equals(Verification.CalibrationTestResult.FAILED)) {
                return reasons + Constants.MINIMAL_FLAW;
            }
        } else {
            return getVerification().getDevice().getUnsuitabilitySet().iterator().next().getName();
        }
        return Constants.NO_REASON;
    }
}