package com.softserve.edu.dto.verificator;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RejectedInfoFilterSearch {
    private String starDate;
    private String endDate;
    private String rejectedReason;
    private String employeeRejected;
    private String providerName;
    private String customerName;
    private String district;
    private String street;
    private String building;
    private String flat;
    private String verificationId;
}
