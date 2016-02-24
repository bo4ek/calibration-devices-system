package com.softserve.edu.entity.verification.calibration;

import com.softserve.edu.entity.device.Counter;

import javax.persistence.*;

import com.softserve.edu.entity.device.UnsuitabilityReason;
import com.softserve.edu.entity.verification.Verification;
import lombok.*;
import com.softserve.edu.entity.verification.Verification.CalibrationTestResult;

/**
 * Created by Misha on 12/11/2015.
 * Calibration Test Data Manual entity.
 * Contains detailed information about manual calibration test
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "CALIBRATION_TEST_DATA_MANUAL")
@NoArgsConstructor
public class CalibrationTestDataManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Enumerated(EnumType.STRING)
    public CalibrationTestResult statusTestFirst;

    @Enumerated(EnumType.STRING)
    public CalibrationTestResult statusTestSecond;

    @Enumerated(EnumType.STRING)
    public CalibrationTestResult statusTestThird;

    @Enumerated(EnumType.STRING)
    public CalibrationTestResult statusCommon;

    @ManyToOne
    @JoinColumn(name = "counterId")
    private Counter counter;

    @ManyToOne
    @JoinColumn(name = "calibrationTestManualId")
    private CalibrationTestManual calibrationTestManual;

    @ManyToOne
    @JoinColumn(name = "verificationId")
    private Verification verification;

    @ManyToOne
    @JoinColumn(name = "unsuitabilityReasonId")
    private UnsuitabilityReason unsuitabilityReason;

    public CalibrationTestDataManual(CalibrationTestResult statusTestFirst, CalibrationTestResult statusTestSecond, CalibrationTestResult statusTestThird, CalibrationTestResult statusCommon, Counter counter, CalibrationTestManual calibrationTestManual, Verification verification, UnsuitabilityReason unsuitabilityReason) {
        this.statusTestFirst = statusTestFirst;
        this.statusTestSecond = statusTestSecond;
        this.statusTestThird = statusTestThird;
        this.statusCommon = statusCommon;
        this.counter = counter;
        this.calibrationTestManual = calibrationTestManual;
        this.verification = verification;
        this.unsuitabilityReason = unsuitabilityReason;
    }
}