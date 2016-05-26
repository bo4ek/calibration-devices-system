package com.softserve.edu.service.utils;

import com.softserve.edu.entity.user.User;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    private String role;


    public EmployeeDTO() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public EmployeeDTO(String username, String firstName, String lastName, String middleName, String role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.role = role;
    }

    @Override
    public String toString() {
        // TODO Use StringBuilder here
        return "EmployeeDTO{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                '}';

    }

    public static List<EmployeeDTO> giveListOfEmployeeDTOs(List<User> list) {
        List<EmployeeDTO> listEmployee = new ArrayList<>();
        for (User providEmployee : list) {
            listEmployee.add(new EmployeeDTO(providEmployee.getUsername(),
                    providEmployee.getFirstName(), providEmployee.getLastName(),
                    providEmployee.getMiddleName(),null));
        }
        return listEmployee;
    }
}
