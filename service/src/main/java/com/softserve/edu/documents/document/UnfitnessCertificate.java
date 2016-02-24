package com.softserve.edu.documents.document;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.document.meta.Placeholder;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.softserve.edu.entity.verification.Verification.*;
import static com.softserve.edu.entity.verification.Verification.CalibrationTestResult.*;

/**
 * Represents an unfitness certificate.
 */
@com.softserve.edu.documents.document.meta.Document
public class UnfitnessCertificate extends BaseCertificate {
    private Logger logger = Logger.getLogger(UnfitnessCertificate.class.getSimpleName());

    /**
     * Constructor for UnfitnessCertificate
     *
     * @param verification    entity, which contains document's data
     * @param calibrationTest calibration test that is assigned to the verification
     */
    public UnfitnessCertificate(Verification verification, CalibrationTest calibrationTest) {
        super(verification, calibrationTest);
    }

    /**
     * @return Returns the identification number of the document
     */
    @Override
    @Placeholder(name = "UNFITNESS_CERTIFICATE_NUMBER")
    public String getVerificationCertificateNumber() {
        return super.getVerificationCertificateNumber();
    }

    /**
     * @return Returns the reason(s) of device's unsuitability
     */
    @Placeholder(name = "REASON_UNSUITABLE")
    public String getReasonUnusable() {
        List<String> reasons = new ArrayList<>();
        CalibrationTestResult firstResult;
        CalibrationTestResult secondResult;
        CalibrationTestResult thirdResult;
        CalibrationTestResult notProcessed = NOT_PROCESSED;
        CalibrationTestResult failed = FAILED;
        try {
            if (verification.isManual()) {
                firstResult = verification.getCalibrationTestDataManualId().getStatusTestFirst();
                secondResult = verification.getCalibrationTestDataManualId().getStatusTestSecond();
                thirdResult = verification.getCalibrationTestDataManualId().getStatusTestThird();

                if (firstResult.equals(notProcessed) || secondResult.equals(notProcessed) || thirdResult.equals(notProcessed)) {
                    return verification.getCalibrationTestDataManualId().getUnsuitabilityReason().getName();
                }
                if (firstResult.equals(failed)) {
                    reasons.add(Constants.RATED_FLAW);
                }
                if (secondResult.equals(failed)) {
                    reasons.add(Constants.TRANSIENT_FLAW);
                }
                if (thirdResult.equals(failed)) {
                    reasons.add(Constants.MINIMAL_FLAW);
                }
                return Constants.MEASURING_ERROR_MESSAGE + String.join(",", reasons);
            } else {
                firstResult = calibrationTest.getCalibrationTestDataList().get(Constants.FIRST_TEST_RESULT).getTestResult();
                secondResult = calibrationTest.getCalibrationTestDataList().get(Constants.SECOND_TEST_RESULT).getTestResult();
                thirdResult = calibrationTest.getCalibrationTestDataList().get(Constants.THIRD_TEST_RESULT).getTestResult();

                if (firstResult.equals(notProcessed) || secondResult.equals(notProcessed) || thirdResult.equals(notProcessed)) {
                    return calibrationTest.getUnsuitabilityReason().getName();
                }
                if (firstResult.equals(failed)) {
                    reasons.add(Constants.RATED_FLAW);
                }
                if (secondResult.equals(failed)) {
                    reasons.add(Constants.TRANSIENT_FLAW);
                }
                if (thirdResult.equals(failed)) {
                    reasons.add(Constants.MINIMAL_FLAW);
                }
                return Constants.MEASURING_ERROR_MESSAGE + String.join(",", reasons);
            }
        } catch (Exception e) {
            logger.error("Test results for this verification are corrupted ", e);
            return Constants.NOT_SPECIFIED;
        }
    }
}