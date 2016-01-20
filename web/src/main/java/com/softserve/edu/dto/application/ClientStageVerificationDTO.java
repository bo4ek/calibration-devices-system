package com.softserve.edu.dto.application;

import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.verification.ClientData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientStageVerificationDTO {
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private String secondPhone;
    private String region;
    private String locality;
    private String district;
    private String street;
    private String building;
    private String flat;
    private Long providerId;
    private Long deviceId;
    private String verificationId;
    private String comment;
    private int quantity;

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
