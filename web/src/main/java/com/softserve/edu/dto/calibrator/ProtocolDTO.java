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
    private String nameProvider;
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


    public ProtocolDTO() {

    }

    public ProtocolDTO(String id, String nameProvider, String nameCalibrator, String status, String comment, boolean isManual, String moduleNumber,
                       int realiseYear, String numberOfCounter, String initialDate, Verification.ReadStatus readStatus, String serialNumber, String numberOfProtocol) {
        this.id = id;
        this.nameProvider = nameProvider;
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
    }

}
