package com.softserve.edu.dto.calibrator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
@Setter
public class RejectedVerificationPageDTO {

    private Date rejectedCalibratorDate;
    private String rejectedReason;
    private String employeeRejected;
    private String providerName;
    private String customerName;
    private String district;
    private String street;
    private String building;
    private String flat;
    private String verificationId;


    public RejectedVerificationPageDTO() {
    }

    public RejectedVerificationPageDTO(Date rejectedCalibratorDate, String rejectedReason, String employeeRejected) {
        this.rejectedCalibratorDate = rejectedCalibratorDate;
        this.rejectedReason = rejectedReason;
        this.employeeRejected = employeeRejected;
    }

    public RejectedVerificationPageDTO(Date rejectedCalibratorDate, String rejectedReason, String employeeRejected, String providerName, String customerName,
                                      String district, String street, String building, String flat, String verificationId) {
        this.rejectedCalibratorDate = rejectedCalibratorDate;
        this.rejectedReason = rejectedReason;
        this.employeeRejected = employeeRejected;
        this.providerName = providerName;
        this.customerName = customerName;
        this.district = district;
        this.street = street;
        this.building = building;
        this.flat = flat;
        this.verificationId = verificationId;
    }

}
