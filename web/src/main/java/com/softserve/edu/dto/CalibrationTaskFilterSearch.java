package com.softserve.edu.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CalibrationTaskFilterSearch {

    private String name;
    private String leaderFullName;
    private String leaderPhone;
    private String startDateToSearch;
    private String endDateToSearch;
}
