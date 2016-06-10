package com.softserve.edu.dto.calibrator;

import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.enumeration.verification.Status;
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
    private String nameProvider;
    private String fileName;
    private String stamp;
    private String testResult;
    private String rejectMessage;
    private String comment;
    private Status status;
    private String calibrator;

    public NotStandardVerificationDTO(String id, Date initialDate, Address address, String firstName, String lastName,
                                      String middleName, Counter counter, Set<CalibrationTest> tests,
                                      Organization providerFromBBI, Organization nameProvider, String rejectMessage, String comment,
                                      Status status) {
        this(id, initialDate, address, firstName, lastName, middleName);
        this.symbol = (counter != null && counter.getCounterType() != null) ? counter.getCounterType().getSymbol() : null;
        this.standardSize = (counter != null && counter.getCounterType() != null) ? counter.getCounterType().getStandardSize() : null;
        this.realiseYear = (counter != null) ? counter.getReleaseYear() : null;
        this.stamp = (counter != null) ? counter.getStamp() : null;
        this.rejectMessage = rejectMessage;
        this.comment = comment;
        this.providerFromBBI = (providerFromBBI != null) ? providerFromBBI.getName() : null;
        this.nameProvider = (nameProvider != null) ? nameProvider.getName() : null;
        // In case of Not Standard Verifications one verification has only one test
        this.fileName = (tests != null && tests.size() != 0) ? tests.iterator().next().getName() : null;
        this.testResult = (tests != null && tests.size() != 0) ? tests.iterator().next().getTestResult().toString() : null;
        this.status = status;
    }

    public NotStandardVerificationDTO(String id, Date initialDate, Address address,
                                      String firstName, String lastName, String middleName) {
        this.id = id;
        this.initialDate = initialDate;
        this.fullName = lastName + " " + firstName + " " + middleName;
        this.street = address.getStreet();
        this.district = address.getDistrict();
        this.locality = address.getLocality();
        this.flat = address.getFlat();
        this.building = address.getBuilding();
    }

    public NotStandardVerificationDTO(String id, Date initialDate, Address address,
                                      String firstName, String lastName, String middleName, String calibrator) {
        this(id, initialDate, address, firstName, lastName, middleName);
        this.calibrator = calibrator;
    }
}