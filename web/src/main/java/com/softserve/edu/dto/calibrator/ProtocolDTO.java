package com.softserve.edu.dto.calibrator;


import com.softserve.edu.entity.verification.Verification;
import lombok.Setter;
import lombok.Getter;

import java.util.Date;

/**
 * @author Veronika
 */
@Getter
@Setter
public class ProtocolDTO {

    private String id;
    private String sentToCalibratorDate;
    private String fullName;
    private String address;
    private String nameProvider;
    private String nameCalibrator;
    private String status;
    private String comment;
    private boolean isManual;
    private String moduleNumber;
    private int realiseYear;
    private String numberCounter;
    private Date initialDate;
    private Verification.ReadStatus readStatus;


    public ProtocolDTO() {

    }

    public ProtocolDTO(String id, String sentToCalibratorDate, String firstName, String lastName, String middleName, String address,
                       String nameProvider, String nameCalibrator, String status, String comment, boolean isManual, String moduleNumber,
                       int realiseYear, String numberCounter, Date initialDate, Verification.ReadStatus readStatus) {
        this.id = id;
        this.sentToCalibratorDate = sentToCalibratorDate;
        this.fullName = "" + lastName + " " + firstName + " " + middleName;
        this.address = address;
        this.nameProvider = nameProvider;
        this.nameCalibrator = nameCalibrator;
        this.status = status;
        this.comment = comment;
        this.isManual = isManual;
        this.moduleNumber = moduleNumber;
        this.realiseYear = realiseYear;
        this.numberCounter = numberCounter;
        this.initialDate = initialDate;
        this.readStatus = readStatus;
    }

}
