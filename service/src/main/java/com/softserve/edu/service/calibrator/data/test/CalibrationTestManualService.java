package com.softserve.edu.service.calibrator.data.test;


import com.softserve.edu.entity.verification.calibration.CalibrationTestManual;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public interface CalibrationTestManualService {

    CalibrationTestManual findTestManual(Long id);

    void deleteTestManual(String verificationId) throws IOException;

    CalibrationTestManual createNewTestManual(String pathToScan, String numberOfTest, Long moduleId, Date dateTest);

    String uploadScanDoc(InputStream file, String originalFileFullName, Long id) throws IOException;

    void editTestManual(String pathToScanDoc, Date dateOfTest, String numberOfTest, Long moduleId , CalibrationTestManual calibrationTestManual);

    void deleteScanDoc(String uri, Long id) throws IOException;

    byte[] getScanDoc(String uri) throws IOException;


}
