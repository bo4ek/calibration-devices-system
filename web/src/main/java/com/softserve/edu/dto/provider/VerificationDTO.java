package com.softserve.edu.dto.provider;

import com.softserve.edu.dto.application.ClientStageVerificationDTO;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class VerificationDTO extends ClientStageVerificationDTO {

    private String id;
    private Status status;
    private Date initialDate;
    private Date expirationDate;
    private String device;
    private String deviceNumber;
    private String counterNumber;
    private String counterSymbol;
    private String counterStandartSize;
    private String counterRealiseYear;
    private String counterManufacturer;
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
        this.providerEmployee = providerEmployee == null ? "" : providerEmployee.getLastName() + " " + providerEmployee.getFirstName() + " " + providerEmployee.getMiddleName();
        this.calibrator = calibrator == null ? "" : calibrator.getName();
        this.calibratorEmployee = calibratorEmployee == null ? "" : calibratorEmployee.getLastName() + " " + calibratorEmployee.getFirstName() + " " + calibratorEmployee.getMiddleName();
        this.stateVerificator = stateVerificator == null ? "" : stateVerificator.getName();
        this.stateVerificatorEmployee = stateVerificatorEmployee == null ? "" : stateVerificatorEmployee.getLastName() + " " + stateVerificatorEmployee.getFirstName() + " " + stateVerificatorEmployee.getMiddleName();
        this.providerAddress = (provider == null) ? null : provider.getAddress();
        this.calibratorAddress = (calibrator == null) ? null : calibrator.getAddress();
    }

    //add new constructor for delegation rejected message
    public VerificationDTO(ClientData clientData, String id, Date initialDate, Date expirationDate, Status status,
                           Organization calibrator, User calibratorEmployee, Device device, Organization provider,
                           User providerEmployee, Organization stateVerificator, User stateVerificatorEmployee, String rejectedMessage) {

        this(clientData, id, initialDate, expirationDate, status, calibrator, calibratorEmployee, device, provider,
                providerEmployee, stateVerificator, stateVerificatorEmployee);
        this.rejectedMessage = rejectedMessage;
    }

    public VerificationDTO(ClientData clientData, String id, Date initialDate, Date expirationDate, Status status,
                           Organization calibrator, User calibratorEmployee, Device device, Organization provider,
                           User providerEmployee, Organization stateVerificator, User stateVerificatorEmployee, String rejectedMessage, Counter counter) {

        this(clientData, id, initialDate, expirationDate, status, calibrator, calibratorEmployee, device, provider,
                providerEmployee, stateVerificator, stateVerificatorEmployee, rejectedMessage);
        this.counterNumber = counter.getNumberCounter();
        if (counter.getCounterType() != null) {
            this.counterSymbol = counter.getCounterType().getSymbol();
            this.counterStandartSize = counter.getCounterType().getStandardSize();
            this.counterRealiseYear = counter.getReleaseYear();
            this.counterManufacturer = counter.getCounterType().getManufacturer();
        }
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
}
