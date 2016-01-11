package com.softserve.edu.entity;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String region;
    private String district;
    private String locality;
    private String street;
    private String building;
    private String flat;

    public Address(String region, String district, String locality, String street, String building,
                   String flat) {
        this.region = region;
        this.district = district;
        this.locality = locality;
        this.street = street;
        this.building = building;
        this.flat = flat;
    }
    public Address(String district, String locality, String street, String building,
                   String flat) {
        this.district = district;
        this.locality = locality;
        this.street = street;
        this.building = building;
        this.flat = flat;
    }
    public String getAddress(){
        return (district + ", " +  street + ", " + building + ", " + flat);
    }

    public String getStreet(){
        return street;
    }

    public String getBuilding(){
        return building;
    }

    public String getFlat(){
        return flat;
    }
}