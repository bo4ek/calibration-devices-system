package com.softserve.edu.entity.verification.calibration;

import com.softserve.edu.common.Constants;
import com.softserve.edu.entity.verification.Verification;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


/**
 * Calibration Test Data entity.
 * Contains information about measurement device calibration test data.Contains information about measurement device
 * calibration test data.
 */
@Entity
@Table(name = "CALIBRATION_TEST_DATA")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class CalibrationTestData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double givenConsumption;
    private Long acceptableError;
    private Double volumeOfStandard;
    private Double initialValue;
    private Double endValue;
    private Double volumeInDevice;
    private Double duration;
    private Integer testPosition;
    private Double actualConsumption;
    private Double calculationError;
    private Long lowerConsumptionLimit;
    private Long upperConsumptionLimit;

    @Enumerated(EnumType.STRING)
    private Verification.ConsumptionStatus consumptionStatus;
    @Enumerated(EnumType.STRING)
    private Verification.CalibrationTestResult testResult;

    @ManyToOne
    @JoinColumn(name = "calibrationTestId")
    private CalibrationTest calibrationTest;

    @OneToMany(mappedBy = "calibrationTestData")
    private List<CalibrationTestIMG> testIMGs;

    public CalibrationTestData(Double givenConsumption, Long acceptableError, Double volumeOfStandard,
                               Double initialValue, Double endValue,
                               Double actualConsumption, Double calculationError,
                               CalibrationTest calibrationTest, Double duration, Long lowerConsumptionLimit,
                               Long upperConsumptionLimit, Integer testPosition) {
        this.givenConsumption = givenConsumption;
        this.acceptableError = acceptableError;
        this.volumeOfStandard = volumeOfStandard;
        this.initialValue = initialValue;
        this.endValue = endValue;
        this.volumeInDevice = BigDecimal.valueOf(this.getEndValue() - this.getInitialValue()).
                setScale(Constants.SCALE_2, RoundingMode.HALF_UP).doubleValue();
        this.actualConsumption = actualConsumption;
        this.calculationError = calculationError;
        this.lowerConsumptionLimit = lowerConsumptionLimit;
        this.upperConsumptionLimit = upperConsumptionLimit;
        if (this.getEndValue() == 0 || this.getInitialValue() > this.getEndValue()) {
            this.testResult = Verification.CalibrationTestResult.NOT_PROCESSED;
        } else {
            if (Math.abs(this.getCalculationError()) <= this.getAcceptableError()) {
                this.testResult = Verification.CalibrationTestResult.SUCCESS;
            } else {
                this.testResult = Verification.CalibrationTestResult.FAILED;
            }
        }
        if ((this.getGivenConsumption() - (this.getGivenConsumption() * this.getLowerConsumptionLimit()
                / Constants.PERCENT) <= this.getActualConsumption()) & (this.getActualConsumption()
                <= (this.getGivenConsumption() + (this.getGivenConsumption() * this.getUpperConsumptionLimit())
                / Constants.PERCENT))) {
            this.consumptionStatus = Verification.ConsumptionStatus.IN_THE_AREA;
        } else {
            this.consumptionStatus = Verification.ConsumptionStatus.NOT_IN_THE_AREA;
        }
        this.calibrationTest = calibrationTest;
        this.duration = duration;
        this.testPosition = testPosition;
    }


    public CalibrationTestData(Double initialValue, Double endValue, Double calculationError, String testResult) {
        this.initialValue = initialValue;
        this.endValue = endValue;
        this.calculationError = calculationError;
        this.testResult = Verification.CalibrationTestResult.valueOf(testResult);
    }

}
