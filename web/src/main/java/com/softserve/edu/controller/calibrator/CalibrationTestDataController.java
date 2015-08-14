package com.softserve.edu.controller.calibrator;

import com.softserve.edu.entity.CalibrationTest;
import com.softserve.edu.service.CalibrationTestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.softserve.edu.dto.CalibrationTestDataDTO;
import com.softserve.edu.entity.CalibrationTestData;
import com.softserve.edu.service.CalibrationTestDataService;


@Controller
@RequestMapping("/calibrator/calibrationTestData/")
public class CalibrationTestDataController {

    @Autowired
    private CalibrationTestDataService service;

    @Autowired
    private CalibrationTestService calibrationTestService;
    
    private final Logger logger = Logger.getLogger(CalibrationTestDataController.class);

    @RequestMapping(value = "{testDataId}", method = RequestMethod.GET)
    public ResponseEntity getTestData(@PathVariable Long testDataId) {
        CalibrationTestData foundtestData = service.findTestData(testDataId);
        if (foundtestData != null) {
            return new ResponseEntity<>(foundtestData, HttpStatus.OK);
        } else {
            logger.error("Not found");
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Saves calibration-test in database
     *
     * @param testDTO object with calibration-test data
     * @param verificationId String of verification ID for saving calibration-test
     * @return a response body with http status {@literal CREATED} if everything
     *         calibration-test successfully created or else http
     *         status {@literal CONFLICT}
     */
    @RequestMapping(value = "addTestData/{testId}", method = RequestMethod.POST)
    public ResponseEntity createCalibrationTestData(@RequestBody CalibrationTestDataDTO testDataDTO, @PathVariable Long testId) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        try {
            CalibrationTest foundTest = calibrationTestService.findTestById(testId);
            CalibrationTestData calibrationTestData = new CalibrationTestData(testDataDTO.getGivenConsumption(), testDataDTO.getAcceptableError(),
                    testDataDTO.getVolumeOfStandart(), testDataDTO.getInitialValue(), testDataDTO.getEndValue(), testDataDTO.getVolumeInDevice(),
                    testDataDTO.getActualConsumption(), testDataDTO.getConsumptionStatus(), testDataDTO.getCalculationError(), testDataDTO.getTestResult(), foundTest);
            calibrationTestService.createTestData(testId, calibrationTestData);
        } catch (Exception e) {
            logger.error("GOT EXCEPTION " + e.getMessage());
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(httpStatus);
    }



    @RequestMapping(value = "edit/{testDataId}", method = RequestMethod.POST)
    public ResponseEntity editTestData(@PathVariable Long testDataId, @RequestBody CalibrationTestDataDTO testDataDTO) {
    	HttpStatus httpStatus = HttpStatus.OK;
    	try {
    		CalibrationTestData updatedTestData = service.editTestData(testDataId, testDataDTO.saveTestData());
		} catch (Exception e) {
			logger.error("GOT EXCEPTION " + e.getMessage());
			httpStatus = HttpStatus.CONFLICT;
		}
    	return new ResponseEntity<>(httpStatus);
    }
    
    @RequestMapping(value = "delete/{testDataId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteTestData(@PathVariable Long testDataId) {
        CalibrationTestData testData = service.deleteTestData(testDataId);
        return new ResponseEntity<>(testData, HttpStatus.OK);
    }


}