package com.softserve.edu.entity.verification;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.calibration.*;
import lombok.*;

import javax.persistence.*;
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
public class Verification implements Comparable {

    @Id
    private String id;

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
    @JoinColumn(name = "providerFromBBI")
    private Organization providerFromBBI;

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
    private Date signProtocolDate;

    @Temporal(TemporalType.DATE)
    private Date sentToCalibratorDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sentToVerificatorDate;

    private String rejectedMessage;
    private String comment;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "moduleId")
    private CalibrationModule calibrationModule;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "verification")
    private Set<BbiProtocol> bbiProtocols;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "taskId")
    private CalibrationTask task;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "groupId")
    private VerificationGroup group;

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

    @Column(columnDefinition = "boolean default false")
    private boolean isManual;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "counterId")
    private Counter counter;

    @Column
    private int queue;

    @Column
    private Integer processTimeExceeding;

    @ManyToOne
    @JoinColumn(name = "calibrationTestManualId")
    private CalibrationTestDataManual calibrationTestDataManualId;

    @Column(columnDefinition = "boolean default false")
    private boolean signed;

    @Column(columnDefinition = "boolean default false")
    private boolean parsed;

    @Lob
    @Column(length = 100000)
    private byte[] signedDocument;

    @Column(columnDefinition = "int default 0")
    private Integer calibrationInterval;

    @Column
    private String numberOfProtocol;

    @Column(name = "verificationTime")
    private String verificationTime;

    @Column
    private String signature;

    @Column(columnDefinition = "boolean default false")
    private boolean verificationWithDismantle;

    public Verification(String verificationId, int queue) {
        this.id = verificationId;
        this.queue = queue;
    }

    public Verification(
            Date initialDate, ClientData clientData, Organization provider,
            Device device, Status status, ReadStatus readStatus, AdditionalInfo info, boolean dismantled, Counter counter,
            String comment, boolean sealPresence, String verificationId) {

        this(initialDate, clientData, provider, device, status, readStatus, null, info, dismantled, counter,
                comment, sealPresence, verificationId);
    }

    public Verification(Date initialDate, ClientData clientData, Organization provider,
                        Device device, Status status, ReadStatus readStatus, Organization calibrator, AdditionalInfo info,
                        Boolean dismantled, Counter counter, String comment, boolean sealPresence, String verificationId) {

        this.id = verificationId;
        this.initialDate = initialDate;
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

    public Verification(Date initialDate, ClientData clientData, Organization provider,
                        Device device, Status status, ReadStatus readStatus, Organization calibrator, AdditionalInfo info,
                        Boolean dismantled, Counter counter, String comment, boolean sealPresence, String verificationId,
                        Date sentToCalibratorDate, Status taskStatus, User calibratorEmployee) {

        this.id = verificationId;
        this.initialDate = initialDate;
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
        this.sentToCalibratorDate = sentToCalibratorDate;
        this.taskStatus = taskStatus;
        this.calibratorEmployee = calibratorEmployee;
    }

    public Verification(Date initialDate, ClientData clientData, Organization provider,
                        Device device, Status status, ReadStatus readStatus, Organization calibrator, AdditionalInfo info,
                        Boolean dismantled, Counter counter, String comment, boolean sealPresence, String verificationId,
                        Date sentToCalibratorDate, Status taskStatus, User calibratorEmployee, Boolean verificationWithDismantle) {
        this(initialDate, clientData, provider, device, status, readStatus, calibrator, info, dismantled, counter, comment,
                sealPresence, verificationId, sentToCalibratorDate, taskStatus, calibratorEmployee);
        this.verificationWithDismantle = verificationWithDismantle;
    }

    public Verification(Date initialDate, ClientData clientData, Organization provider,
                        Device device, Status status, ReadStatus readStatus, Organization calibrator, AdditionalInfo info,
                        Boolean dismantled, Counter counter, String comment, boolean sealPresence, String verificationId, Date sentToCalibratorDate, Status taskStatus) {

        this.id = verificationId;
        this.initialDate = initialDate;
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
        this.sentToCalibratorDate = sentToCalibratorDate;
        this.taskStatus = taskStatus;
    }

    public Verification(Date initialDate, ClientData clientData, Organization provider,
                        Device device, Status status, ReadStatus readStatus, Organization calibrator, AdditionalInfo info,
                        Boolean dismantled, Counter counter, String comment, boolean sealPresence, String verificationId,
                        Date sentToCalibratorDate, Status taskStatus, Boolean verificationWithDismantle) {

        this(initialDate, clientData, provider, device, status, readStatus, calibrator, info, dismantled, counter,
                comment, sealPresence, verificationId, sentToCalibratorDate, taskStatus);
        this.verificationWithDismantle = verificationWithDismantle;
    }

    public Verification(Date initialDate, Date expirationDate, ClientData clientData, Organization provider,
                        Device device, Status status, ReadStatus readStatus, Organization calibrator,
                        String comment, String verificationId) {

        this.id = verificationId;
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

    public Verification(Date initialDate, Date expirationDate, ClientData clientData, Organization provider,
                        Device device, Status status, ReadStatus readStatus, AdditionalInfo info) {

        this(initialDate, expirationDate, clientData, provider, device, status, readStatus, null, null, null);
        this.info = info;
    }

    public Verification(Date initialDate, ClientData clientData, Status status, Organization calibrator, Organization providerFromBBI,
                        User calibratorEmployee, Counter counter, String verificationId, String comment, String verificationTime, Device device) {
        this(initialDate, clientData, status, calibrator, providerFromBBI, calibratorEmployee, counter, verificationId, comment, verificationTime);
        this.device = device;
    }

    public Verification(Date initialDate, ClientData clientData, Status status, Organization calibrator, Organization providerFromBBI,
                        User calibratorEmployee, Counter counter, String verificationId, String comment, String verificationTime) {

        this.id = verificationId;
        this.initialDate = initialDate;
        this.expirationDate = null;
        this.signProtocolDate = null;
        this.sentToCalibratorDate = initialDate;
        this.clientData = clientData;
        this.status = status;
        this.calibrator = calibrator;
        this.providerFromBBI = providerFromBBI;
        this.calibratorEmployee = calibratorEmployee;
        this.counter = counter;
        this.device = counter.getCounterType() == null ? null : counter.getCounterType().getDevice();
        this.readStatus = ReadStatus.UNREAD;
        this.counterStatus = false;
        this.comment = comment;
        this.verificationTime = verificationTime;
    }

    public Verification(Date initialDate, ClientData clientData, Status status, Organization calibrator,
                        User calibratorEmployee, Counter counter, String verificationId, String comment) {

        this.id = verificationId;
        this.initialDate = initialDate;
        this.expirationDate = null;
        this.signProtocolDate = null;
        this.sentToCalibratorDate = initialDate;
        this.clientData = clientData;
        this.status = status;
        this.calibrator = calibrator;
        this.calibratorEmployee = calibratorEmployee;
        this.counter = counter;
        this.device = counter.getCounterType().getDevice();
        this.readStatus = ReadStatus.UNREAD;
        this.counterStatus = false;
        this.comment = comment;
    }

    public void deleteCalibrationTest(CalibrationTest calibrationTest) {
        calibrationTests.remove(calibrationTest);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int compareTo(Object o) {
        int compareAge = ((Verification) o).getQueue();
        return this.queue - compareAge;
    }

    public enum ReadStatus {
        READ,
        UNREAD
    }

    public enum CalibrationTestResult {
        SUCCESS,
        FAILED,
        NOT_PROCESSED
    }

    public enum ConsumptionStatus {
        IN_THE_AREA,
        NOT_IN_THE_AREA
    }
}
