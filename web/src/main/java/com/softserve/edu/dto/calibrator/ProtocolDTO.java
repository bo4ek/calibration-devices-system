package com.softserve.edu.dto.calibrator;


import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import lombok.Setter;
import lombok.Getter;

import java.util.Date;

@Getter
@Setter
public class ProtocolDTO {

    private String id;
    private String nameCalibrator;
    private String status;
    private String comment;
    private boolean isManual;
    private String moduleNumber;
    private int realiseYear;
    private String numberOfCounter;
    private String initialDate;
    private Verification.ReadStatus readStatus;
    private String serialNumber;
    private String numberOfProtocol;
    private String providerEmployee;
    private String stateVerificatorEmployee;
    private String sentToVerificatorDate;


    public ProtocolDTO() {

    }

    public ProtocolDTO(String id, String nameCalibrator, String status, String comment, boolean isManual, String moduleNumber,
                       int realiseYear, String numberOfCounter, String initialDate, Verification.ReadStatus readStatus, String serialNumber, String numberOfProtocol,
                       User providerEmployee, User stateVerificatorEmployee, String sentToVerificatorDate) {
        this.id = id;
        this.nameCalibrator = nameCalibrator;
        this.status = status;
        this.comment = comment;
        this.isManual = isManual;
        this.moduleNumber = moduleNumber;
        this.realiseYear = realiseYear;
        this.numberOfCounter = numberOfCounter;
        this.initialDate = initialDate;
        this.readStatus = readStatus;
        this.serialNumber = serialNumber;
        this.numberOfProtocol = numberOfProtocol;
        if (providerEmployee != null) {
            if (providerEmployee.getMiddleName() != null) {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName() + " " + providerEmployee.getMiddleName();
            } else {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName();
            }
        }
        if (stateVerificatorEmployee != null) {
            if (stateVerificatorEmployee.getMiddleName() != null) {
                this.stateVerificatorEmployee = stateVerificatorEmployee.getLastName() + " " + stateVerificatorEmployee.getFirstName() + " " + stateVerificatorEmployee.getMiddleName();
            } else {
                this.stateVerificatorEmployee = stateVerificatorEmployee.getLastName() + " " + stateVerificatorEmployee.getFirstName();
            }
        }
        this.sentToVerificatorDate = sentToVerificatorDate;
    }

}
