package com.softserve.edu.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StreetDTO {

    private Long id;
    private String region;
    private String district;
    private String city;
    private String streetName;

    public StreetDTO() {
    }

    public StreetDTO(Long id, String region, String district, String city, String streetName) {
        this.id = id;
        this.region = region;
        this.district = district;
        this.city = city;
        this.streetName = streetName;
    }


}
