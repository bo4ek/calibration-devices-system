package com.softserve.edu.dto.admin;

import com.softserve.edu.entity.device.CalibrationModule;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CalibrationModuleDTOLight {

    private Long moduleId;
    private String condDesignation;
    private CalibrationModule.ModuleType moduleType;
    private String serialNumber;


    public CalibrationModuleDTOLight() {
    }

    public CalibrationModuleDTOLight(String condDesignation, CalibrationModule.ModuleType moduleType, String serialNumber, Long moduleId) {
        this.condDesignation = condDesignation;
        this.moduleType = moduleType;
        this.serialNumber = serialNumber;
        this.moduleId = moduleId;
    }


}
