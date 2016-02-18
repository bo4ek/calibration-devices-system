package com.softserve.edu.service.calibrator;

import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Veronika 11.11.2015
 */
public interface CalibratorDigitalProtocolsService {


    List<Verification> findPageOfVerificationsByCalibratorIdAndStatus(
            User calibratorEmployee, int pageNumber, int itemsPerPage, Status status);

    ListToPageTransformer<Verification> findPageOfVerificationsByCalibratorIdAndStatus(Long verificatorId, int pageNumber, int itemsPerPage, String dateToSearch, String idToSearch, String status, String nameProvider, String nameCalibrator,
    String numberOfCounter, String numberOfProtocol,
    String sentToVerificatorDate, String serialNumber,
    String sortCriteria, String sortOrder, User verificatorEmployee);

    Long countByCalibratorEmployeeUsernameAndStatus (User calibratorEmployee, Status status);
}
