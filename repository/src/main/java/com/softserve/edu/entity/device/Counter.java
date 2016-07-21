package com.softserve.edu.entity.device;

import com.softserve.edu.entity.verification.Verification;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * Counter entity. Introduces the real device - counter, which will be verify in the process of verification.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "COUNTER")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Counter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(name = "realiseYear")
    private String releaseYear;

    @Column(name = "dateOfDismantled")
    private Date dateOfDismantled;

    @Column(name = "dateOfMounted")
    private Date dateOfMounted;

    @Column(name = "numberCounter")
    private String numberCounter;

    private String stamp;

    private String accumulatedVolume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterTypeId")
    private CounterType counterType;

    @OneToOne(fetch = FetchType.LAZY)
    private Verification verification;

    public Counter(String releaseYear, Long dateOfDismantled, Long dateOfMounted, String numberCounter,
                   CounterType counterType, Verification verification) {
        this(releaseYear, dateOfDismantled, dateOfMounted, numberCounter, counterType);
        this.verification = verification;
    }

    public Counter(String releaseYear, Long dateOfDismantled, Long dateOfMounted, String numberCounter,
                   CounterType counterType) {
        this.dateOfDismantled = (dateOfDismantled != null) ? new Date(dateOfDismantled) : null;
        this.dateOfMounted = (dateOfMounted != null) ? new Date(dateOfMounted) : null;
        this.numberCounter = numberCounter;
        this.counterType = counterType;
        this.releaseYear = releaseYear;
    }

    public Counter(String releaseYear, Date dateOfDismantled, Date dateOfMounted, String numberCounter,
                   CounterType counterType) {
        this.dateOfDismantled = (dateOfDismantled != null) ? dateOfDismantled : null;
        this.dateOfMounted = (dateOfMounted != null) ? dateOfMounted : null;
        this.numberCounter = numberCounter;
        this.counterType = counterType;
        this.releaseYear = releaseYear;
    }


    public Counter(String releaseYear, Long dateOfDismantled, Long dateOfMounted, String numberCounter,
                   CounterType counterType, String accumulatedVolume) {
        this(releaseYear, dateOfDismantled, dateOfMounted, numberCounter, counterType);
        this.accumulatedVolume = accumulatedVolume;
    }

    public Counter(String releaseYear, Date dateOfDismantled, Date dateOfMounted, String numberCounter,
                   CounterType counterType, String accumulatedVolume) {
        this(releaseYear, dateOfDismantled, dateOfMounted, numberCounter, counterType);
        this.accumulatedVolume = accumulatedVolume;
    }

    public Counter(String releaseYear, String numberCounter, CounterType counterType, String stamp) {
        this.releaseYear = releaseYear;
        this.numberCounter = numberCounter;
        this.counterType = counterType;
        this.stamp = stamp;
    }

    public void setDateOfDismantledLong(Long dateOfDismantled) {
        this.dateOfDismantled = (dateOfDismantled != null) ? new Date(dateOfDismantled) : null;
    }

    public void setDateOfMountedLong(Long dateOfMounted) {
        this.dateOfMounted = (dateOfMounted != null) ? new Date(dateOfMounted) : null;
    }
}