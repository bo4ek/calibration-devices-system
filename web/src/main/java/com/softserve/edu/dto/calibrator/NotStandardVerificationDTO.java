package com.softserve.edu.dto.calibrator;

import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class NotStandardVerificationDTO {

    private String id;
    private Date initialDate;
    private String fullName;
    private String district;
    private String region;
    private String locality;
    private String street;
    private String building;
    private String flat;
    private Verification.ReadStatus readStatus;
    private Long calibrationTestId;
    private String symbol;
    private String standardSize;
    private String realiseYear;
    private Boolean dismantled;
    private String numberCounter;
    private Long counterId;
    private String providerFromBBI;
    private String fileName;
    private String stamp;
    private String testResult;
    private String rejectMessage;
    private String comment;

    public NotStandardVerificationDTO(String id, Date initialDate, Address address,
                                      String firstName, String lastName, String middleName,
                                      Counter counter, Set<CalibrationTest> tests, Organization providerFromBBI, String rejectMessage, String comment) {
        this.id = id;
        this.initialDate = initialDate;
        this.fullName = lastName + " " + firstName + " " + middleName;
        this.street = address.getStreet();
        this.district = address.getDistrict();
        this.locality = address.getLocality();
        this.building = address.getBuilding();
        this.flat = address.getFlat();
        this.symbol = counter.getCounterType().getSymbol();
        this.standardSize = counter.getCounterType().getStandardSize();
        this.realiseYear = counter.getReleaseYear();
        this.stamp = counter.getStamp();
        this.rejectMessage = rejectMessage;
        this.comment = comment;
        this.providerFromBBI = providerFromBBI.getName();
        // In case of Not Standard Verifications one verification has only one test
        this.fileName = tests.iterator().next().getName();
        this.testResult = tests.iterator().next().getTestResult().toString();
    }

    public NotStandardVerificationDTO(String id, Date initialDate, Address address,
                                      String firstName, String lastName, String middleName) {
        this.id = id;
        this.initialDate = initialDate;
        this.fullName = lastName + " " + firstName + " " + middleName;
        this.street = address.getStreet();
        this.district = address.getDistrict();
        this.locality = address.getLocality();

    }
}
