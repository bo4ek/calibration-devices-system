package com.softserve.edu.entity.verification.calibration;

import com.softserve.edu.entity.verification.Verification;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Date;


@Entity
@Table(name = "ADDITIONAL_INFO")
@Getter

@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class AdditionalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int entrance;
    private int doorCode;
    private int floor;

    @Temporal(TemporalType.DATE)
    private Date dateOfVerif;

    private LocalTime timeFrom;
    private LocalTime timeTo;

    private boolean serviceability;

    @Temporal(TemporalType.DATE)
    private Date noWaterToDate;

    private String notes;

    @OneToOne
    @JoinColumn(name = "verification_id")
    private Verification verification;

    public AdditionalInfo(int entrance, int doorCode, int floor, Date dateOfVerif, LocalTime timeFrom,
                          LocalTime timeTo, Boolean serviceability, Date noWaterToDate, String notes, Verification verification) {
        this.entrance = entrance;
        this.doorCode = doorCode;
        this.floor = floor;
        this.dateOfVerif = dateOfVerif;
        this.serviceability = serviceability == null ? false : serviceability;
        this.noWaterToDate = noWaterToDate;
        this.notes = notes;
        this.verification = verification;
        setTimeIfDateOfVerifIsnotNullWithouParsing(timeFrom, timeTo);
    }

    public AdditionalInfo(String entrance, String doorCode, String floor, Date dateOfVerif, Boolean serviceability,
                          Date noWaterToDate, String notes, String timeFrom, String timeTo) {

        this.entrance = (entrance != null && !entrance.equals("")) ? Integer.parseInt(entrance) : 0;
        this.doorCode = (doorCode != null && !doorCode.equals("")) ? Integer.parseInt(doorCode) : 0;
        this.floor = (floor != null && !floor.equals("")) ? Integer.parseInt(floor) : 0;
        this.dateOfVerif = (dateOfVerif != null) ? dateOfVerif : null;
        this.serviceability = serviceability == null ? false : serviceability;
        this.noWaterToDate = (noWaterToDate != null) ? noWaterToDate : null;
        this.notes = notes;
        setTimeIfDateOfVerifIsnotNull(dateOfVerif, timeFrom, timeTo);
    }

    public AdditionalInfo(String entrance, String doorCode, String floor, Long dateOfVerif, Boolean serviceability,
                          Long noWaterToDate, String notes, String timeFrom, String timeTo) {

        this.entrance = (entrance != null && !entrance.equals("")) ? Integer.parseInt(entrance) : 0;
        this.doorCode = (doorCode != null && !doorCode.equals("")) ? Integer.parseInt(doorCode) : 0;
        this.floor = (floor != null && !floor.equals("")) ? Integer.parseInt(floor) : 0;
        this.dateOfVerif = (dateOfVerif != null) ? new Date(dateOfVerif) : null;
        this.serviceability = serviceability == null ? false : serviceability;
        this.noWaterToDate = (noWaterToDate != null) ? new Date(noWaterToDate) : null;
        this.notes = notes;
        setTimeIfDateOfVerifIsnotNull(dateOfVerif, timeFrom, timeTo);
    }

    public AdditionalInfo(int entrance, int doorCode, int floor, Long dateOfVerif, Boolean serviceability,
                          Long noWaterToDate, String notes, String timeFrom, String timeTo) {
        this.entrance = entrance;
        this.doorCode = doorCode;
        this.floor = floor;
        this.dateOfVerif = (dateOfVerif != null) ? new Date(dateOfVerif) : null;
        this.serviceability = serviceability == null ? false : serviceability;
        this.noWaterToDate = (noWaterToDate != null) ? new Date(noWaterToDate) : null;
        this.notes = notes;

    }

    public AdditionalInfo(int entrance, int doorCode, int floor, Date dateOfVerif, Boolean serviceability,
                          Date noWaterToDate, String notes, String timeFrom, String timeTo) {
        this.entrance = entrance;
        this.doorCode = doorCode;
        this.floor = floor;
        this.dateOfVerif = (dateOfVerif != null) ? dateOfVerif : null;
        this.serviceability = serviceability == null ? false : serviceability;
        this.noWaterToDate = (noWaterToDate != null) ? noWaterToDate : null;
        this.notes = notes;

    }

    public Boolean getServiceability() {
        return this.serviceability;
    }

    public boolean isServiceability() {
        return serviceability;
    }

    public void setEntrance(String entrance) {
        this.entrance = (entrance != null && !entrance.equals("")) ? Integer.parseInt(entrance) : 0;
    }

    public void setEntrance(int entrance) {
        this.entrance = entrance;
    }

    public void setDoorCode(String doorCode) {
        this.doorCode = (doorCode != null && !doorCode.equals("")) ? Integer.parseInt(doorCode) : 0;
    }

    public void setDoorCode(int doorCode) {
        this.doorCode = doorCode;
    }

    public void setFloor(String floor) {
        this.floor = (floor != null && !floor.equals("")) ? Integer.parseInt(floor) : 0;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setDateOfVerifLong(Long dateOfVerif) {
        this.dateOfVerif = (dateOfVerif != null) ? new Date(dateOfVerif) : null;
    }

    public void setDateOfVerif(Date dateOfVerif) {
        this.dateOfVerif = (dateOfVerif != null) ? dateOfVerif : null;
    }

    public void setNoWaterToDateLong(Long noWaterToDate) {
        this.noWaterToDate = (noWaterToDate != null) ? new Date(noWaterToDate) : null;
    }

    public void setNoWaterToDate(Date noWaterToDate) {
        this.noWaterToDate = (noWaterToDate != null) ? noWaterToDate : null;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = (timeFrom != null) ? LocalTime.parse(timeFrom) : null;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = (timeTo != null) ? LocalTime.parse(timeTo) : null;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setServiceability(Boolean serviceability) {
        if (serviceability != null) {
            this.serviceability = serviceability;
        }
    }

    private void setTimeIfDateOfVerifIsnotNull(Object dateOfVerif, String timeFrom, String timeTo) {
        if (dateOfVerif != null && timeFrom != null && timeTo != null) {
            this.timeFrom = LocalTime.parse(timeFrom);
            this.timeTo = LocalTime.parse(timeTo);
        }
    }

    private void setTimeIfDateOfVerifIsnotNullWithouParsing(LocalTime timeFrom, LocalTime timeTo) {
        if (this.dateOfVerif != null) {
            this.timeFrom = timeFrom;
            this.timeTo = timeTo;
        }
    }
}
