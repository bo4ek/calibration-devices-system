package com.softserve.edu.entity.device;

import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "CALIBRATION_MODULE")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class CalibrationModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moduleId;

    @ElementCollection
    @JoinTable(name = "CALIBRATION_MODULE_DEVICE_TYPE", joinColumns = @JoinColumn(name = "moduleId"))
    @Enumerated(EnumType.STRING)
    @Column(name = "deviceType", length = 20)
    private Set<Device.DeviceType> deviceType = new LinkedHashSet<>();

    private String organizationCode;

    private String condDesignation;

    private String serialNumber;

    private String employeeFullName;

    private String telephone;

    @Enumerated(EnumType.STRING)
    private ModuleType moduleType;

    private String email;

    private String calibrationType;

    private String moduleNumber; // generates

    @Column(columnDefinition = "bit(1) default 1")
    private Boolean isActive = true;

    @Temporal(TemporalType.DATE)
    private Date workDate;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    private Set<CalibrationTask> tasks;

    public CalibrationModule(Set<Device.DeviceType> deviceType, String organizationCode,
                             String condDesignation, String serialNumber,
                             String employeeFullName, String telephone,
                             ModuleType moduleType, String email, String calibrationType,
                             Date workDate) {
        this.deviceType = deviceType;
        this.organizationCode = organizationCode;
        this.condDesignation = condDesignation;
        this.serialNumber = serialNumber;
        this.employeeFullName = employeeFullName;
        this.telephone = telephone;
        this.moduleType = moduleType;
        this.email = email;
        this.calibrationType = calibrationType;
        this.workDate = workDate;
    }

    public void updateFields(CalibrationModule calibrationModule) {
        this.deviceType = calibrationModule.getDeviceType();
        this.organizationCode = calibrationModule.getOrganizationCode();
        this.condDesignation = calibrationModule.getCondDesignation();
        this.serialNumber = calibrationModule.getSerialNumber();
        this.employeeFullName = calibrationModule.getEmployeeFullName();
        this.telephone = calibrationModule.getTelephone();
        this.moduleType = calibrationModule.getModuleType();
        this.email = calibrationModule.getEmail();
        this.calibrationType = calibrationModule.getCalibrationType();
        this.workDate = calibrationModule.getWorkDate();
    }

    public enum ModuleType {
        INSTALLATION_FIX,
        INSTALLATION_PORT
    }
}
