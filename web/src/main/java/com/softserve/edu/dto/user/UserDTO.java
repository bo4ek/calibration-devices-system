package com.softserve.edu.dto.user;

import java.util.Set;

import com.softserve.edu.entity.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String subdivision;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private String secondPhone;
    private Boolean isAvaliable;

    private Address address;

    private Set<String> userRoles ;

    public UserDTO(){}

    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", isAvaliable=" + isAvaliable +
                ", address=" + address +
                ", userRoles=" + userRoles +
                '}';
    }
}
