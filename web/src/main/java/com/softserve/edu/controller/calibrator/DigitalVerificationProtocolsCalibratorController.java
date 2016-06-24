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
import com.softserve.edu.entity.verification.BbiProtocol;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.calibrator.BbiFileService;
import com.softserve.edu.service.calibrator.CalibratorDigitalProtocolsService;
import com.softserve.edu.service.calibrator.CalibratorEmployeeService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.service.calibrator.impl.BbiFileServiceImpl;
import com.softserve.edu.service.state.verificator.StateVerificatorService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.user.UserService;
import com.softserve.edu.service.utils.ListToPageTransformer;
import com.softserve.edu.service.verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    BbiFileService bbiFileService;

    @Autowired
    UserService userService;

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
                searchData.getModuleNumber(),
                sortCriteria,
                sortOrder,
                calibratorEmployee);

        List<ProtocolDTO> content = ProtocolDTOTransformer.toDTOFromList(queryResult.getContent(), userRoles);
        return new PageDTO<>(queryResult.getTotalItems(), content);

    }

    @RequestMapping(value = "rejected/{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.GET)
    public PageDTO<ProtocolDTO> getPageOfAllRejectedVerificationsByStateCalibratorIdAndSearch(@PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage, @PathVariable String sortCriteria, @PathVariable String sortOrder,
                                                                                              NewVerificationsFilterSearch searchData, @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {

        searchData.setStatus(Status.PROTOCOL_REJECTED.toString());
        return getPageOfAllSentVerificationsByStateCalibratorIdAndSearch(pageNumber, itemsPerPage, sortCriteria,
                sortOrder, searchData, employeeUser);

    }

    @RequestMapping(value = "cancel-protocol/{verificationId}", method = RequestMethod.GET)
    public ResponseEntity cancelVerificationProtocol(@PathVariable String verificationId, @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails userDetails) {
        Verification verification = verificationService.findById(verificationId);
        User user = userService.findOne(userDetails.getUsername());
        if (verification != null && ((user.getUserRoles().contains(UserRole.CALIBRATOR_EMPLOYEE) && verification.getCalibratorEmployee().getUsername().equals(user.getUsername()))
                || (user.getUserRoles().contains(UserRole.CALIBRATOR_ADMIN) && verification.getCalibrator().getId().equals(user.getOrganization().getId())))) {
            setFieldForCanceling(verification);
            List<BbiProtocol> protocolList = bbiFileService.findBbiByVerification(verification);
            for (BbiProtocol bbiProtocol : protocolList) {
                bbiFileService.deleteBbi(bbiProtocol);
            }
            verificationService.saveVerification(verification);
            calibrationTestService.deleteTest(calibrationTestService.findByVerificationId(verification.getId()).getId());
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
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

    @RequestMapping(value = "earliestDate/rejectingProtocol", method = RequestMethod.GET)
    public String getEarliestDateOfRejectingProtocolsByCalibrator(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        if (user != null) {
            Organization organization = organizationService.getOrganizationById(user.getOrganizationId());
            Date earliestDate = verificationService.getEarliestDateOfRejectingVerificationProtocolsByCalibrator(organization);
            if (earliestDate != null) {
                return earliestDate.toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void setFieldForCanceling(Verification verification) {
        verification.setStatus(Status.IN_PROGRESS);
        verification.setTaskStatus(Status.PLANNING_TASK);
        verification.setCalibrationTests(null);
        verification.setVerificationDate(null);
        verification.setVerificationTime(null);
        verification.setSignProtocolDate(null);
        verification.setSigned(false);
        verification.setSignedDocument(null);
        verification.setCalibrationModule(null);
        verification.setProviderFromBBI(null);
        verification.setStateVerificator(null);
        verification.setStateVerificatorEmployee(null);
        verification.setExpirationDate(null);
        verification.setTask(null);
        verification.setRejectedMessage(null);
    }
}
