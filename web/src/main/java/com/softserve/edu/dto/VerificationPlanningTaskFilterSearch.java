package com.softserve.edu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationPlanningTaskFilterSearch {
    private String id;
    private Long date;
    private Long endDate;
    private String client_full_name;
    private String provider;
    private String district;
    private String street;
    private String building;
    private String flat;
    private String dateOfVerif;
    private String time;
    private String serviceability;
    private String noWaterToDate;
    private String sealPresence;
    private String telephone;
    private String verificationWithDismantle;
}
