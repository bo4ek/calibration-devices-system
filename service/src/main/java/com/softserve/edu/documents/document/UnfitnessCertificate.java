package com.softserve.edu.documents.document;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.document.meta.Placeholder;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;
import org.apache.log4j.Logger;

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
        StringBuilder sb = new StringBuilder(Constants.MEASURING_ERROR_MESSAGE);
        Verification.CalibrationTestResult first;
        Verification.CalibrationTestResult second;
        Verification.CalibrationTestResult third;
        Verification.CalibrationTestResult notProcessed = Verification.CalibrationTestResult.NOT_PROCESSED;
        Verification.CalibrationTestResult failed = Verification.CalibrationTestResult.FAILED;

        if (verification.isManual()) {
            first = verification.getCalibrationTestDataManualId().getStatusTestFirst();
            second = verification.getCalibrationTestDataManualId().getStatusTestSecond();
            third = verification.getCalibrationTestDataManualId().getStatusTestThird();

            if (first.equals(notProcessed) || second.equals(notProcessed) || third.equals(notProcessed)) {
                return verification.getCalibrationTestDataManualId().getUnsuitabilityReason().getName();
            }
            if (first.equals(failed)) {
                sb.append(" ").append(Constants.RATED_FLAW).append(",");
            }
            if (second.equals(failed)) {
                sb.append(" ").append(Constants.TRANSIENT_FLAW).append(",");
            }
            if (third.equals(failed)) {
                sb.append(" ").append(Constants.MINIMAL_FLAW);
            }
            return sb.toString();

        } else {
            first = calibrationTest.getCalibrationTestDataList().get(0).getTestResult();
            second = calibrationTest.getCalibrationTestDataList().get(1).getTestResult();
            third = calibrationTest.getCalibrationTestDataList().get(2).getTestResult();

            if (first.equals(notProcessed) || second.equals(notProcessed) || third.equals(notProcessed)) {
                return calibrationTest.getUnsuitabilityReason().getName();
            }
            if (first.equals(failed)) {
                sb.append(" ").append(Constants.RATED_FLAW).append(",");
            }
            if (second.equals(failed)) {
                sb.append(" ").append(Constants.TRANSIENT_FLAW).append(",");
            }
            if (third.equals(failed)) {
                sb.append(" ").append(Constants.MINIMAL_FLAW);
            }
            return sb.toString();
        }
    }
}