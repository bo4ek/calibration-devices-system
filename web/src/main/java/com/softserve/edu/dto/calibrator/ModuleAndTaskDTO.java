package com.softserve.edu.dto.calibrator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString

public class ModuleAndTaskDTO {

    String moduleSerialNumber;
    String taskId;
    Date dateOfTask;

    public ModuleAndTaskDTO(String moduleSerialNumber, String taskId) {
        this.moduleSerialNumber = moduleSerialNumber;
        this.taskId = taskId;
    }

    public ModuleAndTaskDTO(String moduleSerialNumber, String taskId, Date dateOfTask) {
        this.moduleSerialNumber = moduleSerialNumber;
        this.taskId = taskId;
        this.dateOfTask = dateOfTask;
    }
}
