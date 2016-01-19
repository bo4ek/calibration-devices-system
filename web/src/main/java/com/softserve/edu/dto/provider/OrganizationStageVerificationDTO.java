package com.softserve.edu.dto.provider;

import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.verification.ClientData;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationStageVerificationDTO {
	private String firstName;
	private String lastName;
	private String middleName;
	private String email;
	private String phone;
	private String secondPhone;

	private String comment;

	private String region;
	private String locality;
	private String district;
	private String street;
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

	private Long dateOfDismantled;
	private Long dateOfMounted;
	private String numberCounter;
	private Boolean sealPresence;
	private String releaseYear;

	private String symbol;
	private String standardSize;


	private String entrance;
	private String doorCode;
	private String floor;
	private Long dateOfVerif;
	private String timeFrom;
	private String timeTo;

	private Boolean serviceability;
	private Long noWaterToDate;
	private String notes;

	private Long initialDate;
	private Long expirationDate;


	public OrganizationStageVerificationDTO() {
	}

	public OrganizationStageVerificationDTO(ClientData clientData, Address address, Long providerId, Long calibratorId,
											Long deviceId, String verificationId) {
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
		this.calibratorId = calibratorId;
		this.deviceId = deviceId;
		this.verificationId = verificationId;
	}


	public OrganizationStageVerificationDTO(ClientData clientData, String comment, Address address, String verificationId, String calibratorName,
											String entrance, String doorCode, String floor, Long dateOfVerif, Boolean serviceability,
											Long noWaterToDate, String notes, String timeFrom, String timeTo, Boolean dismantled, Long dateOfDismantled,
											Long dateOfMounted, String numberCounter, String releaseYear, String symbol,
											String standardSize, String deviceName, Boolean sealPresence, Device.DeviceType deviceType,
											Long deviceId) {
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

		this.symbol = symbol;
		this.standardSize = standardSize;

		this.deviceName = deviceName;
		this.deviceType = deviceType;
		this.deviceId = deviceId;

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getFlat() {
		return flat;
	}

	public void setFlat(String flat) {
		this.flat = flat;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSecondPhone() {
		return secondPhone;
	}

	public void setSecondPhone(String secondPhone) {
		this.secondPhone = secondPhone;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public Long getCalibratorId() {
		return calibratorId;
	}

	public void setCalibratorId(Long calibratorId) {
		this.calibratorId = calibratorId;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public String getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(String verificationId) {
		this.verificationId = verificationId;
	}

    @Override
    public String toString() {
        return "OrganizationStageVerificationDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", secondPhone='" + secondPhone + '\'' +
                ", comment='" + comment + '\'' +
                ", region='" + region + '\'' +
                ", locality='" + locality + '\'' +
                ", district='" + district + '\'' +
                ", street='" + street + '\'' +
                ", building='" + building + '\'' +
                ", flat='" + flat + '\'' +
                ", providerId=" + providerId +
                ", calibratorId=" + calibratorId +
                ", calibratorName='" + calibratorName + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceType=" + deviceType +
                ", deviceId=" + deviceId +
                ", verificationId='" + verificationId + '\'' +
                ", dismantled=" + dismantled +
                ", dateOfDismantled=" + dateOfDismantled +
                ", dateOfMounted=" + dateOfMounted +
                ", numberCounter='" + numberCounter + '\'' +
                ", sealPresence=" + sealPresence +
                ", releaseYear='" + releaseYear + '\'' +
                ", symbol='" + symbol + '\'' +
                ", standardSize='" + standardSize + '\'' +
                ", entrance='" + entrance + '\'' +
                ", doorCode='" + doorCode + '\'' +
                ", floor='" + floor + '\'' +
                ", dateOfVerif=" + dateOfVerif +
                ", timeFrom='" + timeFrom + '\'' +
                ", timeTo='" + timeTo + '\'' +
                ", serviceability=" + serviceability +
                ", noWaterToDate=" + noWaterToDate +
                ", notes='" + notes + '\'' +
                ", initialDate=" + initialDate +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
