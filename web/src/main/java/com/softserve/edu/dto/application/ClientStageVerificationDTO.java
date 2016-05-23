package com.softserve.edu.dto.application;

import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.verification.ClientData;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Setter
@Getter
public class ClientStageVerificationDTO {
    @NotNull
    @Size(max = 20, min = 2)
    private String firstName;
    @NotNull
    @Size(max = 20, min = 2)
    private String lastName;
    @NotNull
    @Size(max = 20, min = 2)
    private String middleName;
    @NotNull
    private String email;
    @NotNull
    @Digits(integer = 9, fraction = 0)
    @Size(min = 9, max = 9)
    private String phone;
    @Digits(integer = 9, fraction = 0)
    private String secondPhone;
    @NotNull
    private String region;
    @NotNull
    private String locality;
    @NotNull
    private String district;
    @NotNull
    private String street;
    @NotNull
    private String building;
    @NotNull
    private String flat;
    @NotNull
    @Max(99999)
    @Digits(integer = 5, fraction = 0)
    private String mailIndex;
    @NotNull
    private Long providerId;
    @NotNull
    @Digits(integer = 9, fraction = 0)
    private Long deviceId;
    private String verificationId;
    private String comment;
    private byte quantity;

    protected ClientStageVerificationDTO() {
    }

    /**
     *
     * @param clientData
     * @param address
     * @param providerId
     * @param deviceId
     * @param verificationId
     */
    public ClientStageVerificationDTO(ClientData clientData, Address address, Long providerId, Long deviceId,
                                      String verificationId) {
        this.firstName = clientData.getFirstName();
        this.lastName = clientData.getLastName();
        this.middleName = clientData.getMiddleName();
        this.email = clientData.getEmail();
        this.phone = clientData.getPhone();
        this.secondPhone = clientData.getSecondPhone();
        this.region = address.getRegion();
        this.locality = address.getLocality();
        this.district = address.getDistrict();
        this.street = address.getStreet();
        this.building = address.getBuilding();
        this.flat = address.getFlat();
        this.mailIndex = address.getMailIndex();
        this.providerId = providerId;
        this.deviceId = deviceId;
        this.verificationId = verificationId;
    }

    /**
     * Constructor for deligation comment
     *
     * @param clientData
     * @param address
     * @param providerId
     * @param deviceId
     * @param verificationId
     * @param comment
     */
    public ClientStageVerificationDTO(ClientData clientData, Address address, Long providerId, Long deviceId,
                                      String verificationId, String comment) {
        this(clientData, address, providerId, deviceId, verificationId);
        this.comment = comment;
    }
}
