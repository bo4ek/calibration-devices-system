package com.softserve.edu.dto.calibrator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DisassemblyTeamPageItem {

    private String id;
    private String name;
    private Date effectiveTo;
    private List<String> specialization;
    private String leaderFullName;
    private String leaderPhone;
    private String leaderEmail;

    public DisassemblyTeamPageItem(String id, String name, Date effectiveTo, List<String> specialization,
                                   String leaderFullName, String leaderPhone, String leaderEmail) {
        this.id = id;
        this.name = name;
        this.effectiveTo = effectiveTo;
        this.specialization = specialization;
        this.leaderFullName = leaderFullName;
        this.leaderPhone = leaderPhone;
        this.leaderEmail = leaderEmail;
    }
}