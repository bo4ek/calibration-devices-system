package com.softserve.edu.dto;

import lombok.ToString;

@ToString
public class LocalityDTO {

    private Long id;
    private String designation;
    private Long districtId;

    public LocalityDTO(Long id, String designation, Long districtId) {
        this.id = id;
        this.designation = designation;
        this.districtId = districtId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
