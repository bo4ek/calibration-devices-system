package com.softserve.edu.dto.calibrator;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.Verification;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CalibrationTaskDTO {

    private Long taskID;

    private String moduleNumber;

    private String moduleSerialNumber;

    private String status;

    private Date dateOfTask;

    private CalibrationModule.ModuleType moduleType;

    private String employeeFullName;

    private String telephone;

    private List<String> verificationsId;

    private Integer numOfVerifications;

    private Integer numOfCompletedVerifications;

    private Integer numOfNotAcceptedVerifications;

    public CalibrationTaskDTO() {
    }

    public CalibrationTaskDTO(Long taskID, String moduleSerialNumber, Date dateOfTask,
                              CalibrationModule.ModuleType moduleType, String employeeFullName, String telephone,
                              Status status, Integer numOfVerifications, Integer numOfCompletedVerifications) {
        this.taskID = taskID;
        this.moduleSerialNumber = moduleSerialNumber;
        this.dateOfTask = dateOfTask;
        this.moduleType = moduleType;
        this.employeeFullName = employeeFullName;
        this.telephone = telephone;
        this.numOfVerifications = numOfVerifications;
        this.status = status.toString();
        this.numOfCompletedVerifications = numOfCompletedVerifications;

    }

    public CalibrationTaskDTO(Long taskID, String moduleSerialNumber, Date dateOfTask,
                              CalibrationModule.ModuleType moduleType, String employeeFullName, String telephone,
                              Status status, Integer numOfVerifications, Integer numOfCompletedVerifications, Integer numOfNotAcceptedVerifications) {

        this(taskID, moduleSerialNumber, dateOfTask, moduleType, employeeFullName, telephone, status, numOfVerifications, numOfCompletedVerifications);
        this.numOfNotAcceptedVerifications = numOfNotAcceptedVerifications;

    }

    private Integer countNumberOfTestedVerification(Set<Verification> verifications) {
        Integer count = 0;
        for (Verification verification : verifications) {
            if (verification.getStatus().equals(Status.TEST_COMPLETED) || verification.getStatus().equals(Status.SENT_TO_VERIFICATOR)
                    || verification.getStatus().equals(Status.TEST_OK) || verification.getStatus().equals(Status.TEST_NOK)) {
                count++;
            }
        }
        return count;
    }
}
