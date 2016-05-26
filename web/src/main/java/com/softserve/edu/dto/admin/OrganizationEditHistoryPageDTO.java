package com.softserve.edu.dto.admin;

import java.util.Date;
import java.util.List;

public class OrganizationEditHistoryPageDTO {
    private String date;
    private String name;
    private String email;
    private String phone;
    private String types;
    private Integer employeesCapacity;
    private Integer maxProcessTime;

    private String region;
    private String locality;
    private String district;
    private String street;
    private String building;
    private String flat;

    private String username;
    private String firstName;
    private String lastName;
    private String middleName;

    private String adminUsername;

    public OrganizationEditHistoryPageDTO(String date ,String name, String email, String phone, String types, Integer employeesCapacity, Integer maxProcessTime, String region, String district, String locality, String street, String building, String flat, String username, String firstName, String lastName, String middleName, String adminUsername) {
        this.date = date;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.types = types;
        this.employeesCapacity = employeesCapacity;
        this.maxProcessTime = maxProcessTime;
        this.region = region;
        this.locality = locality;
        this.district = district;
        this.street = street;
        this.building = building;
        this.flat = flat;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.adminUsername = adminUsername;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public Integer getEmployeesCapacity() {
        return employeesCapacity;
    }

    public void setEmployeesCapacity(Integer employeesCapacity) {
        this.employeesCapacity = employeesCapacity;
    }

    public Integer getMaxProcessTime() {
        return maxProcessTime;
    }

    public void setMaxProcessTime(Integer maxProcessTime) {
        this.maxProcessTime = maxProcessTime;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
}
