package com.softserve.edu.entity;

import lombok.*;

import javax.persistence.Column;
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

    @Column(length = 5)
    private String mailIndex;

    public Address(String region, String district, String locality, String street, String building,
                   String flat, String mailIndex) {
        this(region, district, locality, street, building, flat);
        this.mailIndex = mailIndex;
    }

    public Address(String region, String district, String locality, String street, String building, String flat) {
        this(district, locality, street, building, flat);
        this.region = region;
    }

    public Address(String district, String locality, String street, String building, String flat) {
        this.district = district;
        this.locality = locality;
        this.street = street;
        this.building = building;
        this.flat = flat;
    }

    public String getAddress(){
        return (district + ", " +  street + ", " + building + ", " + flat);
    }
}