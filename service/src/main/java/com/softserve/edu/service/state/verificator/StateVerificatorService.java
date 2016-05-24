package com.softserve.edu.service.state.verificator;

import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.provider.buildGraphic.ProviderEmployeeGraphic;
import com.softserve.edu.service.utils.EmployeeDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface StateVerificatorService {

    void saveStateVerificator(Organization stateVerificator);

    Organization findById(Long id);

    List<EmployeeDTO> getAllVerificatorEmployee(List<String> role, User employee);

    boolean assignVerificatorEmployee(String idVerification, User employeeCalibrator);

    @Transactional
    void unassignVerificatorEmployee(String verificationId);

    List<ProviderEmployeeGraphic> buidGraphicMainPanel(Date from, Date to, Long idOrganization);

    Date convertToDate(String date);
}