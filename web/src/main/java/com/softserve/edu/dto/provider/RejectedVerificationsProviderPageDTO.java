package com.softserve.edu.dto.provider;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@ToString
@Getter
@Setter
public class RejectedVerificationsProviderPageDTO {

    private String rejectedDate;
    private String rejectedDateEnd;
    private String rejectedReason;
    private String employeeProvider;
    private String calibratorName;
    private String client_full_name;
    private String district;
    private String street;
    private String building;
    private String flat;
    private String verificationId;
    private String status;
    private Date rejectedCalibratorDate;


    public RejectedVerificationsProviderPageDTO() {
    }

    public RejectedVerificationsProviderPageDTO(Date rejectedCalibratorDate, String rejectedReason, String employeeProvider) {
        this.rejectedCalibratorDate = rejectedCalibratorDate;
        this.rejectedReason = rejectedReason;
        this.employeeProvider = employeeProvider;
    }

    public RejectedVerificationsProviderPageDTO(Date rejectedCalibratorDate, String rejectedReason, String employeeProvider, String calibratorName, String customerName,
                                       String district, String street, String building, String flat, String verificationId,String status) {
        this.rejectedCalibratorDate = rejectedCalibratorDate;
        this.rejectedReason = rejectedReason;
        this.employeeProvider = employeeProvider;
        this.calibratorName = calibratorName;
        this.client_full_name = customerName;
        this.district = district;
        this.street = street;
        this.building = building;
        this.flat = flat;
        this.verificationId = verificationId;
        this.status = status;
    }

    public RejectedVerificationsProviderPageDTO(Date rejectedCalibratorDate,String rejectedDate,String rejectedDateEnd ,String rejectedReason, String employeeProvider, String calibratorName, String customerName,
                                                String district, String street, String building, String flat, String verificationId,String status) {
        this(rejectedCalibratorDate,rejectedReason,employeeProvider,calibratorName,customerName,district,street,building,flat,verificationId,status);
        this.rejectedDateEnd = rejectedDateEnd;
        this.rejectedDate = rejectedDate;
    }
}
