package com.softserve.edu.service.utils;

import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
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
public class ArchivalVerificationsQueryConstructorCalibrator {
    static Logger logger = Logger.getLogger(ArchivalVerificationsQueryConstructorCalibrator.class);


    public static CriteriaQuery<Verification> buildSearchQuery(Long employeeId, String startDateToSearch,
                                                               String endDateToSearch, String idToSearch, String fullNameToSearch, String streetToSearch, String status, String employeeName,
                                                               Long protocolId, String protocolStatus,
                                                               String numberCounter,
                                                               String measurementDeviceType,
                                                               String sortCriteria, String sortOrder, User calibratorEmployee, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
        Root<Verification> root = criteriaQuery.from(Verification.class);

        Join<Verification, Organization> calibratorJoin = root.join("calibrator");

        Predicate predicate = ArchivalVerificationsQueryConstructorCalibrator.buildPredicate(root, cb, employeeId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch,
                status, employeeName, protocolId, protocolStatus, numberCounter, measurementDeviceType, calibratorEmployee, calibratorJoin);
        if ((sortCriteria != null) && (sortOrder != null)) {
            criteriaQuery.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
        } else {
            criteriaQuery.orderBy(cb.desc(root.get("initialDate")));
        }
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }


    public static CriteriaQuery<Long> buildCountQuery(Long employeeId, String startDateToSearch, String endDateToSearch,
                                                      String idToSearch, String fullNameToSearch,
                                                      String streetToSearch, String status, String employeeName,
                                                      Long protocolId, String protocolStatus, String numberCounter, String measurementDeviceType,
                                                      User calibratorEmployee, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Verification> root = countQuery.from(Verification.class);
        Join<Verification, Organization> calibratorJoin = root.join("calibrator");
        Predicate predicate = ArchivalVerificationsQueryConstructorCalibrator.buildPredicate(root, cb, employeeId, startDateToSearch, endDateToSearch,
                idToSearch, fullNameToSearch, streetToSearch, status, employeeName, protocolId, protocolStatus, numberCounter, measurementDeviceType, calibratorEmployee, calibratorJoin);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }

    private static Predicate buildPredicate(Root<Verification> root, CriteriaBuilder cb, Long employeeId,
                                            String startDateToSearch, String endDateToSearch, String idToSearch,
                                            String fullNameToSearch, String streetToSearch, String searchStatus,
                                            String employeeName, Long protocolId, String protocolStatus,
                                            String numberCounter, String measurementDeviceType,
                                            User employee, Join<Verification, Organization> calibratorJoin) {
        Predicate queryPredicate = cb.equal(calibratorJoin.get("id"), employeeId);

        if (searchStatus != null) {
            queryPredicate = cb.and(cb.equal(root.get("status"), Status.valueOf(searchStatus.trim())), queryPredicate);
        } else {
            queryPredicate = cb.and(cb.or(
                    Status.TEST_OK.getQueryPredicate(root, cb),
                    Status.TEST_NOK.getQueryPredicate(root, cb),
                    Status.SENT_TO_VERIFICATOR.getQueryPredicate(root, cb)
            ), queryPredicate);
        }

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
            //verifications with date between these two dates
            queryPredicate = cb.and(cb.between(root.get("initialDate"), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate)), queryPredicate);

        }

        if ((idToSearch != null) && (idToSearch.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("id"), "%" + idToSearch + "%"), queryPredicate);
        }

        if ((fullNameToSearch != null) && (fullNameToSearch.length() > 0)) {
            Predicate searchPredicateByClientFullName = cb.or(cb.like(root.get("clientData").get("lastName"), "%" + fullNameToSearch + "%"));
            queryPredicate = cb.and(searchPredicateByClientFullName, queryPredicate);
        }

        if ((streetToSearch != null) && (streetToSearch.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("clientData").get("clientAddress").get("street"), "%" + streetToSearch + "%"),
                    queryPredicate);
        }
        if ((employeeName != null) && (employeeName.length() > 0)) {
            Join<Verification, User> joinCalibratorEmployee = root.join("calibratorEmployee");
            Predicate searchPredicateByCalibratorEmployeeName = cb.or(cb.like(joinCalibratorEmployee.get("lastName"),
                    "%" + employeeName + "%"));
            queryPredicate = cb.and(searchPredicateByCalibratorEmployeeName, queryPredicate);
        }
        if (numberCounter != null) {
            Join<Verification, Counter> joinVerificationCounter = root.join("counter");
            queryPredicate = cb.and(cb.like(joinVerificationCounter.get("numberCounter"),
                    "%" + numberCounter + "%"), queryPredicate);
        }
        if (measurementDeviceType != null) {
            queryPredicate = cb.and(cb.equal(root.get("device").get("deviceType"),
                    Device.DeviceType.valueOf(measurementDeviceType.trim())), queryPredicate);
        }
        if (protocolId != null) {
            Join<Verification, CalibrationTest> joinCalibratorTest = root.join("calibrationTests");
            queryPredicate = cb.and(cb.like(new FilteringNumbersDataLikeStringData<Long>(cb, joinCalibratorTest.get("id")),
                    "%" + protocolId.toString() + "%"), queryPredicate);

        }
        if (protocolStatus != null) {
            logger.debug("ArchiveVerificationQueryConstructorCalibrator : protocolStatus = " + protocolStatus);
            Join<Verification, CalibrationTest> joinCalibratorTest = root.join("calibrationTests");
            queryPredicate = cb.and(cb.equal(joinCalibratorTest.get("testResult"),
                    Verification.CalibrationTestResult.valueOf(protocolStatus.trim())), queryPredicate);
        }
        
        if(employee == null) {
        	queryPredicate = cb.and(root.get("calibratorEmployee").isNull(), queryPredicate);
        }         	

        return queryPredicate;
    }
}

