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

    Long countByCalibratorEmployee_usernameAndStatus (User calibratorEmployee, Status status);

    List<String> findNumbersOfProtocolsFromBbi(List<Verification> verifications);
}
