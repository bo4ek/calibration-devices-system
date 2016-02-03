package com.softserve.edu.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrganizationDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private List<String> types;
    private List<String> counters;
    private Integer employeesCapacity;
    private Integer maxProcessTime;

    private String codeEDRPOU;
    private String subordination;
    private String certificateNumberAuthorization;
    private Long certificateDate;

    private String firstName;
    private String lastName;
    private String middleName;
    private String username;
    private String password;
    private String rePassword;

    private String region;
    private String locality;
    private String district;
    private String street;
    private String building;
    private String flat;

    private String regionRegistered;
    private String localityRegistered;
    private String districtRegistered;
    private String streetRegistered;
    private String buildingRegistered;
    private String flatRegistered;

    private List<Long> serviceAreas;

    public OrganizationDTO() {
    }

    public OrganizationDTO(Long id, String name, String email, String phone, List<String> types,
                           List<String> counters, Integer employeesCapacity, Integer maxProcessTime, String region,
                           String district, String locality, String street, String building, String flat) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.types = types;
        this.counters = counters;
        this.employeesCapacity = employeesCapacity;
        this.maxProcessTime = maxProcessTime;
        this.region = region;
        this.locality = locality;
        this.district = district;
        this.street = street;
        this.building = building;
        this.flat = flat;
    }

    public OrganizationDTO(Long id, String name) {
        this(id, name, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public OrganizationDTO(Long id, String name, String email, String phone, List<String> types,
                           List<String> counters, Integer employeesCapacity, Integer maxProcessTime, String region,
                           String district, String locality, String street, String building, String flat, String codeEDRPOU,
                           String subordination, String certificateNumberAuthorization, Date certificateDate, String regionRegistered,
                           String districtRegistered, String localityRegistered, String streetRegistered, String buildingRegistered,
                           String flatRegistered) {
        this(id, name, email, phone, types, counters, employeesCapacity, maxProcessTime, region, district, locality,
                street, building, flat);
        this.codeEDRPOU = codeEDRPOU;
        this.subordination = subordination;
        this.certificateNumberAuthorization = certificateNumberAuthorization;
        this.certificateDate = (certificateDate != null) ? certificateDate.getTime() : null;
        this.regionRegistered = regionRegistered;
        this.districtRegistered = districtRegistered;
        this.localityRegistered = localityRegistered;
        this.streetRegistered = streetRegistered;
        this.buildingRegistered = buildingRegistered;
        this.flatRegistered = flatRegistered;
    }
}