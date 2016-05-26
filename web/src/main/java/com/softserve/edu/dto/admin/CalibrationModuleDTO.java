package com.softserve.edu.dto.admin;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
public class CalibrationModuleDTO {

    private Long moduleId;
    private List<String> deviceType;
    private String organizationCode;
    private String condDesignation;
    private String serialNumber;
    private String employeeFullName;
    private String telephone;
    private CalibrationModule.ModuleType moduleType;
    private String email;
    private String calibrationType;
    private String moduleNumber;
    private Boolean isActive;
    private Boolean hasTask;
    private Date workDate;
    private Long startDateToSearch = null;
    private Long endDateToSearch = null;

    public CalibrationModuleDTO() {
    }

    public CalibrationModuleDTO(String condDesignation, CalibrationModule.ModuleType moduleType, String serialNumber) {
        this.condDesignation = condDesignation;
        this.moduleType = moduleType;
        this.serialNumber = serialNumber;
    }

    public CalibrationModuleDTO(Long moduleId, List<String> deviceType, String organizationCode,
                                String condDesignation, String serialNumber,
                                String employeeFullName, String telephone, String moduleNumber,
                                Boolean isActive, CalibrationModule.ModuleType moduleType, String email,
                                String calibrationType, Date workDate, Set<CalibrationTask> tasks) {
        this.moduleId = moduleId;
        this.deviceType = deviceType;
        this.organizationCode = organizationCode;
        this.condDesignation = condDesignation;
        this.serialNumber = serialNumber;
        this.employeeFullName = employeeFullName;
        this.telephone = telephone;
        this.moduleNumber = moduleNumber;
        this.isActive = isActive;
        this.moduleType = moduleType;
        this.email = email;
        this.calibrationType = calibrationType;
        this.workDate = workDate;
        if (tasks != null && !tasks.isEmpty()) {
            this.hasTask = true;
        }
    }

    public CalibrationModuleDTO(Long moduleId, List<String> deviceType, String organizationCode,
                                String condDesignation, String serialNumber, String employeeFullName,
                                String telephone, String moduleNumber, Boolean isActive,
                                CalibrationModule.ModuleType moduleType, String email, String calibrationType,
                                Set<CalibrationTask> tasks, Date workDate, Long startDate, Long endDate) {
        this(moduleId, deviceType, organizationCode, condDesignation, serialNumber, employeeFullName,
                telephone, moduleNumber, isActive, moduleType, email, calibrationType, workDate, tasks);
        this.startDateToSearch = startDate;
        this.endDateToSearch = endDate;
    }
}
