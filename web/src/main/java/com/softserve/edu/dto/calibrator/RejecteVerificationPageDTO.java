package com.softserve.edu.dto.calibrator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
@Setter
public class RejecteVerificationPageDTO {

    private Date rejectedCalibratorDate;
    private String rejectedReason;
    private String employeeRejected;

    public RejecteVerificationPageDTO() {
    }

    public RejecteVerificationPageDTO(Date rejectedCalibratorDate, String rejectedReason, String employeeRejected) {
        this.rejectedCalibratorDate = rejectedCalibratorDate;
        this.rejectedReason = rejectedReason;
        this.employeeRejected = employeeRejected;
    }

}
