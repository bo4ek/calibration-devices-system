package com.softserve.edu.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewStreetDTO {

    private Long streetId;
    private Long localityId;
    private String streetName;

    public NewStreetDTO() {
    }

    public NewStreetDTO(Long streetId, Long localityId, String streetName) {
        this.streetId = streetId;
        this.localityId = localityId;
        this.streetName = streetName;
    }
}
