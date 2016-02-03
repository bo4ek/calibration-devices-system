package com.softserve.edu.dto.verificator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Project system-calibration-devices
 * Created by bo4ek on 27.01.2016.
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class VerificatorSubdivisionDTO {

    private String subdivisionId;
    private String subdivisionName;
    private String subdivisionLeader;
    private String subdivisionLeaderEmail;
    private String subdivisionLeaderPhone;

    public VerificatorSubdivisionDTO(String subdivisionId, String subdivisionName, String subdivisionLeader, String subdivisionLeaderEmail, String subdivisionLeaderPhone) {
        this.subdivisionId = subdivisionId;
        this.subdivisionName = subdivisionName;
        this.subdivisionLeader = subdivisionLeader;
        this.subdivisionLeaderEmail = subdivisionLeaderEmail;
        this.subdivisionLeaderPhone = subdivisionLeaderPhone;
    }

    public VerificatorSubdivisionDTO(String subdivisionId) {
        this.subdivisionId = subdivisionId;
    }
}