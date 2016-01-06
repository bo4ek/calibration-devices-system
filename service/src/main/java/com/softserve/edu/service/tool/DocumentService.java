package com.softserve.edu.service.tool;

import com.softserve.edu.documents.parameter.FileFormat;
import com.softserve.edu.documents.resources.DocumentType;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;
import org.apache.commons.vfs2.FileObject;

public interface DocumentService {

    FileObject buildFile(String verificationCode, DocumentType documentType, FileFormat fileFormat);

    FileObject buildFile(String verificationCode, Long calibrationTestID, DocumentType documentType, FileFormat fileFormat);

    FileObject buildFile(String verificationCode, FileFormat fileFormat);

    FileObject buildFile(DocumentType documentType, Verification verification, CalibrationTest calibrationTest, FileFormat fileFormat);

    FileObject buildInfoFile(String verificationCode, FileFormat fileFormat);
}
