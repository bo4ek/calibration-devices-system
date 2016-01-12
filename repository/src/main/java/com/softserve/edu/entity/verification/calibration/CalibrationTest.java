package com.softserve.edu.entity.verification.calibration;

import com.softserve.edu.entity.verification.Verification;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Calibration Test entity.
 * Contains data about measurement device calibration test.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "CALIBRATION_TEST")
@NoArgsConstructor
public class CalibrationTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTest;
    private String capacity;
    private Double latitude;
    private Double longitude;
    private Integer temperature;
    @Enumerated(EnumType.STRING)
    private Verification.ConsumptionStatus consumptionStatus;

    @Enumerated(EnumType.STRING)
    private Verification.CalibrationTestResult testResult;

    private String photoPath;

    @Column(columnDefinition = "boolean default false")
    private boolean signed;

    @Column
    private byte[] signedDocument;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name",
                    column = @Column(name = "documentName")),
            @AttributeOverride(name = "sign",
                    column = @Column(name = "documentSign"))
    })
    private MeteorologicalDocument meteorologicalDocument;

    @ManyToOne
    @JoinColumn(name = "verificationId")
    private Verification verification;

    @OneToMany(mappedBy = "calibrationTest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CalibrationTestData> calibrationTestDataList;

    public CalibrationTest(String name, Double latitude,
                           Double longitude, Long unixTime,
                           Verification verification, String capacity, Integer temperature) {
        this.name = name;
        this.dateTest = new Date(unixTime);
        this.capacity = capacity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.consumptionStatus = Verification.ConsumptionStatus.IN_THE_AREA;
        this.testResult = Verification.CalibrationTestResult.SUCCESS;
        this.verification = verification;
        this.temperature = temperature;
    }
}
