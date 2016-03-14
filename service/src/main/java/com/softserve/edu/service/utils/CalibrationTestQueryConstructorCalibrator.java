package com.softserve.edu.service.utils;

import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @deprecated this class have a lot of repeated code <br/>
 * {need to be replaced and removed}<br/>
 * use {@link com.softserve.edu.specification.SpecificationBuilder} instead
 */
@Deprecated
public class CalibrationTestQueryConstructorCalibrator {
    static Logger logger = Logger.getLogger(CalibrationTestQueryConstructorCalibrator.class);


    public static CriteriaQuery<CalibrationTest> buildSearchQuery(String startDateToSearch, String endDateToSearch, String name, String region, String district, String locality, String streetToSearch,
                                                               String idToSearch, String fullNameToSearch, Integer settingNumber, String consumptionStatus,
                                                               Long protocolId, String testResult,
                                                               Long measurementDeviceId,
                                                               String measurementDeviceType,
                                                               String sortCriteria, String sortOrder,  EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CalibrationTest> criteriaQuery = cb.createQuery(CalibrationTest.class);
        Root<CalibrationTest> root = criteriaQuery.from(CalibrationTest.class);

        Join<CalibrationTest, Verification> verificationJoin = root.join("verification");

        Predicate predicate = CalibrationTestQueryConstructorCalibrator.buildPredicate(root, cb,  startDateToSearch, endDateToSearch, name, region, district, locality, streetToSearch,
                idToSearch, fullNameToSearch, settingNumber, consumptionStatus, protocolId, testResult, measurementDeviceId, measurementDeviceType);
        if ((sortCriteria != null) && (sortOrder != null)) {
            criteriaQuery.orderBy(SortCriteriaCalibrationTest.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
        } else {
            criteriaQuery.orderBy(cb.desc(root.get("dateTest")));
        }
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }


    public static CriteriaQuery<Long> buildCountQuery(String startDateToSearch, String endDateToSearch, String name, String region, String district, String locality, String streetToSearch,
                                                      String idToSearch, String fullNameToSearch, Integer settingNumber, String consumptionStatus,
                                                      Long protocolId, String testResult,
                                                      Long measurementDeviceId,
                                                      String measurementDeviceType,
                                                      EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CalibrationTest> root = countQuery.from(CalibrationTest.class);

        Join<CalibrationTest, Verification> verificationJoin = root.join("verification");
        Predicate predicate = CalibrationTestQueryConstructorCalibrator.buildPredicate(root, cb,  startDateToSearch, endDateToSearch, name,
                region, district, locality, streetToSearch, idToSearch, fullNameToSearch, settingNumber, consumptionStatus, protocolId, testResult, measurementDeviceId, measurementDeviceType);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }

    private static Predicate buildPredicate(Root<CalibrationTest> root, CriteriaBuilder cb, String startDateToSearch, String endDateToSearch, String name, String region, String district, String locality, String streetToSearch,
                                            String idToSearch, String fullNameToSearch, Integer settingNumber, String consumptionStatus,
                                            Long protocolId, String testResult,
                                            Long measurementDeviceId,
                                            String measurementDeviceType) {
        Predicate queryPredicate = cb.conjunction();

        if (startDateToSearch != null && endDateToSearch != null) {
            DateTimeFormatter dbDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

            LocalDate startDate = null;
            LocalDate endDate = null;
            try {
                startDate = LocalDate.parse(startDateToSearch, dbDateTimeFormatter);
                endDate = LocalDate.parse(endDateToSearch, dbDateTimeFormatter);
            } catch (Exception pe) {
                logger.error("Cannot parse date", pe); //TODO: add exception catching
            }
            //CalibrationTests with date between these two dates
            queryPredicate = cb.and(cb.between(root.get("dateTest"), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate)), queryPredicate);

        }

        if (StringUtils.isNotEmpty(name)) {
            queryPredicate = cb.and(cb.like(root.get("name"), "%" + name + "%"), queryPredicate);
        }

        if (StringUtils.isNotEmpty(idToSearch)) {
            Join<CalibrationTest, Verification> joinCalibratorTest = root.join("verification");
            queryPredicate = cb.and(cb.like( joinCalibratorTest.get("id"), "%" + idToSearch + "%"), queryPredicate);
        }

        if (StringUtils.isNotEmpty(fullNameToSearch)) {
            Join<CalibrationTest, Verification> joinCalibratorTest = root.join("verification");
            Predicate searchPredicateByClientFullName = cb.or(cb.like(joinCalibratorTest.get("clientData").get("lastName"), "%" + fullNameToSearch + "%"));
            queryPredicate = cb.and(searchPredicateByClientFullName, queryPredicate);
        }

        if (settingNumber != null) {
            queryPredicate = cb.and(cb.like(new FilteringNumbersDataLikeStringData<Integer>(cb, root.get("settingNumber")),
                    "%" + settingNumber.toString() + "%"), queryPredicate);
        }

        if ((consumptionStatus != null)) {
            queryPredicate = cb.and(cb.equal(root.get("consumptionStatus"),
                    Verification.ConsumptionStatus.valueOf(consumptionStatus.trim())), queryPredicate);
        }

        if (StringUtils.isNotEmpty(streetToSearch)) {
            Join<CalibrationTest, Verification> joinCalibratorTest = root.join("verification");
            queryPredicate = cb.and(
                    cb.like(joinCalibratorTest.get("clientData").get("clientAddress").get("street"), "%" + streetToSearch + "%"),
                    queryPredicate);
        }

        if (StringUtils.isNotEmpty(region)) {
            Join<CalibrationTest, Verification> joinCalibratorTest = root.join("verification");
            queryPredicate = cb.and(
                    cb.like(joinCalibratorTest.get("clientData").get("clientAddress").get("region"), "%" + region + "%"),
                    queryPredicate);
        }

        if (StringUtils.isNotEmpty(district)) {
            Join<CalibrationTest, Verification> joinCalibratorTest = root.join("verification");
            queryPredicate = cb.and(
                    cb.like(joinCalibratorTest.get("clientData").get("clientAddress").get("district"), "%" + district + "%"),
                    queryPredicate);
        }

        if (StringUtils.isNotEmpty(locality)) {
            Join<CalibrationTest, Verification> joinCalibratorTest = root.join("verification");
            queryPredicate = cb.and(
                    cb.like(joinCalibratorTest.get("clientData").get("clientAddress").get("locality"), "%" + locality + "%"),
                    queryPredicate);
        }
        if (measurementDeviceId != null) {
            Join<CalibrationTest, Verification> joinCalibratorTest = root.join("verification");
            queryPredicate = cb.and(cb.like(new FilteringNumbersDataLikeStringData<Long>(cb, joinCalibratorTest.get("device").get("id")),
                    "%" + measurementDeviceId.toString() + "%"), queryPredicate);
        }
        if (measurementDeviceType != null) {
            Join<CalibrationTest, Verification> joinCalibratorTest = root.join("verification");
            queryPredicate = cb.and(cb.equal(joinCalibratorTest.get("device").get("deviceType"),
                    Device.DeviceType.valueOf(measurementDeviceType.trim())), queryPredicate);
        }
        if (protocolId != null) {
            queryPredicate = cb.and(cb.like(new FilteringNumbersDataLikeStringData<Long>(cb, root.get("id")),
                    "%" + protocolId.toString() + "%"), queryPredicate);

        }
        if (testResult != null) {
            logger.debug("CalibrationTestQueryConstructorCalibrator : testResult = " + testResult);
            queryPredicate = cb.and(cb.equal(root.get("testResult"),
                    Verification.CalibrationTestResult.valueOf(testResult.trim())), queryPredicate);
        }

        return queryPredicate;
    }
}
