package com.softserve.edu.controller.provider;

import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.dto.calibrator.NotStandardVerificationDTO;
import com.softserve.edu.dto.provider.VerificationProviderEmployeeDTO;
import com.softserve.edu.dto.provider.VerificationStatusUpdateDTO;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.calibrator.CalibratorService;
import com.softserve.edu.service.provider.ProviderEmployeeService;
import com.softserve.edu.service.provider.ProviderService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.verification.VerificationProviderEmployeeService;
import com.softserve.edu.service.verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/provider/not-standard-verifications/")
public class NotStandardVerificationProviderController {

    @Autowired
    VerificationService verificationService;

    @Autowired
    CalibratorService calibratorService;

    @Autowired
    OrganizationService organizationService;
    @Autowired
    ProviderService providerService;
    @Autowired
    ProviderEmployeeService providerEmployeeService;
    @Autowired
    VerificationProviderEmployeeService verificationProviderEmployeeService;

    @RequestMapping(value = "{pageNumber}/{itemsPerPage}", method = RequestMethod.GET)
    public PageDTO<NotStandardVerificationDTO> getPageOfVerificationsCreatedByCalibrator(
            @PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {

        Organization providerOrganisation = providerEmployeeService.oneProviderEmployee(
                employeeUser.getUsername()).getOrganization();
        Status status = Status.SENT_TO_PROVIDER;
        List<Verification> verifications = verificationService.findPageOfVerificationsByProviderIdAndStatus(
                providerOrganisation, pageNumber, itemsPerPage, status);

        Long count = verificationService.countByProviderAndStatus(providerOrganisation, status);
        List<NotStandardVerificationDTO> content = toDTOFromList(verifications);
        return new PageDTO<>(count, content);
    }

    @RequestMapping(value = "assign/providerEmployee", method = RequestMethod.PUT)
    public void assignProviderEmployee(@RequestBody VerificationProviderEmployeeDTO verificationProviderEmployeeDTO) {

        String userNameProvider = verificationProviderEmployeeDTO.getEmployeeProvider().getUsername();
        String idVerification = verificationProviderEmployeeDTO.getIdVerification();
        User employeeProvider = verificationProviderEmployeeService.oneProviderEmployee(userNameProvider);
        verificationProviderEmployeeService.assignProviderEmployeeForNotStandardVerification(idVerification,
                employeeProvider);
    }

    @RequestMapping(value = "assign/providerEmployee/{verificationId}", method = RequestMethod.PUT)
    public void assignCalibratorEmployee(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails userDetails,
                                         @PathVariable String verificationId) {
        User user = verificationProviderEmployeeService.oneProviderEmployee(userDetails.getUsername());
        if(user != null && user.getUserRoles().contains(UserRole.PROVIDER_EMPLOYEE)) {
            verificationProviderEmployeeService.assignProviderEmployeeForNotStandardVerification(verificationId, user);
        }
    }

    @RequestMapping(value = "new/reject", method = RequestMethod.PUT)
    public void rejectVerification(@RequestBody VerificationStatusUpdateDTO verificationReadStatusUpdateDTO) {

        String verificationId = verificationReadStatusUpdateDTO.getVerificationId();
        verificationService.returnVerificationToCalibratorFromProvider(verificationId,
                verificationReadStatusUpdateDTO.getMessage());
    }

    /**
     * Find count of new not standard verifications that have Read Status "UNREAD"
     *
     * @return Long count
     */
    @RequestMapping(value = "new/count", method = RequestMethod.GET)
    public Long getCountOfNewVerificationsByProviderId(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        return verificationService.findCountOfNotStandardNewVerificationsByProviderId(user.getOrganizationId());

    }


    private List<NotStandardVerificationDTO> toDTOFromList(List<Verification> verifications) {

        List<NotStandardVerificationDTO> resultList = new ArrayList<>();

        for (Verification verification : verifications) {
            resultList.add(new NotStandardVerificationDTO(
                    verification.getId(),
                    verification.getInitialDate(),
                    verification.getClientData().getClientAddress(),
                    verification.getClientData().getFirstName(),
                    verification.getClientData().getLastName(),
                    verification.getClientData().getMiddleName()));
        }
        return resultList;
    }
}

