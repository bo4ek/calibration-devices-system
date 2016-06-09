package com.softserve.edu.service.calibrator;

import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import com.softserve.edu.service.utils.EmployeeDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public interface CalibratorService {

    Organization findById(Long id);

    CounterType findOneBySymbolAndStandardSizeAndDeviceId(String symbol, String standardSize, Long deviceId);

    void uploadBbi(InputStream fileStream, String verificationId,
                   String originalFileFullName) throws IOException;

    void uploadBbi(InputStream fileStream, Verification verification,
                   String originalFileFullName) throws IOException;

    String findBbiFileByOrganizationId(String id);

    User oneCalibratorEmployee(String username);

    List<EmployeeDTO> getAllCalibratorEmployee(List<String> role, User employee);

    void assignCalibratorEmployee(String verificationId, User calibratorEmployee);

    void removeCalibratorEmployee(String verificationId, User calibratorEmployee);

    boolean isAdmin(User user);

    boolean checkIfAdditionalInfoExists(String verificationId);

    Set<String> getTypesById(Long id);

    int getNumOfVerifications(Long taskID);

    int getNumOfCompletedVerifications(CalibrationTask task);

}
