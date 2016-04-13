package com.softserve.edu.service.calibrator.data.test.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.calibration.CalibrationTestData;
import com.softserve.edu.repository.CalibrationModuleRepository;
import com.softserve.edu.repository.CalibrationTestDataRepository;
import com.softserve.edu.repository.CalibrationTestRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestDataService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.service.exceptions.DuplicateCalibrationTestException;
import com.softserve.edu.service.exceptions.InvalidModuleIdException;
import com.softserve.edu.service.exceptions.NotAvailableException;
import com.softserve.edu.service.tool.MailService;
import com.softserve.edu.service.utils.CalibrationTestDataList;
import com.softserve.edu.service.utils.CalibrationTestList;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@Service
public class CalibrationTestServiceImpl implements CalibrationTestService {

    private static final int POSITION_OF_PROTOCOL = 6;

    @Value("${photo.storage.local}")
    private String localStorage;

    @Autowired
    private CalibrationTestRepository calibrationTestRepository;
    @Autowired
    private CalibrationTestDataService testDataService;
    @Autowired
    private CalibrationTestDataRepository dataRepository;
    @Autowired
    private VerificationRepository verificationRepository;
    @Autowired
    private CalibrationModuleRepository calibrationModuleRepository;
    @Autowired
    MailService mailService;

    private Logger logger = Logger.getLogger(CalibrationTestServiceImpl.class);

    @Override
    @Transactional
    public long createNewTest(DeviceTestData deviceTestData, String verificationId) throws IOException, InvalidModuleIdException, DuplicateCalibrationTestException {
        Verification verification = verificationRepository.findOne(verificationId);
        CalibrationModule moduleId = calibrationModuleRepository.findByModuleNumber(deviceTestData.getInstallmentNumber());
        if (moduleId == null) {
            throw new InvalidModuleIdException();
        }
        if(calibrationTestRepository.existByVerificationId(verificationId)) {
            throw new DuplicateCalibrationTestException();
        }
        CalibrationTest calibrationTest = new CalibrationTest(deviceTestData.getFileName(),
                deviceTestData.getLatitude(), deviceTestData.getLongitude(), deviceTestData.getUnixTime(), verification,
                deviceTestData.getInitialCapacity());
        BufferedImage buffered = ImageIO.read(new ByteArrayInputStream(
                Base64.decodeBase64(deviceTestData.getTestPhoto())));
        String testPhoto = Constants.MAIN_PHOTO + Constants.DOT + Constants.IMAGE_TYPE;
        String folderPath = localStorage + File.separator + verificationId;
        String absolutePath = localStorage + File.separator + verificationId + File.separator + testPhoto;
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        ImageIO.write(buffered, Constants.IMAGE_TYPE, new File(absolutePath));
        calibrationTest.setPhotoPath(testPhoto);

        calibrationTestRepository.save(calibrationTest);

        for (int testDataId = 1; testDataId <= Constants.TEST_COUNT; testDataId++) {
            if (deviceTestData.getTestNumber(testDataId) != 0) { // if there is no photo there is now test data
                CalibrationTestData сalibrationTestData = testDataService.createNewTestData(calibrationTest.getId(),
                        deviceTestData, testDataId);
                if (сalibrationTestData.getTestResult().equals(Verification.CalibrationTestResult.FAILED)) {
                    calibrationTest.setTestResult(Verification.CalibrationTestResult.FAILED);
                }
                if (сalibrationTestData.getTestResult().equals(Verification.CalibrationTestResult.NOT_PROCESSED)) {
                    calibrationTest.setTestResult(Verification.CalibrationTestResult.FAILED);
                }
                if (сalibrationTestData.getConsumptionStatus().equals(Verification.ConsumptionStatus.NOT_IN_THE_AREA)) {
                    calibrationTest.setConsumptionStatus(Verification.ConsumptionStatus.NOT_IN_THE_AREA);
                }
            }
        }
        calibrationTestRepository.save(calibrationTest);
        verification.setStatus(Status.TEST_COMPLETED);


        verification.setCalibrationModule(moduleId);
        verification.setNumberOfProtocol(deviceTestData.getFileName().substring(POSITION_OF_PROTOCOL,
                deviceTestData.getFileName().indexOf('.')));
        verificationRepository.save(verification);
        return calibrationTest.getId();
    }

    @Override
    @Transactional
    public CalibrationTest findTestById(Long testId) {
        return calibrationTestRepository.findOne(testId);
    }

    @Override
    @Transactional
    public CalibrationTest findByVerificationId(String verifId) {
        return calibrationTestRepository.findByVerificationId(verifId);
    }

    @Override
    @Transactional
    public CalibrationTestList findAllCalibrationTests() {
        List<CalibrationTest> list = (ArrayList<CalibrationTest>) calibrationTestRepository.findAll();
        return new CalibrationTestList(list);
    }

    @Override
    @Transactional
    public Page<CalibrationTest> getCalibrationTestsBySearchAndPagination(int pageNumber,
                                                                          int itemsPerPage, String search) {
        PageRequest pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
        return search.equalsIgnoreCase("null") ? calibrationTestRepository.findAll(pageRequest) :
                calibrationTestRepository.findByNameLikeIgnoreCase("%" + search + "%", pageRequest);
    }

    @Override
    @Transactional
    public CalibrationTest editTest(Long testId, String name, String capacity, Integer settingNumber,
                                    Double latitude, Double longitude, Verification.ConsumptionStatus consumptionStatus,
                                    Verification.CalibrationTestResult testResult) {
        CalibrationTest calibrationTest = calibrationTestRepository.findOne(testId);
        testResult = Verification.CalibrationTestResult.SUCCESS;
        calibrationTest.setName(name);
        calibrationTest.setCapacity(capacity);
        calibrationTest.setLatitude(latitude);
        calibrationTest.setLongitude(longitude);
        calibrationTest.setConsumptionStatus(consumptionStatus);
        calibrationTest.setTestResult(testResult);
        return calibrationTest;
    }

    @Override
    @Transactional
    public void deleteTest(Long testId) {
        calibrationTestRepository.delete(testId);
    }

    @Override
    @Transactional
    public void createTestData(Long testId, CalibrationTestData testData) {
        CalibrationTest calibrationTest = calibrationTestRepository.findOne(testId);
        testData.setCalibrationTest(calibrationTest);
        dataRepository.save(testData);
    }

    @Override
    @Transactional
    public CalibrationTestDataList findAllTestDataAsociatedWithTest(Long calibrationTestId) {
        CalibrationTest calibrationTest = calibrationTestRepository.findOne(calibrationTestId);
        if (calibrationTest == null) {
            throw new NotAvailableException("Тесту з таким ID не існує!");
        } else {
            return new CalibrationTestDataList(calibrationTestId
                    , dataRepository.findByCalibrationTestId(calibrationTestId));
        }
    }

    @Override
    public String getPhotoAsString(String photoPath, CalibrationTest calibrationTest) {
        String photo = null;
        InputStream reader = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            reader = new FileInputStream(localStorage + File.separator + calibrationTest.getVerification().getId()
                    + File.separator + photoPath);
            bufferedInputStream = new BufferedInputStream(reader);
            BufferedImage image = ImageIO.read(bufferedInputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, Constants.IMAGE_TYPE, baos);
            byte[] bytesOfImages = Base64.encodeBase64(baos.toByteArray());
            photo = new String(bytesOfImages);
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error(e); // for prevent critical issue "Either log or rethrow this exception"
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
                logger.error(e); // for prevent critical issue "Either log or rethrow this exception"
            }
        }
        return photo;
    }

    @Override
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

    @Override
    @Transactional
    public void createNewCalibrationTestData(CalibrationTestData calibrationTestData) {
        dataRepository.save(calibrationTestData);
    }

    @Override
    @Transactional
    public CalibrationTest createNewCalibrationTest(Long testId, String name, String capacity, Integer settingNumber,
                                                    Double latitude, Double longitude) {
        Verification.CalibrationTestResult testResult;
        CalibrationTest calibrationTest = calibrationTestRepository.findOne(testId);
        testResult = Verification.CalibrationTestResult.SUCCESS;
        Date initial = new Date();
        calibrationTest.setName(name);
        calibrationTest.setDateTest(initial);
        calibrationTest.setCapacity(capacity);
        calibrationTest.setLatitude(latitude);
        calibrationTest.setLongitude(longitude);
        calibrationTest.setConsumptionStatus(Verification.ConsumptionStatus.IN_THE_AREA);
        calibrationTest.setTestResult(testResult);
        return calibrationTest;
    }

    @Override
    @Transactional
    public void updateTest(String verificationId, String status) {
        Verification verification = verificationRepository.findOne(verificationId);
        updateTest(verification, status);
    }

    @Override
    @Transactional
    public void updateTest(Verification verification, String status) {
        Status statusReceived = Status.valueOf(status.toUpperCase());
        if (statusReceived.equals(Status.TEST_OK) || statusReceived.equals(Status.TEST_NOK)) {
            String statusToSend = statusReceived.equals(Status.TEST_OK) ? Constants.TEST_OK : Constants.TEST_NOK;
            verification.setStatus(statusReceived);
            mailService.sendPassedTestMail(verification.getClientData().getEmail(), verification.getId(), statusToSend);
            if (verification.getProviderEmployee() != null) {
                mailService.sendPassedTestMail(verification.getProviderEmployee().getEmail(), verification.getId(), statusToSend);
            }
            verificationRepository.save(verification);
        } else {
            verification.setStatus(Status.TEST_COMPLETED);
        }
    }
}