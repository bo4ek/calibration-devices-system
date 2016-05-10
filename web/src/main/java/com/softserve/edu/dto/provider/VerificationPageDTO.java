package com.softserve.edu.dto.provider;




import com.softserve.edu.documents.resources.DocumentType;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.service.utils.ArchivalVerificationsQueryConstructorProvider;
import lombok.*;
import org.apache.log4j.Logger;

import java.util.Date;

@Setter
@Getter
@ToString
public class VerificationPageDTO {
    static Logger logger = Logger.getLogger(ArchivalVerificationsQueryConstructorProvider.class);
    private String id;
    private Date initialDate;
    private String surname; //TODO: surname and name not needed anymore
    private String name;
    private String fullName;
    private String district;
    private String locality;
    private String phone;
    private String street;
    private String region;
    private Status status;
    private String providerEmployee;
    private String calibratorEmployee;
    private String stateVerificatorEmployee;
    private Long countOfWork;
    private Verification.ReadStatus readStatus;
    private boolean isUpload;
    private Integer processTimeExceeding;
    private Long protocolId;
    private String protocolDate;
    private String protocolStatus;
    private String measurementDeviceId;
    private String measurementDeviceType;
    private DocumentType documentType;
    private String documentTypeName;
    private String documentDate;
    private Long calibrationTestId;
    private String address;
    private String nameProvider;
    private String nameCalibrator;
    private String building;
    private String flat;
    private String symbol;
    private String standardSize;
    private String realiseYear;
    private Boolean dismantled;
    private String numberCounter;
    private Long counterId;
    private boolean isManual;
    private String comment;

    public VerificationPageDTO() {
    }

    public VerificationPageDTO(String id, Date initialDate, String surname, String street, String region,
                               Status status, Verification.ReadStatus readStatus, User providerEmployee,
                               User calibratorEmployee, User stateVerificatorEmployee, String name, String fullName,
                               String district, String locality, String phone, boolean isUpload,
                               Integer processTimeExceeding, CalibrationTest calibrationTest, Device device,
                               String documentType, String documentDate, String comment) {
        this.id = id;
        this.initialDate = initialDate;
        this.surname = surname;
        this.name = name;
        this.street = street;
        this.region = region;
        this.status = status;
        this.readStatus = readStatus;
        if (providerEmployee != null) {
            if (providerEmployee.getMiddleName() != null) {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName() + " " + providerEmployee.getMiddleName();
            } else {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName();
            }
        }
        if (calibratorEmployee != null) {
            if (calibratorEmployee.getMiddleName() != null) {
                this.calibratorEmployee = calibratorEmployee.getLastName() + " " + calibratorEmployee.getFirstName() + " " + calibratorEmployee.getMiddleName();
            } else {
                this.calibratorEmployee = calibratorEmployee.getLastName() + " " + calibratorEmployee.getFirstName();
            }
        }
        if (stateVerificatorEmployee != null) {
            if (stateVerificatorEmployee.getMiddleName() != null) {
                this.stateVerificatorEmployee = stateVerificatorEmployee.getLastName() + " " + stateVerificatorEmployee.getFirstName() + " " + stateVerificatorEmployee.getMiddleName();
            } else {
                this.stateVerificatorEmployee = stateVerificatorEmployee.getLastName() + " " + stateVerificatorEmployee.getFirstName();
            }
        }
        this.fullName = fullName;
        this.district = district;
        this.locality = locality;
        this.phone = phone;
        this.isUpload = isUpload;
        this.processTimeExceeding = processTimeExceeding;
        if (calibrationTest != null) {
            if (calibrationTest.getId() != null) {
                this.protocolId = calibrationTest.getId();
            }
            if (calibrationTest.getDateTest() != null) {
                this.protocolDate = calibrationTest.getDateTest().toString();
            }
            if (calibrationTest.getTestResult() != null) {
                this.protocolStatus = calibrationTest.getTestResult().toString();
                logger.debug("protocolStatus = " + protocolStatus);
                if (protocolStatus == Verification.CalibrationTestResult.SUCCESS.toString()) {
                    logger.debug("documentType = " + this.documentType);
                    this.documentType = DocumentType.VERIFICATION_CERTIFICATE;
                    this.documentTypeName = "СПП";
                } else {
                    this.documentType = DocumentType.UNFITNESS_CERTIFICATE;
                    this.documentTypeName = "Довідка про непридатність";
                }
                logger.debug("documentType = " + this.documentType);
            }
        }
        if (device != null) {
            if (device.getId() != null) {
                this.measurementDeviceId = device.getId().toString();
            }
            if (device.getDeviceType() != null) {
                this.measurementDeviceType = device.getDeviceType().toString();
            }
        }
        if (documentDate != null) {
            this.documentDate = documentDate;
        }
        this.comment = comment;
    }

    public VerificationPageDTO(String id, Date initialDate, String surname, String street, String region,
                               Status status, Verification.ReadStatus readStatus, User providerEmployee,
                               User calibratorEmployee, User stateVerificatorEmployee, String name, String fullName,
                               String district, String locality, String phone, boolean isUpload,
                               Integer processTimeExceeding, CalibrationTest calibrationTest, Long calibrationTestId,
                               Device device, String documentType, String documentDate) {
        this.id = id;
        this.initialDate = initialDate;
        this.surname = surname;
        this.name = name;
        this.street = street;
        this.region = region;
        this.status = status;
        this.readStatus = readStatus;
        if (providerEmployee != null) {
            if (providerEmployee.getMiddleName() != null) {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName() + " " + providerEmployee.getMiddleName();
            } else {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName();
            }
        }
        if (calibratorEmployee != null) {
            if (calibratorEmployee.getMiddleName() != null) {
                this.calibratorEmployee = calibratorEmployee.getLastName() + " " + calibratorEmployee.getFirstName() + " " + calibratorEmployee.getMiddleName();
            } else {
                this.calibratorEmployee = calibratorEmployee.getLastName() + " " + calibratorEmployee.getFirstName();
            }
        }
        if (stateVerificatorEmployee != null) {
            if (stateVerificatorEmployee.getMiddleName() != null) {
                this.stateVerificatorEmployee = stateVerificatorEmployee.getLastName() + " " + stateVerificatorEmployee.getFirstName() + " " + stateVerificatorEmployee.getMiddleName();
            } else {
                this.stateVerificatorEmployee = stateVerificatorEmployee.getLastName() + " " + stateVerificatorEmployee.getFirstName();
            }
        }
        this.fullName = fullName;
        this.district = district;
        this.locality = locality;
        this.phone = phone;
        this.isUpload = isUpload;
        this.processTimeExceeding = processTimeExceeding;
        if (calibrationTest != null) {
            if (calibrationTest.getId() != null) {
                this.protocolId = calibrationTest.getId();
            }
            if (calibrationTest.getDateTest() != null) {
                this.protocolDate = calibrationTest.getDateTest().toString();
            }
            if (calibrationTest.getTestResult() != null) {
                this.protocolStatus = calibrationTest.getTestResult().toString();
                logger.debug("protocolStatus = " + protocolStatus);
                if (protocolStatus == Verification.CalibrationTestResult.SUCCESS.toString()) {
                    logger.debug("documentType = " + this.documentType);
                    this.documentType = DocumentType.VERIFICATION_CERTIFICATE;
                    this.documentTypeName = "СПП";
                } else {
                    this.documentType = DocumentType.UNFITNESS_CERTIFICATE;
                    this.documentTypeName = "Довідка про непридатність";
                }
                logger.debug("documentType = " + this.documentType);
            }
        }
        if (device != null) {
            if (device.getId() != null) {
                this.measurementDeviceId = device.getId().toString();
            }
            if (device.getDeviceType() != null) {
                this.measurementDeviceType = device.getDeviceType().toString();
            }
        }
        if (documentDate != null) {
            this.documentDate = documentDate;
        }
        this.calibrationTestId = calibrationTestId;
    }

    public VerificationPageDTO(Long count) {
        this.countOfWork = count;
    }

    public VerificationPageDTO(String id, Date initialDate, String surname, String street, String region,
                               Status status, Verification.ReadStatus readStatus, User providerEmployee,
                               User calibratorEmployee, User stateVerificatorEmployee,
                               String name, String fullName, String district, String locality, String phone,
                               boolean isUpload, Integer processTimeExceeding,
                               CalibrationTest calibrationTest,
                               Device device,
                               String documentType, String documentDate, String address, String building, String flat, String comment) {
        this.id = id;
        this.initialDate = initialDate;
        this.surname = surname;
        this.name = name;
        this.street = street;
        this.region = region;
        this.status = status;
        this.readStatus = readStatus;
        this.address = address;
        this.building = building;
        this.flat = flat;
        if (providerEmployee != null) {
            if (providerEmployee.getMiddleName() != null) {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName() + " " + providerEmployee.getMiddleName();
            } else {
                this.providerEmployee = providerEmployee.getLastName() + " " + providerEmployee.getFirstName();
            }
        }
        if (calibratorEmployee != null) {
            if (calibratorEmployee.getMiddleName() != null) {
                this.calibratorEmployee = calibratorEmployee.getLastName() + " " + calibratorEmployee.getFirstName() + " " + calibratorEmployee.getMiddleName();
            } else {
                this.calibratorEmployee = calibratorEmployee.getLastName() + " " + calibratorEmployee.getFirstName();
            }
        }
        if (stateVerificatorEmployee != null) {
            if (stateVerificatorEmployee.getMiddleName() != null) {
                this.stateVerificatorEmployee = stateVerificatorEmployee.getLastName() + " " + stateVerificatorEmployee.getFirstName() + " " + stateVerificatorEmployee.getMiddleName();
            } else {
                this.stateVerificatorEmployee = stateVerificatorEmployee.getLastName() + " " + stateVerificatorEmployee.getFirstName();
            }
        }
        this.fullName = fullName;
        this.district = district;
        this.locality = locality;
        this.phone = phone;
        this.isUpload = isUpload;
        this.processTimeExceeding = processTimeExceeding;
        if (calibrationTest != null) {
            if (calibrationTest.getId() != null) {
                this.protocolId = calibrationTest.getId();
            }
            if (calibrationTest.getDateTest() != null) {
                this.protocolDate = calibrationTest.getDateTest().toString();
            }
            if (calibrationTest.getTestResult() != null) {
                this.protocolStatus = calibrationTest.getTestResult().toString();
                logger.debug("protocolStatus = " + protocolStatus);
                if (protocolStatus == Verification.CalibrationTestResult.SUCCESS.toString()) {
                    logger.debug("documentType = " + this.documentType);
                    this.documentType = DocumentType.VERIFICATION_CERTIFICATE;
                    this.documentTypeName = "СПП";
                } else {
                    this.documentType = DocumentType.UNFITNESS_CERTIFICATE;
                    this.documentTypeName = "Довідка про непридатність";
                }
                logger.debug("documentType = " + this.documentType);
            }
        }
        if (device != null) {
            if (device.getId() != null) {
                this.measurementDeviceId = device.getId().toString();
            }
            if (device.getDeviceType() != null) {
                this.measurementDeviceType = device.getDeviceType().toString();
            }
        }
        if (documentDate != null) {
            this.documentDate = documentDate;
        }
        this.comment = comment;
    }

    public void setIsManual(boolean isManual) {
        this.isManual = isManual;
    }
}