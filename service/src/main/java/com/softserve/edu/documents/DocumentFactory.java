package com.softserve.edu.documents;

import com.softserve.edu.documents.document.Document;
import com.softserve.edu.documents.document.InfoDocument;
import com.softserve.edu.documents.document.UnfitnessCertificate;
import com.softserve.edu.documents.document.VerificationCertificate;
import com.softserve.edu.documents.resources.DocumentType;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTestData;
import org.springframework.util.Assert;

import java.util.List;

public class DocumentFactory {
    /**
     * Creates and returns a document. Document type is determined by documentType's type.
     *
     * @param documentType    by which te type of the document is determined
     * @param verification    verification for this document
     * @param calibrationTest calibration test for this document
     * @return created document
     */
    public static Document build(DocumentType documentType, Verification verification,
                                 CalibrationTest calibrationTest) {
        Assert.notNull(verification, Verification.class.getSimpleName()
                + " can't be null");
        Assert.notNull(calibrationTest, CalibrationTest.class.getSimpleName()
                + " can't be null");
        Assert.notNull(documentType, CalibrationTest.class.getSimpleName()
                + " can't be null");

        Document document;

        switch (documentType) {
            case VERIFICATION_CERTIFICATE:
                document = new VerificationCertificate(verification, calibrationTest);
                break;
            case UNFITNESS_CERTIFICATE:
                List<CalibrationTestData> calibrationTestDatas = calibrationTest.getCalibrationTestDataList();
                document = new UnfitnessCertificate(verification, calibrationTest, calibrationTestDatas);
                break;
            default:
                throw new IllegalArgumentException(documentType.name() + "is not supported");
        }

        return document;
    }

    public static Document buildInfoDoc(DocumentType documentType, Verification verification) {

        Assert.notNull(verification, Verification.class.getSimpleName() + " can't be null");
        Assert.notNull(documentType, CalibrationTest.class.getSimpleName() + " can't be null");

        Document document;
        document = new InfoDocument(verification);
        return document;
    }
}
