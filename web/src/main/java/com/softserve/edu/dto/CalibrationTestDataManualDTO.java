package com.softserve.edu.dto;

import com.softserve.edu.dto.admin.CounterTypeDTO;
import com.softserve.edu.dto.admin.UnsuitabilityReasonDTO;
import com.softserve.edu.entity.device.UnsuitabilityReason;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalibrationTestDataManualDTO {

    private String numberCounter;
    private String statusTestFirst;
    private String statusTestSecond;
    private String statusTestThird;
    private String statusCommon;
    private String verificationId;
    private int realiseYear;
    private boolean signed;
    private UnsuitabilityReasonDTO unsuitabilityReason;
    private CalibrationTestManualDTO calibrationTestManualDTO;

    public CalibrationTestDataManualDTO() {
    }

    public CalibrationTestDataManualDTO(String statusTestFirst, String statusTestSecond, String statusTestThird
            , String statusCommon, CalibrationTestManualDTO calibrationTestManualDTO, UnsuitabilityReasonDTO unsuitabilityReason
            , boolean signed, int realiseYear, String numberCounter) {
        this.statusTestFirst = statusTestFirst;
        this.statusTestSecond = statusTestSecond;
        this.statusTestThird = statusTestThird;
        this.statusCommon = statusCommon;
        this.calibrationTestManualDTO = calibrationTestManualDTO;
        this.unsuitabilityReason = unsuitabilityReason;
        this.signed = signed;
        this.realiseYear = realiseYear;
        this.numberCounter = numberCounter;
    }

    public CalibrationTestDataManualDTO(String numberCounter, String statusTestFirst, String statusTestSecond, String statusTestThird, String statusCommon, CalibrationTestManualDTO calibrationTestManualDTO) {
        this.numberCounter = numberCounter;
        this.statusTestFirst = statusTestFirst;
        this.statusTestSecond = statusTestSecond;
        this.statusTestThird = statusTestThird;
        this.statusCommon = statusCommon;
        this.calibrationTestManualDTO = calibrationTestManualDTO;
    }

}
