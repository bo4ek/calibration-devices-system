package com.softserve.edu.controller.stateverificator;

import com.softserve.edu.controller.provider.ProviderEmployeeController;
import com.softserve.edu.dto.provider.VerificationProviderEmployeeDTO;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.provider.buildGraphic.ProviderEmployeeGraphic;
import com.softserve.edu.service.state.verificator.StateVerificatorEmployeeService;
import com.softserve.edu.service.state.verificator.StateVerificatorService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.user.UserService;
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
@RequestMapping(value = "verificator/admin/users")
public class StateVerificatorEmployeeController {

	Logger logger = Logger.getLogger(ProviderEmployeeController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationService organizationsService;

	@Autowired
	private StateVerificatorEmployeeService stateVerificatorEmployeeService;

	@Autowired
	private StateVerificatorService stateVerificatorService;

	@Autowired
	private VerificationService verificationService;

	/**
	 * Check whereas {@code username} is available, i.e. it is possible to
	 * create new user with this {@code username}
	 *
	 * @param username  username         
	 * @return {@literal true} if {@code username} available or else  {@literal false}     
	 */
	@RequestMapping(value = "available/{username}", method = RequestMethod.GET)
	public Boolean isValidUsername(@PathVariable String username) {
		boolean isAvailable = false;
		if (username != null) {
			isAvailable = userService.isExistsWithUsername(username);
		}
		return isAvailable;
	}

	/**
	 * add employee, set organization
	 * @param stateVerificatorEmployee
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public ResponseEntity<HttpStatus> addEmployee(
			@RequestBody User stateVerificatorEmployee,
			@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
		Organization employeeOrganization = organizationsService.getOrganizationById(user.getOrganizationId());
		stateVerificatorEmployee.setOrganization(employeeOrganization);
		
		stateVerificatorEmployeeService.addEmployee(stateVerificatorEmployee);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * return All Verificator Employee
	 * using for add Employee to verification
	 * @param user
	 * @return
	 */

	@RequestMapping(value = "new/verificatorEmployees", method = RequestMethod.GET)
	public List<EmployeeDTO> employeeStateVerificatorVerification(
			@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
		User employee = stateVerificatorEmployeeService.oneProviderEmployee(user.getUsername());
		List<String> role = userService.getRoles(user.getUsername());
		return stateVerificatorService.getAllVerificatorEmployee(role, employee);
	}

	/**
	 * set employee on a current verification,
	 * and than this guy will have opportunity to
	 * check this verification.
	 * @param verificationProviderEmployeeDTO
	 */
	@RequestMapping(value = "assign/verificatorEmployee", method = RequestMethod.PUT)
	public void assignVerificatorEmployee(@RequestBody VerificationProviderEmployeeDTO verificationProviderEmployeeDTO) {
		String usernameVerificator = verificationProviderEmployeeDTO.getEmployeeCalibrator().getUsername();
		String idVerification = verificationProviderEmployeeDTO.getIdVerification();
		User employeeCalibrator = stateVerificatorEmployeeService.oneProviderEmployee(usernameVerificator);
		stateVerificatorService.assignVerificatorEmployee(idVerification, employeeCalibrator);
	}

	/**
	 * remove assigned employee on a current verification
	 * @param verificationUpdatingDTO
	 */
	@RequestMapping(value = "remove/verificatorEmployee", method = RequestMethod.PUT)
	public void removeVerificatorEmployee(@RequestBody VerificationProviderEmployeeDTO verificationUpdatingDTO) {
		String idVerification = verificationUpdatingDTO.getIdVerification();
		stateVerificatorService.assignVerificatorEmployee(idVerification, null);
	}

	@RequestMapping(value = "graphicmainpanel", method = RequestMethod.GET)
	public List<ProviderEmployeeGraphic> graphicMainPanel
			(@RequestParam String fromDate, @RequestParam String toDate,
			 @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
		Long idOrganization = user.getOrganizationId();
		List<ProviderEmployeeGraphic> list = null;
		try {
			Date dateFrom = stateVerificatorService.convertToDate(fromDate);
			Date dateTo = stateVerificatorService.convertToDate(toDate);

			list = stateVerificatorService.buidGraphicMainPanel(dateFrom, dateTo, idOrganization);

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
		tmp.put("NO_EMPLOYEE", verificationService.findCountOfAllVerificatorVerificationWithoutEmployee(organization));
		tmp.put("HAS_EMPLOYEE", verificationService.findCountOfAllVerificatorVerificationWithEmployee(organization));
		return tmp;
	}

}
