package com.softserve.edu.dto.provider;

import com.softserve.edu.dto.application.ClientStageVerificationDTO;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class VerificationDTO extends ClientStageVerificationDTO {

    private String id;
    private Status status;
    private Date initialDate;
    private Date expirationDate;
    private String device;
    private String deviceNumber;
    private String provider;
    private String providerEmployee;
    private String calibrator;
    private String calibratorEmployee;
    private String stateVerificator;
    private String stateVerificatorEmployee;
    private Address providerAddress;
    private Address calibratorAddress;
    private String rejectedMessage;
    //private String comment;

    public VerificationDTO() {
    }

    public VerificationDTO(ClientData clientData, String id, Date initialDate, Date expirationDate, Status status,
                           Organization calibrator, User calibratorEmployee, Device device, Organization provider,
                           User providerEmployee, Organization stateVerificator, User stateVerificatorEmployee) {

        super(clientData, clientData.getClientAddress(), null, null, null);
        this.id = id;
        this.status = status;
        this.initialDate = initialDate;
        this.expirationDate = expirationDate;
        this.device = device == null ? "" : device.getDeviceType().name();
        this.deviceNumber = device == null ? "" : " : " + device.getNumber();
        this.provider = provider == null ? "" : provider.getName();
        if (providerEmployee != null) {
            if (providerEmployee.getMiddleName() != null) {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName() +
                        " " + providerEmployee.getMiddleName();
            } else {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName();
            }
        }
        this.calibrator = calibrator == null ? "" : calibrator.getName();
        this.calibratorEmployee = calibratorEmployee == null ? "" : calibratorEmployee.getFirstName() + " "
                + calibratorEmployee.getLastName() + " " + calibratorEmployee.getMiddleName();
        this.stateVerificator = stateVerificator == null ? "" : stateVerificator.getName();
        this.stateVerificatorEmployee = stateVerificatorEmployee == null ? "" : stateVerificatorEmployee.getFirstName() + " "
                + stateVerificatorEmployee.getLastName();
        this.providerAddress = (calibrator == null) ? null : provider.getAddress();
        this.calibratorAddress = (stateVerificator == null) ? null : calibrator.getAddress();
    }

    //add new constructor for delegation rejected message
    public VerificationDTO(ClientData clientData, String id, Date initialDate, Date expirationDate, Status status,
                           Organization calibrator, User calibratorEmployee, Device device, Organization provider,
                           User providerEmployee, Organization stateVerificator, User stateVerificatorEmployee, String rejectedMessage) {

        this(clientData, id, initialDate, expirationDate, status, calibrator, calibratorEmployee, device, provider,
                providerEmployee, stateVerificator, stateVerificatorEmployee);
        this.rejectedMessage = rejectedMessage;
    }

    /**
     * Constructor for delegation comment
     */
    public VerificationDTO(ClientData clientData, String id, Date initialDate, Date expirationDate, Status status,
                           Organization calibrator, User calibratorEmployee, Device device, Organization provider,
                           User providerEmployee, Organization stateVerificator, User stateVerificatorEmployee,
                           String rejectedMessage, String comment) {

        this(clientData, id, initialDate, expirationDate, status, calibrator, calibratorEmployee, device,
                provider, providerEmployee, stateVerificator, stateVerificatorEmployee, rejectedMessage);
        super.setComment(comment);
    }

    @Override
    public String toString() {
        return "VerificationDTO{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", initialDate=" + initialDate +
                ", expirationDate=" + expirationDate +
                ", device='" + device + '\'' +
                ", provider='" + provider + '\'' +
                ", providerEmployee='" + providerEmployee + '\'' +
                ", calibrator='" + calibrator + '\'' +
                ", calibratorEmployee='" + calibratorEmployee + '\'' +
                ", stateVerificator='" + stateVerificator + '\'' +
                ", stateVerificatorEmployee='" + stateVerificatorEmployee + '\'' +
                '}';
    }
}
