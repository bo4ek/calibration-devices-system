package com.softserve.edu.documents.document;

import com.softserve.edu.common.Constants;
import com.softserve.edu.documents.document.meta.Placeholder;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTestData;
import com.softserve.edu.service.calibrator.data.test.impl.CalibrationTestServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        CalibrationTestResult firstResult = NOT_PROCESSED;
        CalibrationTestResult secondResult = NOT_PROCESSED;
        CalibrationTestResult thirdResult = NOT_PROCESSED;
        CalibrationTestResult notProcessed = NOT_PROCESSED;
        CalibrationTestResult failed = FAILED;
        String error1 = Constants.NOT_SPECIFIED;
        String error2 = Constants.NOT_SPECIFIED;
        String error3 = Constants.NOT_SPECIFIED;

        try {
            if (verification.isManual() && verification.getCalibrationTestDataManualId() != null) {
                firstResult = verification.getCalibrationTestDataManualId().getStatusTestFirst();
                secondResult = verification.getCalibrationTestDataManualId().getStatusTestSecond();
                thirdResult = verification.getCalibrationTestDataManualId().getStatusTestThird();
                if (firstResult.equals(notProcessed) || secondResult.equals(notProcessed) || thirdResult.equals(notProcessed)) {
                    return verification.getCalibrationTestDataManualId().getUnsuitabilityReason().getName();
                }

            } else {

                List<CalibrationTestData> calibrationTestDatas = calibrationTest.getCalibrationTestDataList();
                for (CalibrationTestData calibration : getLatestTests(calibrationTestDatas)) {
                    switch (calibration.getTestPosition()) {
                        case 10:
                        case 11:
                        case 12:
                        case 13: {
                            firstResult = calibration.getTestResult();
                            error1 = getErrorFromTest(calibration);
                            break;
                        }
                        case 20:
                        case 21:
                        case 22:
                        case 23: {
                            secondResult = calibration.getTestResult();
                            error2 = getErrorFromTest(calibration);
                            break;
                        }
                        case 30:
                        case 31:
                        case 32:
                        case 33: {
                            thirdResult = calibration.getTestResult();
                            error3 = getErrorFromTest(calibration);
                            break;
                        }
                    }
                }

                if (firstResult.equals(notProcessed) || secondResult.equals(notProcessed) || thirdResult.equals(notProcessed)) {
                    return calibrationTest.getUnsuitabilityReason().getName();
                }
            }

            if (firstResult.equals(failed) ) {
                reasons.add(String.format("δQn%s%%", error1.replace('.', ',')));
            }
            if (secondResult.equals(failed)) {
                reasons.add(String.format("δQt%s%%", error2.replace('.', ',')));
            }
            if (thirdResult.equals(failed)) {
                reasons.add(String.format("δQmin%s%%", error3.replace('.', ',')));
            }
            return Constants.ERROR_PATTERN + " " + String.join("; ", reasons);
        } catch (Exception e) {

            logger.error("Test results for this verification are corrupted ", e);
            return Constants.NOT_SPECIFIED;
        }
    }

    public Set<CalibrationTestData> getLatestTests(List<CalibrationTestData> rawListOfCalibrationTestData) {
        Set<CalibrationTestData> setOfCalibrationTestData = new LinkedHashSet<>();
        Integer position;
        for (CalibrationTestData calibrationTestData : rawListOfCalibrationTestData) {
            position = calibrationTestData.getTestPosition();
            position++;
            for (CalibrationTestData calibrationTestDataSearch : rawListOfCalibrationTestData) {
                if (calibrationTestDataSearch.getTestPosition().equals(position)) {
                    calibrationTestData = calibrationTestDataSearch;
                    position++;
                }
            }
            setOfCalibrationTestData.add(calibrationTestData);
        }
        return setOfCalibrationTestData;
    }

    private String prepareAcceptableError(Double error) {
        StringBuilder builder = new StringBuilder();
        if(error < 0) {
            builder.append(" = мінус ");
            error *= -1;
        } else {
            builder.append(" = ");
        }
        builder.append(error);
        return builder.toString();
    }

    public String getErrorFromTest(CalibrationTestData calibrationTestData) {
        if (!verification.isManual()) {
            return prepareAcceptableError(calibrationTestData.getCalculationError());
        } else {
            return Constants.NOT_SPECIFIED;
        }
    }
}