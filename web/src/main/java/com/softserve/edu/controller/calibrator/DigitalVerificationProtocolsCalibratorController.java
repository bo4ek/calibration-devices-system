package com.softserve.edu.controller.calibrator;

import com.softserve.edu.controller.calibrator.util.ProtocolDTOTransformer;
import com.softserve.edu.dto.NewVerificationsFilterSearch;
import com.softserve.edu.dto.VerificationUpdateDTO;
import com.softserve.edu.dto.admin.OrganizationDTO;
import com.softserve.edu.dto.calibrator.ProtocolDTO;
import com.softserve.edu.entity.enumeration.organization.OrganizationType;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.calibrator.CalibratorDigitalProtocolsService;
import com.softserve.edu.service.calibrator.CalibratorEmployeeService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.service.state.verificator.StateVerificatorService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.utils.ListToPageTransformer;
import com.softserve.edu.service.verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Veronika 5.11.2015
 */
@RestController
@RequestMapping(value = "/calibrator/protocols/", produces = "application/json")
public class DigitalVerificationProtocolsCalibratorController {

    @Autowired
    private CalibratorDigitalProtocolsService protocolsService;

    @Autowired
    CalibratorEmployeeService calibratorEmployeeService;

    @Autowired
    VerificationService verificationService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    StateVerificatorService stateVerificatorService;

    @Autowired
    CalibrationTestService calibrationTestService;

    /**
     * Change status for verification when it is sent to verificator
     *
     * @param verificationUpdateDTO
     */
    @RequestMapping(value = "send", method = RequestMethod.PUT)
    public void updateVerification(@RequestBody VerificationUpdateDTO verificationUpdateDTO) {
        for (String verificationId : verificationUpdateDTO.getIdsOfVerifications()) {
            Long idVerificator = verificationUpdateDTO.getOrganizationId();
            Organization verificator = stateVerificatorService.findById(idVerificator);
            verificationService.sendVerificationTo(verificationId, verificator, Status.SENT_TO_VERIFICATOR);
        }
    }

    /**
     * This method calls service whiche returns the list of verifications. The controller transform them with the help of
     * ProtocolDTOTransformer to list of protocolsDTO. It's done to sent to the client only the necessary data.
     *
     * @param pageNumber
     * @param itemsPerPage
     * @param employeeUser
     * @return list of ProtocolDTO - data for table with protocols
     */
    @RequestMapping(value = "{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.GET)
    public PageDTO<ProtocolDTO> getPageOfAllSentVerificationsByStateCalibratorIdAndSearch(@PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage, @PathVariable String sortCriteria, @PathVariable String sortOrder,
                                                                                          NewVerificationsFilterSearch searchData, @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {

        User calibratorEmployee = calibratorEmployeeService.oneCalibratorEmployee(employeeUser.getUsername());
        Set<UserRole> userRoles = calibratorEmployee.getUserRoles();

        ListToPageTransformer<Verification> queryResult = protocolsService.findPageOfVerificationsByCalibratorIdAndStatus(
                employeeUser.getOrganizationId(), pageNumber, itemsPerPage,
                searchData.getDate(),
                searchData.getEndDate(),
                searchData.getId(),
                searchData.getStatus(),
                searchData.getNameProvider(),
                searchData.getNameCalibrator(),
                searchData.getNumberOfCounter(),
                searchData.getNumberOfProtocol(),
                searchData.getSerialNumber(),
                sortCriteria,
                sortOrder,
                calibratorEmployee);

        List<ProtocolDTO> content = ProtocolDTOTransformer.toDTOFromList(queryResult.getContent(), userRoles);
        return new PageDTO<>(queryResult.getTotalItems(), content);

    }

    /**
     * Get verificators that has agreement with this calibrator
     *
     * @param user
     * @return Set of organizations with all available data
     */
    @RequestMapping(value = "verificators", method = RequestMethod.GET)
    public Set<OrganizationDTO> getVerificators(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        Organization userOrganization = organizationService.getOrganizationById(user.getOrganizationId());
        return organizationService.findByIdAndTypeAndActiveAgreementDeviceType(user.getOrganizationId(),
                OrganizationType.STATE_VERIFICATOR, userOrganization.getDeviceTypes().iterator().next()).stream()
                .map(organization -> new OrganizationDTO(organization.getId(), organization.getName()))
                .collect(Collectors.toSet());
    }

    @RequestMapping(value = "earliestDate/creatingProtocol", method = RequestMethod.GET)
    public String getEarliestDateOfDigitalVerificationProtocolsByCalibrator(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        if (user != null) {
            Organization organization = organizationService.getOrganizationById(user.getOrganizationId());
            Date earliestDate = verificationService.getEarliestDateOfDigitalVerificationProtocolsByCalibrator(organization);
            if (earliestDate != null) {
                return earliestDate.toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
