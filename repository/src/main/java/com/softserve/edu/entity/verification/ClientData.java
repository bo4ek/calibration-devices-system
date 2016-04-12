package com.softserve.edu.entity.verification;

import com.softserve.edu.entity.Address;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientData {
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private String secondPhone;

    @Embedded
    private Address clientAddress;

    public ClientData(String firstName, String lastName, String middleName, String phone, String secondPhone, Address clientAddress) {
        this(firstName, lastName, middleName, null, phone, secondPhone, clientAddress);
    }
    public ClientData(String firstName, String lastName, String middleName, String phone,  Address clientAddress) {
        this(firstName, lastName, middleName, null, phone, null, clientAddress);
    }
    public ClientData(String firstName, String lastName, String middleName, String email, String phone, String secondPhone, Address clientAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = email;
        this.phone = phone;
        this.setSecondPhone(secondPhone);
        this.clientAddress = clientAddress;
    }

    public String getFullName() {
        return this.lastName + " " + this.firstName + " " + this.middleName;
    }

}
