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

    @ManyToOne
    @JoinColumn(name = "counterTypeId")
    private CounterType counterType;

    @OneToOne
    private Verification verification;

    public Counter(String releaseYear, Long dateOfDismantled, Long dateOfMounted, String numberCounter,
                   CounterType counterType, Verification verification) {
        this.releaseYear = releaseYear;
        this.dateOfDismantled = (dateOfDismantled != null) ? new Date(dateOfDismantled) : null;
        this.dateOfMounted = (dateOfMounted != null) ? new Date(dateOfMounted) : null;
        this.numberCounter = numberCounter;
        this.counterType = counterType;
        this.verification = verification;
    }

    public Counter(String releaseYear, Long dateOfDismantled, Long dateOfMounted, String numberCounter,
                   CounterType counterType) {
        this.releaseYear = releaseYear;
        this.dateOfDismantled = (dateOfDismantled != null) ? new Date(dateOfDismantled) : null;
        this.dateOfMounted = (dateOfMounted != null) ? new Date(dateOfMounted) : null;
        this.numberCounter = numberCounter;
        this.counterType = counterType;
    }

    public Counter(String releaseYear, String numberCounter, CounterType counterType, String stamp) {
        this.releaseYear = releaseYear;
        this.numberCounter = numberCounter;
        this.counterType = counterType;
        this.stamp = stamp;
    }

    public void setDateOfDismantled(Long dateOfDismantled) {
        this.dateOfDismantled = (dateOfDismantled != null) ? new Date(dateOfDismantled) : null;
    }

    public void setDateOfMounted(Long dateOfMounted) {
        this.dateOfMounted = (dateOfMounted != null) ? new Date(dateOfMounted) : null;
    }
}
