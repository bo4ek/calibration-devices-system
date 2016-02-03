package com.softserve.edu.controller.calibrator;

import com.softserve.edu.controller.calibrator.util.CalibrationModuleDTOTransformer;
import com.softserve.edu.controller.calibrator.util.CounterTypeDTOTransformer;
import com.softserve.edu.documents.parameter.FileFormat;
import com.softserve.edu.documents.resources.DocumentType;
import com.softserve.edu.dto.*;
import com.softserve.edu.dto.admin.CalibrationModuleDTO;
import com.softserve.edu.dto.admin.CounterTypeDTO;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.UnsuitabilityReason;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.calibration.CalibrationTestData;
import com.softserve.edu.entity.verification.calibration.CalibrationTestDataManual;
import com.softserve.edu.entity.verification.calibration.CalibrationTestManual;
import com.softserve.edu.exceptions.NotFoundException;
import com.softserve.edu.repository.*;
import com.softserve.edu.service.admin.CalibrationModuleService;
import com.softserve.edu.service.admin.CounterTypeService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestDataManualService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestManualService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.service.exceptions.NotAvailableException;
import com.softserve.edu.service.tool.DocumentService;
import com.softserve.edu.service.utils.CalibrationTestDataList;
import com.softserve.edu.service.utils.CalibrationTestList;
import com.softserve.edu.service.verification.VerificationService;
import org.apache.commons.vfs2.FileObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/calibrator/calibrationTests/")
public class CalibrationTestController {

    @Autowired
    private CalibrationTestRepository testRepository;

    @Autowired
    private CalibrationTestDataRepository testDataRepository;

    @Autowired
    private UnsuitabilityReasonRepository unsuitabilityReasonRepository;

    @Autowired
    private CalibrationTestService testService;

    private final Logger logger = Logger.getLogger(CalibrationTestController.class);

    private static final String contentExtPattern = "^.*\\.(jpg|JPG|gif|GIF|png|PNG|tif|TIF|)$";

    private static final String contentDocExtPattern = "^.*\\.(PDF|pdf|jpg|JPG|gif|GIF|png|PNG|tif|TIF|)$";

    @Autowired
    private CalibrationModuleService calibrationModuleService;

    @Autowired
    private CalibrationTestManualService calibrationTestManualService;

    @Autowired
    private CalibrationTestDataManualService calibrationTestDataManualService;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private CounterTypeRepository counterTypeRepository;

    @Autowired
    private CounterTypeService counterTypeService;

    @Autowired
    private VerificationService verificationService;
    private ResponseEntity responseEntity;

    @Autowired
    private DocumentService documentService;

    /**
     * Finds all calibration-tests form database
     *
     * @return a list of calibration-tests
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAllCalibrationTests() {
        try {
            CalibrationTestList list = testService.findAllCalibrationTests();
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (NotAvailableException exception) {
            throw new NotFoundException(exception);
        }
    }

    /**
     * Edit calibration-test in database
     *
     * @param testDTO           object with calibration-test data
     * @param calibrationTestId id parameter of calibration test
     * @return a response body with http status {@literal OK} if calibration-test
     * successfully edited or else http status {@literal CONFLICT}
     */
    @RequestMapping(value = "edit/{calibrationTestId}", method = RequestMethod.POST)
    public ResponseEntity editCalibrationTest(@PathVariable Long calibrationTestId, @RequestBody CalibrationTestDTO testDTO) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            testService.editTest(calibrationTestId, testDTO.getName(), testDTO.getCapacity(),
                    testDTO.getSettingNumber(), testDTO.getLatitude(), testDTO.getLongitude(), testDTO.getConsumptionStatus(), testDTO.getTestResult());
        } catch (Exception e) {
            logger.error("GOT EXCEPTION " + e.getMessage());
            logger.error(e); // for prevent critical issue "Either log or rethrow this exception"
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(httpStatus);
    }

    /**
     * Deletes selected calibration-test by Id
     *
     * @param calibrationTestId id parameter of calibration test
     * @return a response body with http status {@literal OK} if calibration-test
     * successfully deleted
     */
    @RequestMapping(value = "delete/{calibrationTestId}", method = RequestMethod.POST)
    public void deleteCalibrationTest(@PathVariable Long calibrationTestId) {
        testService.deleteTest(calibrationTestId);
    }


    /**
     * Finds all calibration-tests data form database
     *
     * @return a list of calibration-tests data
     */
    @RequestMapping(value = "/{calibrationTestId}/testData", method = RequestMethod.GET)
    public ResponseEntity findAllCalibrationTestData(@PathVariable Long calibrationTestId) {
        try {
            CalibrationTestDataList list = testService.findAllTestDataAsociatedWithTest(calibrationTestId);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (NotAvailableException exception) {
            logger.error("Not found " + exception);
            throw new com.softserve.edu.exceptions.NotFoundException(exception);
        }
    }


    /**
     * get all counters types for test
     *
     * @param
     */
    @RequestMapping(value = "getCountersTypes", method = RequestMethod.GET)
    public List<CounterTypeDTO> getCountersTypes() {
        List listOfCounterType = null;
        try {
            listOfCounterType = CounterTypeDTOTransformer.toDtofromListLight(counterTypeRepository.findAll());
        } catch (Exception e) {
            logger.error("failed to get list of CounterTyp", e);
        }
        return listOfCounterType;
    }

    @RequestMapping(value = "getCountersTypes/{standardSize}/{deviceType}/{symbol}", method = RequestMethod.GET)
    public List<CounterTypeDTO> getCountersTypesByStandardSizeAndDeviceTypeAndSymbol(@PathVariable String standardSize, @PathVariable String deviceType, @PathVariable String symbol) {
        List listOfCounterType = null;
        try {
            listOfCounterType = CounterTypeDTOTransformer.toDtofromListLight(counterTypeRepository.findByStandardSizeAndDeviceTypeAndSymbol(standardSize, deviceType, symbol));
        } catch (Exception e) {
            logger.error("failed to get list of CounterTyp", e);
        }
        return listOfCounterType;
    }


    /**
     * get all calibration module for handmade protocol
     *
     * @return CalibrationModuleDTO
     */
    @RequestMapping(value = "getCalibrationModules", method = RequestMethod.GET)
    public List<CalibrationModuleDTO> getCalibrationModules() {
        List list = null;
        try {
            list = CalibrationModuleDTOTransformer.toDtofromList(calibrationModuleService.findAllActing());
        } catch (Exception e) {
            logger.error("failed to get list of calibrationModule" + e.getMessage());
            logger.error(e);
        }
        return list;
    }


    /**
     * @param calibrationTestManualDTO
     * @return httpStatus 200 OK if everything went well
     */
    @RequestMapping(value = "createTestManual", method = RequestMethod.POST)
    public ResponseEntity createTestManual(@RequestBody CalibrationTestManualDTO calibrationTestManualDTO) {
        ResponseEntity<String> responseEntity = new ResponseEntity(HttpStatus.OK);
        try {
            CalibrationTestManual calibrationTestManual = calibrationTestManualService.createNewTestManual(calibrationTestManualDTO.getPathToScanDoc(), calibrationTestManualDTO.getNumberOfTest(),
                    calibrationTestManualDTO.getModuleId(), calibrationTestManualDTO.getDateOfTest());
            for (CalibrationTestDataManualDTO calibrationTDMDTO : calibrationTestManualDTO.getListOfCalibrationTestDataManual()) {
                calibrationTestDataManualService.createNewTestDataManual(calibrationTDMDTO.getStatusTestFirst()
                        , calibrationTDMDTO.getStatusTestSecond(), calibrationTDMDTO.getStatusTestThird()
                        , calibrationTDMDTO.getStatusCommon(), calibrationTDMDTO.getCounterId()
                        , calibrationTestManual, calibrationTDMDTO.getVerificationId());
            }
        } catch (Exception e) {
            logger.error(e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }


    /**
     * get protocol manual
     *
     * @param verificationId
     * @return test manual
     */
    @RequestMapping(value = "getTestManual/{verificationId}", method = RequestMethod.GET)
    public ResponseEntity<CalibrationTestDataManualDTO> getTestManual(@PathVariable String verificationId) {
        ResponseEntity<CalibrationTestDataManualDTO> responseEntity;
        try {
            CalibrationTestDataManual cTestDataManual = calibrationTestDataManualService.findByVerificationId(verificationId);
            CalibrationTestManual cTestManual = cTestDataManual.getCalibrationTestManual();
            CalibrationTestDataManualDTO cTestDataManualDTO = new CalibrationTestDataManualDTO(
                    cTestDataManual.getStatusTestFirst().toString()
                    , cTestDataManual.getStatusTestSecond().toString()
                    , cTestDataManual.getStatusTestThird().toString()
                    , cTestDataManual.getStatusCommon().toString(), new CalibrationTestManualDTO(
                    cTestManual.getCalibrationModule().getSerialNumber()
                    , cTestManual.getNumberOfTest()
                    , cTestManual.getDateTest()
                    , cTestManual.getGenerateNumberTest(), cTestManual.getPathToScan(), cTestManual.getId()));
            responseEntity = new ResponseEntity(cTestDataManualDTO, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("failed to get manual protocol" + e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * Edit calibration test manual
     *
     * @param cTestManualDTO object with calibration-test-manual
     * @param verificationId id parameter of verification
     * @return httpStatus 200 OK if everything went well
     */
    @RequestMapping(value = "editTestManual/{verificationId}/{verificationEdit}", method = RequestMethod.POST)
    public ResponseEntity editTestManual(@PathVariable String verificationId, @PathVariable boolean verificationEdit, @RequestBody CalibrationTestManualDTO cTestManualDTO) {
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        try {
            CalibrationTestDataManual cTestDataManual = calibrationTestDataManualService.findByVerificationId(verificationId);
            CalibrationTestManual cTestManual = cTestDataManual.getCalibrationTestManual();
            calibrationTestManualService.editTestManual(cTestManualDTO.getPathToScanDoc(), cTestManualDTO.getDateOfTest(), cTestManualDTO.getNumberOfTest()
                    , cTestManualDTO.getModuleId(), cTestManual);
            CalibrationTestDataManualDTO cTestDataManualDTO = cTestManualDTO.getListOfCalibrationTestDataManual().get(0);
            calibrationTestDataManualService.editTestDataManual(cTestDataManualDTO.getStatusTestFirst()
                    , cTestDataManualDTO.getStatusTestSecond(), cTestDataManualDTO.getStatusTestThird()
                    , cTestDataManualDTO.getStatusCommon(), cTestDataManual, verificationId, verificationEdit);
        } catch (Exception e) {
            logger.error("failed to edit calibration test manual" + e.getMessage());
            logger.error(e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * delete test manual and all test data manual of protocol
     *
     * @param verificationId successfully deleted
     */
    @RequestMapping(value = "deleteTestManual/{verificationId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteTestManual(@PathVariable String verificationId) {
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        try {
            calibrationTestManualService.deleteTestManual(verificationId);
        } catch (Exception e) {
            logger.error("can't delete test manual" + e.getMessage());
            logger.error(e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }


    /**
     * uploads a scanDoc from chosen directory
     *
     * @param file chosen file object
     * @return httpStatus 200 OK if everything went well
     */
    @RequestMapping(value = "uploadScanDoc/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> uploadScanDoc(@RequestBody MultipartFile file, @PathVariable Long id) {
        ResponseEntity<String> responseEntity;
        try {
            String originalFileName = file.getOriginalFilename();
            String fileType = originalFileName.substring(originalFileName.lastIndexOf('.'));
            if (Pattern.compile(contentDocExtPattern, Pattern.CASE_INSENSITIVE).matcher(fileType).matches()) {
                String uriOfscanDoc = calibrationTestManualService.uploadScanDoc(file.getInputStream(), originalFileName, id);
                responseEntity = new ResponseEntity(uriOfscanDoc, HttpStatus.OK);
            } else {
                logger.error("failed to uploadScanDoc");
                responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("failed to uploadScanDoc " + e.getMessage());
            logger.error(e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }


    /**
     * get  scanDoc
     *
     * @param pathToScanDoc to file
     * @return httpStatus 200 OK if everything went well
     */
    @RequestMapping(value = "getScanDoc/{pathToScanDoc}", produces = "application/pdf", method = RequestMethod.GET)
    public void getScanDoc(@PathVariable String pathToScanDoc, HttpServletResponse response) {
        byte[] doc = null;
        OutputStream out = null;
        try {
            doc = calibrationTestManualService.getScanDoc(pathToScanDoc);
            response.setContentType("application/pdf");
            response.setContentLength(doc.length);
            out = response.getOutputStream();
            out.write(doc);
        } catch (IOException e) {
            logger.error("failed to get pdf blob" + e.getMessage());
            logger.error(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("can't close outputStream" + e.getMessage());
                    logger.error(e);
                }
            }
        }
    }


    /**
     * delete a scanDoc
     *
     * @param pathToScanDoc to file
     * @return httpStatus 200 OK if everything went well
     */
    @RequestMapping(value = "deleteScanDoc/{pathToScanDoc}/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteScanDoc(@PathVariable String pathToScanDoc, @PathVariable Long id) {
        ResponseEntity<String> responseEntity = new ResponseEntity(HttpStatus.OK);
        try {
            calibrationTestManualService.deleteScanDoc(pathToScanDoc, id);
        } catch (Exception e) {
            logger.error("Failed to delete ScanDoc " + e.getMessage());
            logger.error(e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }


    /**
     * get protocol
     *
     * @param verificationId id parameter of verification
     * @return protocol
     */
    @RequestMapping(value = "getTest/{verificationId}", method = RequestMethod.GET)
    public ResponseEntity getTestProtocol(@PathVariable String verificationId) {
        ResponseEntity<String> responseEntity;
        try {
            CalibrationTest calibrationTest = testService.findByVerificationId(verificationId);
            Verification verification = verificationService.findById(verificationId);
            responseEntity = new ResponseEntity((new CalibrationTestFileDataDTO(calibrationTest, testService, verification)), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("failed to get protocol " + e.getMessage());
            logger.error(e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * update test
     *
     * @param verificationId id parameter of verification
     * @return httpStatus 200 OK if everything went well
     */
    @RequestMapping(value = "editTest/{verificationId}", method = RequestMethod.POST)
    public ResponseEntity editTestProtocol(@RequestBody CalibrationTestFileDataDTO cTestFileDataDTO, @PathVariable String verificationId) {
        ResponseEntity<String> responseEntity = new ResponseEntity(HttpStatus.OK);
        try {
            CalibrationTest calibTest = testService.findByVerificationId(verificationId);
            Counter counter = verificationService.findById(verificationId).getCounter();
            counter.setNumberCounter(cTestFileDataDTO.getCounterNumber());
            counter.setReleaseYear(Integer.valueOf(cTestFileDataDTO.getCounterProductionYear()).toString());
            counter.setCounterType(counterTypeService.findById(cTestFileDataDTO.getCounterTypeId()));
            counterRepository.save(counter);
            UnsuitabilityReason unsuitabilityReason = null;
            if(cTestFileDataDTO.getReasonUnsuitabilityId() != null) {
              unsuitabilityReason   = unsuitabilityReasonRepository.findOne(cTestFileDataDTO.getReasonUnsuitabilityId());
            }
            calibTest.setUnsuitabilityReason(unsuitabilityReason);
            calibTest.setRotateIndex(cTestFileDataDTO.getRotateIndex());
            calibTest.setTestResult(cTestFileDataDTO.getTestResult());
            calibTest.setCapacity(cTestFileDataDTO.getAccumulatedVolume());
            Set<CalibrationTestData> setOfTestDate = testService.getLatestTests(calibTest.getCalibrationTestDataList());
            List<CalibrationTestData> listOfTestDate = new ArrayList<>(setOfTestDate);
            CalibrationTestData calibTestData;
            for (int x = 0; x < listOfTestDate.size(); x++) {
                calibTestData = listOfTestDate.get(x);
                CalibrationTestDataDTO calibrationTestDataDTO = cTestFileDataDTO.getListTestData().get(x);
                calibTestData.setInitialValue(calibrationTestDataDTO.getInitialValue());
                calibTestData.setEndValue(calibrationTestDataDTO.getEndValue());
                calibTestData.setCalculationError(calibrationTestDataDTO.getCalculationError());
                calibTestData.setTestResult(calibrationTestDataDTO.getTestResult());
                calibTestData.setVolumeInDevice(calibrationTestDataDTO.getVolumeInDevice());
                testDataRepository.save(calibTestData);
            }
            testRepository.save(calibTest);
            testService.updateTest(verificationId, cTestFileDataDTO.getStatus());
        } catch (Exception e) {
            logger.error(e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    @RequestMapping(value = "signTest/{verificationId}", method = RequestMethod.GET)
    public void signTestProtocol(@PathVariable String verificationId, HttpServletResponse httpResponse) {
        try {
            Verification verification = verificationService.findById(verificationId);
            CalibrationTest calibrationTest = testService.findByVerificationId(verificationId);

            Integer maxOfYearIntroduction = counterTypeRepository.findMaximumYearIntroduction(
                    getStandardSize(verification), getSymbol(verification), getManufacturer(verification),
                    getDeviceType(verification), getYearIntroduction(verification));

            Integer calibrationInterval = counterTypeRepository.findCalibrationInterval(
                    getStandardSize(verification), getSymbol(verification), getManufacturer(verification),
                    getDeviceType(verification), getYearIntroduction(verification), maxOfYearIntroduction);

            Calendar validityOfCertificate = Calendar.getInstance();
            validityOfCertificate.setTime(new Date());
            validityOfCertificate.add(Calendar.YEAR, calibrationInterval);

            verification.setExpirationDate(validityOfCertificate.getTime());
            verification.setSignProtocolDate(new Date());
            calibrationTest.setCalibrationInterval(calibrationInterval);
            DocumentType documentType = verification.getStatus() == Status.TEST_OK ? DocumentType.VERIFICATION_CERTIFICATE : DocumentType.UNFITNESS_CERTIFICATE;
            FileObject file = documentService.buildFile(documentType, verification, calibrationTest, FileFormat.DOCX);

            httpResponse.setContentType("application/pdf");
            httpResponse.setHeader("Content-Disposition", "attachment; " +
                    "filename=\"" + file.getName().getBaseName() + ".pdf"  + "\"");

            ServletOutputStream outputStream = httpResponse.getOutputStream();
            byte[] documentByteArray = new byte[(int)file.getContent().getSize()];
            file.getContent().getInputStream().read(documentByteArray);

            outputStream.write(documentByteArray);
            outputStream.close();
            testRepository.save(calibrationTest);
            verificationService.saveVerification(verification);
        } catch (Exception e) {
            logger.error("Cannot sing protocol", e);
        }

    }

    @RequestMapping(value = "signEDSTest/{verificationId}", method = RequestMethod.POST)
    public ResponseEntity signEDSTestProtocol(@RequestParam(value="file") MultipartFile file, @PathVariable String verificationId){
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        try {
            CalibrationTest calibrationTest = testService.findByVerificationId(verificationId);
            calibrationTest.setSignedDocument(file.getBytes());
            calibrationTest.setSigned(true);
            testRepository.save(calibrationTest);
        }catch (Exception e) {
            logger.error("Cannot sing protocol", e);
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
    private String getStandardSize(Verification verification) {
        return verification.getCounter().getCounterType().getStandardSize();
    }

    private String getSymbol(Verification verification) {
        return verification.getCounter().getCounterType().getSymbol();
    }

    private String getManufacturer(Verification verification) {
        return verification.getCounter().getCounterType().getManufacturer();
    }

    private String getDeviceType(Verification verification) {
        return verification.getCounter().getCounterType().getDevice().getDeviceType().name();
    }

    private Integer getYearIntroduction(Verification verification) {
        return Integer.parseInt(verification.getCounter().getReleaseYear());
    }

}

