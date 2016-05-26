package com.softserve.edu.dto;

public class DeviceLightDTO {
    private Long id;
    private String designation;

    private String deviceType;

    protected DeviceLightDTO() {
    }

    public DeviceLightDTO(Long id, String name, String deviceType) {
        this.id = id;
        this.designation = name;
        this.deviceType = deviceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
