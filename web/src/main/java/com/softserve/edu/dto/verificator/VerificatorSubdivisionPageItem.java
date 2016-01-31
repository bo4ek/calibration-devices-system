package com.softserve.edu.dto.verificator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Project system-calibration-devices
 * Created by bo4ek on 29.01.2016.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificatorSubdivisionPageItem {

    private String subdivisionId;
    private String subdivisionName;
    private String subdivisionLeader;
    private String subdivisionLeaderEmail;
    private String subdivisionLeaderPhone;

    public VerificatorSubdivisionPageItem(String subdivisionId, String subdivisionName, String subdivisionLeader,
                                          String subdivisionLeaderEmail, String subdivisionLeaderPhone) {
        this.subdivisionId = subdivisionId;
        this.subdivisionName = subdivisionName;
        this.subdivisionLeader = subdivisionLeader;
        this.subdivisionLeaderEmail = subdivisionLeaderEmail;
        this.subdivisionLeaderPhone = subdivisionLeaderPhone;
    }
}
