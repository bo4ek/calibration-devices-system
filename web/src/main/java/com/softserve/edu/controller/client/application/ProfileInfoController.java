package com.softserve.edu.controller.client.application;

import com.softserve.edu.dto.user.UserDTO;
import com.softserve.edu.dto.admin.UsersPageItem;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "employee/profile")
public class ProfileInfoController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public UsersPageItem getUser(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails userDetails) {
        User user = userService.findOne(userDetails.getUsername());

        UsersPageItem usersPageItem = new UsersPageItem();
        usersPageItem.setFirstName(user.getFirstName());
        usersPageItem.setLastName(user.getLastName());
        usersPageItem.setUsername(user.getUsername());
        usersPageItem.setMiddleName(user.getMiddleName());
        usersPageItem.setEmail(user.getEmail());
        usersPageItem.setPhone(user.getPhone());
        usersPageItem.setSecondPhone(user.getSecondPhone());

        return usersPageItem;
    }

    @RequestMapping(value = "havePermissionToAssignPerson", method = RequestMethod.GET)
    public Boolean haveUserPermissionToAssignPerson(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails userDetails) {
        User user = userService.findOne(userDetails.getUsername());
        return user != null && userService.haveUserPermissionToAssignPerson(user);
    }


    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseEntity<HttpStatus> updateUser(@RequestBody UserDTO userDTO) {

        User currentUser = userService.findOne(userDTO.getUsername());

        currentUser.setFirstName(userDTO.getFirstName());
        currentUser.setLastName(userDTO.getLastName());
        currentUser.setMiddleName(userDTO.getMiddleName());
        currentUser.setEmail(userDTO.getEmail());
        currentUser.setPhone(userDTO.getPhone());
        currentUser.setSecondPhone(userDTO.getSecondPhone());

        userService.updateUser(currentUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
