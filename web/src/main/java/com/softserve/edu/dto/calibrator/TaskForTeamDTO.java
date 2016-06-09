package com.softserve.edu.dto.calibrator;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.enumeration.verification.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TaskForTeamDTO {

    private Long taskID;

    private Date dateOfTask;

    private String name;

    private String leaderFullName;

    private String leaderPhone;

    private Integer numOfVerifications;

    private Integer numOfCompletedVerifications;

    private String status;

    public TaskForTeamDTO() {
    }

    public TaskForTeamDTO(Long taskID, Date dateOfTask, String name, String leaderFullName, String leaderPhone, Status status,
                          Integer numOfVerifications, Integer numOfCompletedVerifications) {
        this.taskID = taskID;
        this.dateOfTask = dateOfTask;
        this.name = name;
        this.leaderFullName = leaderFullName;
        this.leaderPhone = leaderPhone;
        this.status = status.toString();
        this.numOfVerifications = numOfVerifications;
        this.numOfCompletedVerifications = numOfCompletedVerifications;
    }
}
