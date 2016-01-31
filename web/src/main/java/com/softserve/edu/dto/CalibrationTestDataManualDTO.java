package com.softserve.edu.dto;

import com.softserve.edu.dto.admin.UnsuitabilityReasonDTO;
import com.softserve.edu.entity.device.UnsuitabilityReason;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Misha on 12/14/2015.
 */
@Getter
@Setter
public class CalibrationTestDataManualDTO {

    private String numberCounter;
    private String statusTestFirst;
    private String statusTestSecond;
    private String statusTestThird;
    private String statusCommon;
    private String verificationId;
    private Long counterId;
    private boolean signed;
    private UnsuitabilityReasonDTO unsuitabilityReason;
    private CalibrationTestManualDTO calibrationTestManualDTO;

    public CalibrationTestDataManualDTO() {
    }

    public CalibrationTestDataManualDTO(String statusTestFirst, String statusTestSecond, String statusTestThird, String statusCommon, CalibrationTestManualDTO calibrationTestManualDTO, UnsuitabilityReasonDTO unsuitabilityReason, boolean signed) {
        this.statusTestFirst = statusTestFirst;
        this.statusTestSecond = statusTestSecond;
        this.statusTestThird = statusTestThird;
        this.statusCommon = statusCommon;
        this.calibrationTestManualDTO = calibrationTestManualDTO;
        this.unsuitabilityReason = unsuitabilityReason;
        this.signed = signed;
    }

    public CalibrationTestDataManualDTO(String numberCounter, String statusTestFirst, String statusTestSecond, String statusTestThird, String statusCommon,Long counterId, CalibrationTestManualDTO calibrationTestManualDTO) {
        this.numberCounter = numberCounter;
        this.statusTestFirst = statusTestFirst;
        this.statusTestSecond = statusTestSecond;
        this.statusTestThird = statusTestThird;
        this.statusCommon = statusCommon;
        this.counterId = counterId;
        this.calibrationTestManualDTO = calibrationTestManualDTO;
    }

}
