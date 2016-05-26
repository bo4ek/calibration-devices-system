package com.softserve.edu.service.utils;

public class ExcelFileDTO {

    private String verificationId;

    private String surname;

    private String name;

    private String middelName;

    private String adress;

    private String telephone;

    private int entrance;

    private int doorCode;

    private int floor;

    public ExcelFileDTO(String verificationId, String surname, String name,
                        String middelName, String adress, String telephone, int entrance,
                        int dooCode, int floor) {
        this.verificationId = verificationId;
        this.surname = surname;
        this.name = name;
        this.middelName = middelName;
        this.adress = adress;
        this.telephone = telephone;
        this.entrance = entrance;
        this.doorCode = dooCode;
        this.floor = floor;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiddelName() {
        return middelName;
    }

    public void setMiddelName(String middelName) {
        this.middelName = middelName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public void setDoorCode(int dooCode) {
        this.doorCode = dooCode;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
