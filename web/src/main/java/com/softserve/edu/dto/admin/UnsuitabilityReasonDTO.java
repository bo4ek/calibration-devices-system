package com.softserve.edu.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnsuitabilityReasonDTO {
    private Long id;
    private String deviceName;
    private String name;
    private Long deviceId;

    public UnsuitabilityReasonDTO() {}

    public UnsuitabilityReasonDTO(Long id, String name, Long deviceId,String deviceName) {
        this.id = id;
        this.name = name;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    public UnsuitabilityReasonDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
