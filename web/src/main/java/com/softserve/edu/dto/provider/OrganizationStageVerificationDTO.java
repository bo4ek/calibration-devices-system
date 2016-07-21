package com.softserve.edu.dto.provider;

import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.verification.ClientData;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class OrganizationStageVerificationDTO {
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private String secondPhone;

    private String comment;

    private Integer queue;

    private String region;
    private String locality;
    private String district;
    private String street;
    private String mailIndex;
    private String building;
    private String flat;
    private Long providerId;
    private Long calibratorId;
    private String calibratorName;
    private String deviceName;
    private Device.DeviceType deviceType;
    private Long deviceId;
    private String verificationId;

    private Boolean dismantled;

    private Date dateOfDismantled;
    private Date dateOfMounted;
    private String numberCounter;
    private Boolean sealPresence;
    private String releaseYear;

    private String symbol;
    private String standardSize;

    private String entrance;
    private String doorCode;
    private String floor;
    private Date dateOfVerif;
    private String timeFrom;
    private String timeTo;

    private Boolean serviceability;
    private Date noWaterToDate;
    private String notes;

    private Long initialDate;
    private Long expirationDate;

    private String accumulatedVolume;

    private byte quantity;

    private Boolean verificationWithDismantle;

    private Boolean editAll;

    private Long taskId;
    private Long groupId;

    public OrganizationStageVerificationDTO() {
    }

    public OrganizationStageVerificationDTO(ClientData clientData, String comment, Address address, String verificationId, String calibratorName,
                                            String entrance, String doorCode, String floor, Date dateOfVerif, Boolean serviceability,
                                            Date noWaterToDate, String notes, String timeFrom, String timeTo, Boolean dismantled, Date dateOfDismantled,
                                            Date dateOfMounted, String numberCounter, String releaseYear, String accumulatedVolume, String symbol,
                                            String standardSize, String deviceName, Boolean sealPresence, Device.DeviceType deviceType,
                                            Long deviceId, Boolean verificationWithDismantle, Long taskId, Long groupId, Integer queue) {
        this.firstName = clientData.getFirstName();
        this.lastName = clientData.getLastName();
        this.middleName = clientData.getMiddleName();
        this.email = clientData.getEmail();
        this.phone = clientData.getPhone();
        this.secondPhone = clientData.getSecondPhone();

        this.comment = comment;

        this.region = address.getRegion();
        this.locality = address.getLocality();
        this.district = address.getDistrict();
        this.street = address.getStreet();
        this.building = address.getBuilding();
        this.flat = address.getFlat();
        this.mailIndex = address.getMailIndex();

        this.verificationId = verificationId;
        this.calibratorName = calibratorName;
        this.entrance = entrance;
        this.doorCode = doorCode;
        this.floor = floor;
        this.dateOfVerif = dateOfVerif;
        this.serviceability = serviceability;
        this.noWaterToDate = noWaterToDate;
        this.notes = notes;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;

        this.dismantled = dismantled;
        this.sealPresence = sealPresence;

        this.dateOfDismantled = dateOfDismantled;
        this.dateOfMounted = dateOfMounted;
        this.numberCounter = numberCounter;
        this.releaseYear = releaseYear;
        this.accumulatedVolume = accumulatedVolume;

        this.symbol = symbol;
        this.standardSize = standardSize;

        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.taskId = taskId;
        this.groupId = groupId;
        this.queue = queue;

        this.verificationWithDismantle = verificationWithDismantle;
    }
}