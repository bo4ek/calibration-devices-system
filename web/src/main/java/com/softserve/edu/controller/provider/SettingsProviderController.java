package com.softserve.edu.controller.provider;

import com.softserve.edu.dto.EmployeeDTO;
import com.softserve.edu.dto.FieldDTO;
import com.softserve.edu.dto.NewPasswordDTO;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/provider/settings/")
public class SettingsProviderController {

    Logger logger = Logger.getLogger(SettingsProviderController.class);

    @Autowired
    private UserService userService;

    /**
     * Changes user's password
     *
     * @param newPasswordDTO container for new and old password
     * @param userDetails user details stored in session
     * @return 200 OK if changed, else 409 CONFLICT
     */
    @RequestMapping(value = "password", method = RequestMethod.PUT)
    public ResponseEntity changePassword(
            @RequestBody NewPasswordDTO newPasswordDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        if (userService.changePassword(
                userDetails.getUsername(),
                newPasswordDTO.getOldPassword(),
                newPasswordDTO.getNewPassword())) {
            httpStatus = HttpStatus.OK;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Changes employee's field according to fieldDTO type
     *
     * @param fieldDTO filed to change
     * @param userDetails user details stored in session
     * @return 200 OK if changed, else 409 CONFLICT
     */
    @RequestMapping(value = "fields", method = RequestMethod.PUT)
    public ResponseEntity changeField(
            @RequestBody FieldDTO fieldDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        if (userService.changeField(
                userDetails.getUsername(),
                fieldDTO.getField(),
                fieldDTO.getType())) {
            httpStatus = HttpStatus.OK;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Returns information about current employee
     * Notice, that if current principal isn't employee then
     * method response is 409 CONFLICT
     *
     * @param userDetails user details stored in session
     * @return employeeDTO and 200 OK if request data is correct, else 409 CONFLICT
     */
    @RequestMapping(value = "fields", method = RequestMethod.GET)
    public ResponseEntity<EmployeeDTO> getEmployeeData(@AuthenticationPrincipal UserDetails userDetails) {
        ResponseEntity<EmployeeDTO> response;
        try {
        	User employee = userService.getUser(userDetails.getUsername());
            EmployeeDTO employeeDTO = new EmployeeDTO(
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getMiddleName(),
                    employee.getEmail(),
                    employee.getPhone()
            );
            response = new ResponseEntity<>(employeeDTO, HttpStatus.OK);
        } catch (ClassCastException e) {
            response = new ResponseEntity<>(new EmployeeDTO(), HttpStatus.CONFLICT);
            logger.info(e);
        }
        return response;
    }
}
