package com.softserve.edu.controller.calibrator;

import com.softserve.edu.dto.provider.VerificationProviderEmployeeDTO;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.provider.buildGraphic.ProviderEmployeeGraphic;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.admin.UsersService;
import com.softserve.edu.service.calibrator.CalibratorEmployeeService;
import com.softserve.edu.service.calibrator.CalibratorService;
import com.softserve.edu.service.utils.EmployeeDTO;
import com.softserve.edu.service.verification.VerificationService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "calibrator/admin/users/")
public class CalibratorEmployeeController {

    Logger logger = Logger.getLogger(CalibratorEmployeeController.class);

    @Autowired
    private UsersService usersService;

    @Autowired
    private OrganizationService organizationsService;

    @Autowired
    private CalibratorEmployeeService calibratorEmployeeService;

    @Autowired
    private CalibratorService calibratorService;

    @Autowired
    private VerificationService verificationService;


    /**
     * Check whereas {@code username} is available,
     * i.e. it is possible to create new user with this {@code username}
     *
     * @param username username
     * @return {@literal true} if {@code username} available or else {@literal false}
     */
    @RequestMapping(value = "available/{username}", method = RequestMethod.GET)
    public Boolean isValidUsername(@PathVariable String username) {
        boolean isAvailable = false;
        if (username != null) {
            isAvailable = usersService.isExistsWithUsername(username);
        }
        return isAvailable;
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity<HttpStatus> addEmployee(
            @RequestBody User calibratorEmployee,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        Organization employeeOrganization = organizationsService.getOrganizationById(user.getOrganizationId());
        calibratorEmployee.setOrganization(employeeOrganization);
        calibratorEmployeeService.addEmployee(calibratorEmployee);
        return new ResponseEntity<HttpStatus>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "new/calibratorEmployees", method = RequestMethod.GET)
    public List<EmployeeDTO> employeeCalibratorVerification(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        User employee = calibratorService.oneCalibratorEmployee(user.getUsername());
        List<String> role = usersService.getRoles(user.getUsername());
        return calibratorService.getAllCalibratorEmployee(role, employee);
    }

    @RequestMapping(value = "remove/calibratorEmployee", method = RequestMethod.PUT)
    public void removeCalibratorEmployee(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails userDetails,
                                         @RequestBody VerificationProviderEmployeeDTO verificationUpdatingDTO) {
        User user = calibratorService.oneCalibratorEmployee(userDetails.getUsername());
        if (calibratorService.isAdmin(user)) {
            String idVerification = verificationUpdatingDTO.getIdVerification();
            calibratorService.removeCalibratorEmployee(idVerification, user);
        }
    }

    @RequestMapping(value = "graphicmainpanel", method = RequestMethod.GET)
    public List<ProviderEmployeeGraphic> graphicMainPanel
            (@RequestParam String fromDate, @RequestParam String toDate,
             @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        Long idOrganization = user.getOrganizationId();
        List<ProviderEmployeeGraphic> list = null;
        try {
            Date dateFrom = calibratorEmployeeService.convertToDate(fromDate);
            Date dateTo = calibratorEmployeeService.convertToDate(toDate);
            list = calibratorEmployeeService.buidGraphicMainPanel(dateFrom, dateTo, idOrganization);
        } catch (Exception e) {
            logger.error("Failed to get graphic data");
            logger.error(e); // for prevent critical issue "Either log or rethrow this exception"
        }
        return list;
    }

    @RequestMapping(value = "piemainpanel", method = RequestMethod.GET)
    public Map pieMainPanel(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        Long idOrganization = user.getOrganizationId();
        Organization organization = organizationsService.getOrganizationById(idOrganization);
        Map tmp = new HashMap<>();
        tmp.put("NO_EMPLOYEE", verificationService.findCountOfAllCalibratorVerificationWithoutEmployee(organization));
        tmp.put("HAS_EMPLOYEE", verificationService.findCountOfAllCalibratorVerificationWithEmployee(organization));
        return tmp;
    }

}


