package com.softserve.edu.dto.admin;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "username")
public class UsersPageItem {

    private String username;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private String secondPhone;
    private Boolean isAvailable;
    private String organization;
    private Long countOfVerification;
    private Long calibratorTasks;
    private Long stateVerificatorTasks;

    public UsersPageItem(String username, List<String> roles, String firstName, String lastName,
                         String middleName, String phone, String secondPhone, String organization,
                         Long countOfVerification, Long calibratorTasks, Long stateVerificatorTasks, Boolean isAvailable) {

        this.username = username;
        StringBuilder stringBuilder = new StringBuilder();
        for (String someRole : roles) {
            stringBuilder.append(someRole).append(" ");
        }
        role = stringBuilder.toString().trim();
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.phone = phone;
        this.secondPhone = secondPhone;
        this.organization = organization;
        this.countOfVerification = countOfVerification;
        this.calibratorTasks = calibratorTasks;
        this.stateVerificatorTasks = stateVerificatorTasks;
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "UsersPageItem{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", secondPhone='" + secondPhone + '\'' +
                ", isAvailable=" + isAvailable +
                ", organization='" + organization + '\'' +
                ", countOfVerification=" + countOfVerification +
                '}';
    }
}