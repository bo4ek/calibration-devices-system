package com.softserve.edu.dto;

import java.util.Date;


public class AdditionalInfoDTO {

    private int entrance;
    private int doorCode;
    private int floor;
    private Long dateOfVerif;
    private String timeFrom;
    private String timeTo;
    private Boolean serviceability;
    private Long noWaterToDate;
    private String notes;
    private String verificationId;

    public AdditionalInfoDTO() {

    }

    public AdditionalInfoDTO(int entrance, int doorCode, int floor, Date dateOfVerif, String timeFrom, String timeTo, boolean serviceability,
                             Date noWaterToDate, String notes, String verificationId) {
        this.entrance = entrance;
        this.doorCode = doorCode;
        this.floor = floor;
        this.dateOfVerif = (dateOfVerif != null) ? dateOfVerif.getTime() : null;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.serviceability = serviceability;
        this.noWaterToDate = (noWaterToDate != null) ? noWaterToDate.getTime() : null;
        this.notes = notes;
        this.verificationId = verificationId;
    }

    public int getEntrance() {
        return entrance;
    }

    public void setEntrance(int entrance) {
        this.entrance = entrance;
    }

    public int getDoorCode() {
        return doorCode;
    }

    public void setDoorCode(int doorCode) {
        this.doorCode = doorCode;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public Long getDateOfVerif() {
        return dateOfVerif;
    }

    public void setDateOfVerif(Date dateOfVerif) {
        this.dateOfVerif = (dateOfVerif != null) ? dateOfVerif.getTime() : null;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public Boolean isServiceability() {
        return serviceability;
    }

    public void setServiceability(Boolean serviceability) {
        this.serviceability = serviceability;
    }

    public Long getNoWaterToDate() {
        return noWaterToDate;
    }

    public void setNoWaterToDate(Date noWaterToDate) {
        this.noWaterToDate = (noWaterToDate != null) ? noWaterToDate.getTime() : null;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }
}
