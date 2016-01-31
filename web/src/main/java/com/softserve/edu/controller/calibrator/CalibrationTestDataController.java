package com.softserve.edu.controller.calibrator;

import com.softserve.edu.service.calibrator.data.test.CalibrationTestDataService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.softserve.edu.dto.CalibrationTestDataDTO;
import com.softserve.edu.entity.verification.calibration.CalibrationTestData;

@Controller
@RequestMapping("/calibrator/calibrationTestData/")
public class CalibrationTestDataController {

    @Autowired
    private CalibrationTestDataService service;

    private final Logger logger = Logger.getLogger(CalibrationTestDataController.class);

    @RequestMapping(value = "{testDataId}", method = RequestMethod.GET)
    public ResponseEntity getTestData(@PathVariable Long testDataId) {
        CalibrationTestData foundTestData = service.findTestData(testDataId);
        if (foundTestData != null) {
            return new ResponseEntity<>(foundTestData, HttpStatus.OK);
        } else {
            logger.error("Not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "edit/{testDataId}", method = RequestMethod.POST)
    public ResponseEntity editTestData(@PathVariable Long testDataId, @RequestBody CalibrationTestDataDTO testDataDTO) {
    	HttpStatus httpStatus = HttpStatus.OK;
    	try {
    		CalibrationTestData updatedTestData = service.editTestData(testDataId, testDataDTO.saveTestData());
		} catch (Exception e) {
			logger.error("GOT EXCEPTION " + e.getMessage());
            logger.error(e); // for prevent critical issue "Either log or rethrow this exception"
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
