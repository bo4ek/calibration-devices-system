package com.softserve.edu.entity.verification;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.softserve.edu.common.Constants;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import lombok.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Verification entity. Contains data about whole business process of
 * verification.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "VERIFICATION")
public class Verification {

    @Id
    @Setter(AccessLevel.PRIVATE)
    private String id;

    @Override
    public String toString() {
        return "Verification{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", readStatus=" + readStatus +
                ", taskStatus=" + taskStatus +
                '}';
    }

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus;

    @Enumerated(EnumType.STRING)
    private Status taskStatus;

    @ManyToOne
    @JoinColumn(name = "deviceId")
    @JsonManagedReference
    private Device device;

    @OneToMany(mappedBy = "verification")
    private Set<CalibrationTest> calibrationTests;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "providerId")
    private Organization provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "providerEmployeeUsername")
    private User providerEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calibratorId")
    private Organization calibrator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calibratorEmployeeUsername")
    private User calibratorEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stateVerificatorId")
    private Organization stateVerificator;

    @ManyToOne
    @JoinColumn(name = "stateVerificatorEmployeeUsername")
    private User stateVerificatorEmployee;

    @Embedded
    private ClientData clientData;

    @Temporal(TemporalType.DATE)
    private Date initialDate;

    @Temporal(TemporalType.DATE)
    private Date expirationDate;

    @Temporal(TemporalType.DATE)
    private Date sentToCalibratorDate;

    private String rejectedMessage;
    private String comment;
    private Integer installmentNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "verification")
    private Set<BbiProtocol> bbiProtocols;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "taskId")
    private CalibrationTask task;

    @Deprecated
    @Column(columnDefinition = "boolean default false")
    private boolean isAddInfoExists;

    @Column(columnDefinition = "boolean default false")
    private boolean counterStatus;

    @Column(columnDefinition = "boolean default true")
    private boolean sealPresence;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "infoId")
    private AdditionalInfo info;

    @Column(columnDefinition = "bit(1) default 0")
    private Boolean isManual;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "counterId")
    private Counter counter;

    private Integer processTimeExceeding;

    public Verification(
            Date initialDate, Date expirationDate, ClientData clientData, Organization provider,
            Device device, Status status, ReadStatus readStatus, AdditionalInfo info, boolean dismantled, Counter counter,
            String comment, boolean sealPresence, String verificationId
    ) {
        this(initialDate, expirationDate, clientData, provider, device, status, readStatus, null, info, dismantled, counter,
                comment, sealPresence, verificationId);
    }

    public Verification(Date initialDate, Date expirationDate, ClientData clientData, Organization provider,
                        Device device, Status status, ReadStatus readStatus, Organization calibrator, AdditionalInfo info,
                        Boolean dismantled, Counter counter, String comment, boolean sealPresence, String verificationId
    ) {

        this.id = (new SimpleDateFormat(Constants.DAY_MONTH_YEAR).format(initialDate)).toString()
                + device.getDeviceType().getId() + verificationId;
        this.initialDate = initialDate;
        this.expirationDate = expirationDate;
        this.clientData = clientData;
        this.provider = provider;
        this.device = device;
        this.status = status;
        this.readStatus = readStatus;
        this.calibrator = calibrator;
        this.info = info;
        this.counterStatus = dismantled;
        this.counter = counter;
        if (this.comment == null) {
            this.comment = "";
        }
        this.comment = (comment != null) ? this.comment + comment : this.comment + "";
        this.sealPresence = sealPresence;
    }

    public Verification(Date initialDate, Date expirationDate, ClientData clientData, Organization provider,
                        Device device, Status status, ReadStatus readStatus, Organization calibrator,
                        String comment, String verificationId) {

        this.id = (new SimpleDateFormat(Constants.DAY_MONTH_YEAR).format(initialDate)).toString()
                + device.getDeviceType().getId() + verificationId;
        this.initialDate = initialDate;
        this.expirationDate = expirationDate;
        this.clientData = clientData;
        this.provider = provider;
        this.device = device;
        this.status = status;
        this.readStatus = readStatus;
        this.calibrator = calibrator;
        this.comment = comment;
    }

    public Verification(Date initialDate, ClientData clientData, Status status, Organization calibrator,
                        User calibratorEmployee, Counter counter, String verificationId) {

        this.id = (new SimpleDateFormat(Constants.DAY_MONTH_YEAR).format(initialDate)).toString()
                + counter.getCounterType().getDevice().getDeviceType().getId() + verificationId;
        this.initialDate = initialDate;
        this.expirationDate = initialDate;
        this.sentToCalibratorDate = initialDate;
        this.clientData = clientData;
        this.status = status;
        this.calibrator = calibrator;
        this.calibratorEmployee = calibratorEmployee;
        this.counter = counter;
        this.device = counter.getCounterType().getDevice();
        this.readStatus = ReadStatus.UNREAD;
        this.counterStatus = false;
    }

    public void deleteCalibrationTest(CalibrationTest calibrationTest) {
        calibrationTests.remove(calibrationTest);

    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public enum ReadStatus {
        READ,
        UNREAD
    }

    public enum CalibrationTestResult {
        SUCCESS,
        FAILED,
        RAW
    }

    public enum ConsumptionStatus {
        IN_THE_AREA,
        NOT_IN_THE_AREA
    }
}
