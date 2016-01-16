package com.softserve.edu.service.calibrator.data.test.impl;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTestDataManual;
import com.softserve.edu.entity.verification.calibration.CalibrationTestManual;
import com.softserve.edu.repository.CalibrationModuleRepository;
import com.softserve.edu.repository.CalibrationTestManualRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestDataManualService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestManualService;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Created by Misha on 12/13/2015.
 */

@Setter
@Service
public class CalibrationTestManualServiceImpl implements CalibrationTestManualService {

    @Value("${document.storage.local}")
    private String localStorage;

    @Autowired
    private CalibrationTestManualRepository calibrationTestManualRepository;

    @Autowired
    private CalibrationModuleRepository calibrationModuleRepository;

    @Autowired
    private CalibrationTestDataManualService calibrationTestDataManualService;

    @Autowired
    private VerificationRepository verificationRepository;

    private Logger logger = Logger.getLogger(CalibrationTestManualServiceImpl.class);

    @Override
    public CalibrationTestManual findTestManual(Long id) {
        return calibrationTestManualRepository.findOne(id);
    }

    @Override
    @Transactional
    public void deleteTestManual(String verificationId) throws IOException {
        CalibrationTestDataManual cTestDataManual = calibrationTestDataManualService.findByVerificationId(verificationId);
        CalibrationTestManual calibrationTestManual = cTestDataManual.getCalibrationTestManual();
        String pathToScan = calibrationTestManual.getPathToScan();
        if (pathToScan != null) deleteScanDoc(pathToScan, null);
        List<CalibrationTestDataManual> list = calibrationTestManual.getCalibrationTestDataManual();
        Verification verification;
        for (CalibrationTestDataManual testDataManual : list) {
            verification = testDataManual.getVerification();
            verification.setStatus(Status.IN_PROGRESS);
            verificationRepository.save(verification);
        }
        calibrationTestManualRepository.delete(calibrationTestManual);
    }

    @Override
    @Transactional
    public CalibrationTestManual createNewTestManual(String pathToScan, Integer numberOfTest, Long moduleId, Date dateTest) {
        CalibrationModule calibrationModule = calibrationModuleRepository.findOne(moduleId);
        CalibrationTestManual calibrationTestManual = new CalibrationTestManual(pathToScan, numberOfTest
                , generateNumber(dateTest, calibrationModule.getModuleNumber(), numberOfTest), dateTest, calibrationModule);
        return calibrationTestManualRepository.save(calibrationTestManual);
    }


    @Override
    public String uploadScanDoc(InputStream file, String originalFileFullName, Long id) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        UUID uuid = UUID.randomUUID();
        String uri = null;
        try {
            uri = uuid.toString();
            Path path = Paths.get(localStorage + uuid);

            Files.createDirectories(path);
            is = new BufferedInputStream(file);
            os = new BufferedOutputStream(new FileOutputStream(path.toString() + File.separator + originalFileFullName));
            int value;
            while ((value = is.read()) != -1) {
                os.write(value);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
        setPathToScan(id, uri);
        return uri;
    }

    @Override
    @Transactional
    public void editTestManual(String pathToScanDoc, Date dateOfTest, Integer numberOfTest, Long moduleId, CalibrationTestManual calibrationTestManual) {
        CalibrationModule calibrationModule = calibrationModuleRepository.findOne(moduleId);
        calibrationTestManual.setPathToScan(pathToScanDoc);
        calibrationTestManual.setDateTest(dateOfTest);
        calibrationTestManual.setNumberOfTest(numberOfTest);
        calibrationTestManual.setCalibrationModule(calibrationModule);
        calibrationTestManual.setGenerateNumberTest(generateNumber(dateOfTest, calibrationModule.getModuleNumber(), numberOfTest));
        calibrationTestManualRepository.save(calibrationTestManual);
    }

    @Override
    public void deleteScanDoc(String uri, Long id) throws IOException {
        Path dirToDel = Paths.get(localStorage + uri);
        try {
            Files.walkFileTree(dirToDel, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        logger.error("Exception while iterating directory");
                        throw e;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IOException(e);
        }
        if (id != null) {
            setPathToScan(id, null);
        }
    }


    @Override
    public byte[] getScanDoc(String uri) throws IOException {
        byte[] scanDoc;
        Path scanDocPath = Paths.get(localStorage + uri);
        DirectoryStream<Path> pathDirectoryStream = Files.newDirectoryStream(scanDocPath);
        Iterator<Path> iterator = pathDirectoryStream.iterator();
        scanDoc = Files.readAllBytes(iterator.next());
        return scanDoc;
    }


    private Long generateNumber(Date dateOfTest, String moduleNumber, Integer numberOfTest) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOfTest);
        int year = cal.get(Calendar.YEAR);
        int month = (cal.get(Calendar.MONTH)) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        StringBuffer number = new StringBuffer(moduleNumber).append(day).append(month).append(year).append(numberOfTest);
        return Long.valueOf(number.toString());
    }

    private void setPathToScan(Long id, String pathToScan) {
        if (id != 0) {
            CalibrationTestManual testManual = findTestManual(id);
            testManual.setPathToScan(pathToScan);
            calibrationTestManualRepository.save(testManual);
        }
    }


}
