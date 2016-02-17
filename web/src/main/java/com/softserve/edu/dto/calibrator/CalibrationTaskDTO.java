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

    public CalibrationTaskDTO() {
    }

    public CalibrationTaskDTO(String moduleNumber, Date dateOfTask, List<String> verificationsId) {
        this.moduleNumber = moduleNumber;
        this.dateOfTask = dateOfTask;
        this.verificationsId = verificationsId;
    }

    public CalibrationTaskDTO(Long taskID, String moduleSerialNumber, Date dateOfTask, Set<Verification> verifications,
                              CalibrationModule.ModuleType moduleType, String employeeFullName, String telephone,
                              Status status) {
        this.taskID = taskID;
        this.moduleSerialNumber = moduleSerialNumber;
        this.dateOfTask = dateOfTask;
        this.moduleType = moduleType;
        this.employeeFullName = employeeFullName;
        this.telephone = telephone;
        this.numOfVerifications = verifications.size();
        this.status = status.toString();
        this.numOfCompletedVerifications = countNumberOfTestedVerification(verifications);
    }

    private Integer countNumberOfTestedVerification(Set<Verification> verifications) {
        Integer count = 0;
        for (Verification verification : verifications) {
            if (verification.getStatus().equals(Status.TEST_COMPLETED)) {
                count++;
            }
        }
        return count;
    }
}
