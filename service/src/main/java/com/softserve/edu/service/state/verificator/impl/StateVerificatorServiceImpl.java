package com.softserve.edu.service.state.verificator.impl;


import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.repository.OrganizationRepository;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.provider.buildGraphic.GraphicBuilder;
import com.softserve.edu.service.provider.buildGraphic.GraphicBuilderMainPanel;
import com.softserve.edu.service.provider.buildGraphic.MonthOfYear;
import com.softserve.edu.service.provider.buildGraphic.ProviderEmployeeGraphic;
import com.softserve.edu.service.state.verificator.StateVerificatorService;
import com.softserve.edu.service.utils.EmployeeDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class StateVerificatorServiceImpl implements StateVerificatorService {

    @Autowired
    private OrganizationRepository stateVerificatorRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    Logger logger = Logger.getLogger(StateVerificatorServiceImpl.class);

    @Override
    @Transactional
    public void saveStateVerificator(Organization stateVerificator) {
        stateVerificatorRepository.save(stateVerificator);
    }

    @Override
    @Transactional(readOnly = true)
    public Organization findById(Long id) {
        return stateVerificatorRepository.findOne(id);
    }


    /**
     * return all Employees from Verificators(User) organization
     * @param userRoles
     * @param employee
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllVerificatorEmployee(List<String> userRoles, User employee) {

        List<EmployeeDTO> verificatorEmployeeList = new ArrayList<>();
        if (userRoles.contains(UserRole.STATE_VERIFICATOR_ADMIN.name())) {
            List<User> allAvailableUsersList = userRepository
                    .findAllAvailableUsersByRoleAndOrganizationId(UserRole.STATE_VERIFICATOR_EMPLOYEE,
                            employee.getOrganization().getId())
                    .stream()
                    .collect(Collectors.toList());

            verificatorEmployeeList = EmployeeDTO.giveListOfEmployeeDTOs(allAvailableUsersList);
        } else {
            EmployeeDTO userPage = new EmployeeDTO(employee.getUsername(), employee.getFirstName(),
                    employee.getLastName(), employee.getMiddleName(), userRoles.get(0));
            verificatorEmployeeList.add(userPage);
        }
        return verificatorEmployeeList;
    }

    /**
     * assigning employee on the verification
     * and than this employee will work with this verification
     * @param verificationId
     * @param verificatorEmployee
     */
    @Override
    @Transactional
    public boolean assignVerificatorEmployee(String verificationId, User verificatorEmployee) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification.getStateVerificatorEmployee() == null) {
            verification.setStateVerificatorEmployee(verificatorEmployee);
            verification.setReadStatus(Verification.ReadStatus.READ);
            verificationRepository.save(verification);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public void unassignVerificatorEmployee(String verificationId) {
        Verification verification = verificationRepository.findOne(verificationId);
        verification.setStateVerificatorEmployee(null);
        verificationRepository.save(verification);
    }

    @Override
    @Transactional
    public List<ProviderEmployeeGraphic> buidGraphicMainPanel(Date from, Date to, Long idOrganization) {
        Organization organization = organizationRepository.findOne(idOrganization);
        List<Verification> verifications = verificationRepository.
                findByStateVerificatorAndSentToVerificatorDateBetween
                        (organization, from, to);
        List<ProviderEmployeeGraphic> graficData = null;
        try {
            List<MonthOfYear> monthList = GraphicBuilder.listOfMonths(from, to);
            graficData = GraphicBuilderMainPanel.builderData(verifications, monthList, organization);

        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return graficData;
    }

    @Override
    public Date convertToDate(String date) throws IllegalArgumentException {
        Date result = null;
        if (StringUtils.isNotBlank(date)) {
            try {
                result = DATE_FORMAT.parse(date);
            } catch (ParseException e) {
                logger.error(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("input date is not correct");
        }
        return result;
    }
}
