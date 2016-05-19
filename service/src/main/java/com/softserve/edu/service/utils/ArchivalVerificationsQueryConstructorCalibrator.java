package com.softserve.edu.service.utils;

import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.calibration.RejectedInfo;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                logger.error("Cannot parse date", pe);
            }
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

        if (employee == null) {
            queryPredicate = cb.and(root.get("calibratorEmployee").isNull(), queryPredicate);
        }

        return queryPredicate;
    }

    public static CriteriaQuery<Verification> buildSearchRejectedQuery(Long employeeId, String startDateToSearch,
                                                                       String endDateToSearch, String rejectedReason, String employeeRejected, String providerName, String customerName, String district,
                                                                       String street, String building, String flat, String verificationId, String sortCriteria, String sortOrder, User calibratorEmployee, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
        Root<Verification> root = criteriaQuery.from(Verification.class);

        Join<Verification, Organization> calibratorJoin = root.join("calibrator");
        Join<Verification, RejectedInfo> rejectedInfoJoin = root.join("rejectedInfo");

        Predicate predicate = ArchivalVerificationsQueryConstructorCalibrator.buildPredicateRejected(root, cb, employeeId, startDateToSearch, endDateToSearch,
                rejectedReason, employeeRejected, calibratorEmployee, calibratorJoin, rejectedInfoJoin, providerName, customerName, district, street, building, flat, verificationId);


        if ((sortCriteria != null) && (sortOrder != null)) {
            criteriaQuery.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
        } else {
            criteriaQuery.orderBy(cb.desc(root.get("rejectedCalibratorDate")));
        }
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }

    public static CriteriaQuery<Verification> buildSearchPlaningTaskQuery(Long organizationId, Integer pageNumber, Integer itemsPerPage, String date, String endDate, String client_full_name, String provider, String district,
                                                                          String street, String building, String flat, String dateOfVerif, String time, String serviceability, String noWaterToDate, String sealPresence,
                                                                          String telephone, String verificationWithDismantle, String sortCriteria, String sortOrder, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);

        Root<Verification> root = criteriaQuery.from(Verification.class);

        Join<Verification, AdditionalInfo> additionalInfoJoin = root.join("info");

        Predicate predicate = ArchivalVerificationsQueryConstructorCalibrator.buildPredicatePlannedTask(root, cb, organizationId, date, endDate, client_full_name, provider, district,
                street, building, flat, dateOfVerif, time, serviceability, noWaterToDate, sealPresence, telephone, verificationWithDismantle, additionalInfoJoin);

        if ((sortCriteria != null) && (sortOrder != null)) {
            criteriaQuery.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
        } else {
            criteriaQuery.orderBy(cb.desc(root.get("sentToCalibratorDate")));
        }
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }

    private static Predicate buildPredicatePlannedTask(Root<Verification> root, CriteriaBuilder cb, Long organizationId, String startDateToSearch, String endDateToSearch, String client_full_name,
                                                       String provider, String district, String street, String building, String flat, String dateOfVerif, String time, String serviceability, String noWaterToDate,
                                                       String sealPresence, String telephone, String verificationWithDismantle, Join<Verification, AdditionalInfo> additionalInfoJoin) {

        Predicate queryPredicate = cb.equal(root.get("calibrator"), organizationId);
        DateTimeFormatter dbDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter baseDateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE;

        queryPredicate = cb.and(cb.equal(root.get("taskStatus"), Status.PLANNING_TASK), queryPredicate);
        queryPredicate = cb.and(cb.equal(root.get("status"), Status.IN_PROGRESS), queryPredicate);

        if (startDateToSearch != null && endDateToSearch != null) {
            LocalDate startDate;
            LocalDate endDate;
            try {
                startDate = LocalDate.parse(startDateToSearch, dbDateTimeFormatter);
                endDate = LocalDate.parse(endDateToSearch, dbDateTimeFormatter);
                queryPredicate = cb.and(cb.between(root.get("sentToCalibratorDate"), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate)), queryPredicate);
            } catch (Exception pe) {
                logger.error("Cannot parse date", pe);
            }
        }

        if ((serviceability != null && (serviceability.length() > 0))) {
            Boolean serviceabilityReceived = Boolean.valueOf(serviceability);
            if (serviceabilityReceived == true) {
                queryPredicate = cb.and(cb.isTrue(additionalInfoJoin.get("serviceability")), queryPredicate);
            } else {
                queryPredicate = cb.and(cb.isFalse(additionalInfoJoin.get("serviceability")), queryPredicate);
            }
        }

        if ((sealPresence != null && (sealPresence.length() > 0))) {
            Boolean sealPresenceReceived = Boolean.valueOf(sealPresence);
            if (sealPresenceReceived == true) {
                queryPredicate = cb.and(cb.isTrue(root.get("sealPresence")), queryPredicate);
            } else {
                queryPredicate = cb.and(cb.isFalse(root.get("sealPresence")), queryPredicate);
            }
        }

        if ((verificationWithDismantle != null && (verificationWithDismantle.length() > 0))) {
            Boolean dismantledReceived = Boolean.valueOf(verificationWithDismantle);
            if (dismantledReceived == true) {
                queryPredicate = cb.and(cb.isTrue(root.get("verificationWithDismantle")), queryPredicate);
            } else {
                queryPredicate = cb.and(cb.isFalse(root.get("verificationWithDismantle")), queryPredicate);
            }
        }

        if (dateOfVerif != null && checkString(dateOfVerif) && dateOfVerif.length() == 8) {
            LocalDate date = null;
            try {
                date = LocalDate.parse(dateOfVerif, baseDateTimeFormatter);
                queryPredicate = cb.and(cb.equal(additionalInfoJoin.get("dateOfVerif"), java.sql.Date.valueOf(date)), queryPredicate);
            } catch (Exception pe) {
                logger.error("Cannot parse date", pe);
            }
        }

        if (dateOfVerif != null && checkString(dateOfVerif) && dateOfVerif.length() == 4) {
            LocalDateTime timePoint = LocalDateTime.now();
            LocalDate date = null;
            try {
                date = LocalDate.parse(timePoint.getYear() + dateOfVerif, baseDateTimeFormatter);
                queryPredicate = cb.and(cb.equal(additionalInfoJoin.get("dateOfVerif"), java.sql.Date.valueOf(date)), queryPredicate);
            } catch (Exception pe) {
                logger.error("Cannot parse date", pe);
            }
        }

        if (noWaterToDate != null && noWaterToDate.length() == 8 && checkString(noWaterToDate)) {
            LocalDate date = null;
            try {
                date = LocalDate.parse(noWaterToDate, baseDateTimeFormatter);
                queryPredicate = cb.and(cb.equal(additionalInfoJoin.get("noWaterToDate"), java.sql.Date.valueOf(date)), queryPredicate);
            } catch (Exception pe) {
                logger.error("Cannot parse date", pe);
            }
        }

        if (noWaterToDate != null && noWaterToDate.length() == 4 && checkString(noWaterToDate)) {
            LocalDate date = null;
            LocalDateTime timePoint = LocalDateTime.now();
            try {
                date = LocalDate.parse(timePoint.getYear() + noWaterToDate, baseDateTimeFormatter);
                queryPredicate = cb.and(cb.equal(additionalInfoJoin.get("noWaterToDate"), java.sql.Date.valueOf(date)), queryPredicate);
            } catch (Exception pe) {
                logger.error("Cannot parse date", pe);
            }
        }

        if ((client_full_name != null) && (client_full_name.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("lastName"), "%" + client_full_name + "%"), queryPredicate);
        }

        if ((provider != null) && (provider.length() > 0)) {
            Join<Verification, Organization> joinProviderName = root.join("provider");
            queryPredicate = cb.and(cb.like(joinProviderName.get("name"), "%" + provider + "%"), queryPredicate);
        }

        if ((street != null) && (street.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("street"), "%" + street + "%"), queryPredicate);
        }

        if ((district != null) && (district.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("district"), "%" + district + "%"), queryPredicate);
        }

        if ((building != null) && (building.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("building"), "%" + building + "%"), queryPredicate);
        }

        if ((flat != null) && (flat.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("flat"), "%" + flat + "%"), queryPredicate);
        }

        if ((telephone != null) && (telephone.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("phone"), "%" + telephone + "%"), queryPredicate);
        }

        return queryPredicate;
    }

    private static Predicate buildPredicateRejected(Root<Verification> root, CriteriaBuilder cb, Long employeeId, String startDateToSearch, String endDateToSearch,
                                                    String rejectedReason, String employeeRejected, User calibratorEmployee, Join<Verification,
            Organization> calibratorJoin, Join<Verification, RejectedInfo> rejectedJoin, String providerName, String customerName, String district,
                                                    String street, String building, String flat, String verificationId) {

        Predicate queryPredicate = cb.equal(calibratorJoin.get("id"), employeeId);



        if (startDateToSearch != null && endDateToSearch != null) {
            DateTimeFormatter dbDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

            LocalDate startDate = null;
            LocalDate endDate = null;
            try {
                startDate = LocalDate.parse(startDateToSearch, dbDateTimeFormatter);
                endDate = LocalDate.parse(endDateToSearch, dbDateTimeFormatter);
            } catch (Exception pe) {
                logger.error("Cannot parse date", pe);
            }
            queryPredicate = cb.and(cb.between(root.get("rejectedCalibratorDate"), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate)), queryPredicate);
        }

        queryPredicate = cb.and(cb.equal(root.get("status"), Status.REJECTED_BY_CALIBRATOR), queryPredicate);

        if ((rejectedReason != null) && (rejectedReason.length() > 0)) {
            queryPredicate = cb.and(cb.like(rejectedJoin.get("name"), "%" + rejectedReason + "%"), queryPredicate);
        }

        if ((employeeRejected != null) && (employeeRejected.length() > 0)) {
            Join<Verification, User> joinCalibratorEmployee = root.join("calibratorEmployee");
            queryPredicate = cb.and(cb.or(cb.like(joinCalibratorEmployee.get("lastName"), "%" + employeeRejected + "%")), queryPredicate);
        }

        if ((providerName != null) && (providerName.length() > 0)) {
            Join<Verification, Organization> joinProviderName = root.join("provider");
            queryPredicate = cb.and(cb.and(cb.like(joinProviderName.get("name"), "%" + providerName + "%")), queryPredicate);
        }

        if ((verificationId != null) && (verificationId.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("id"), "%" + verificationId + "%"), queryPredicate);
        }

        if ((customerName != null) && (customerName.length() > 0)) {
            queryPredicate = cb.and(cb.or(cb.like(root.get("clientData").get("lastName"), "%" + customerName + "%")), queryPredicate);
        }

        if ((street != null) && (street.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("street"), "%" + street + "%"), queryPredicate);
        }

        if ((district != null) && (district.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("district"), "%" + district + "%"), queryPredicate);
        }

        if ((building != null) && (building.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("building"), "%" + building + "%"), queryPredicate);
        }

        if ((flat != null) && (flat.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("flat"), "%" + flat + "%"), queryPredicate);
        }



        return queryPredicate;
    }

    public static CriteriaQuery<Long> buildCountRejectedQuery(Long employeeId, String startDateToSearch,
                                                              String endDateToSearch, String rejectedReason, String employeeRejected, String providerName, String customerName, String district,
                                                              String street, String building, String flat, String verificationId, String sortCriteria, String sortOrder, User calibratorEmployee, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Verification> root = countQuery.from(Verification.class);
        Join<Verification, Organization> calibratorJoin = root.join("calibrator");
        Join<Verification, RejectedInfo> rejectedInfoJoin = root.join("rejectedInfo");
        Predicate predicate = ArchivalVerificationsQueryConstructorCalibrator.buildPredicateRejected(root, cb, employeeId, startDateToSearch, endDateToSearch,
                rejectedReason, employeeRejected, calibratorEmployee, calibratorJoin, rejectedInfoJoin, providerName, customerName, district, street, building, flat, verificationId);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }


    public static CriteriaQuery<Long> buildCountPlaningTaskQuery(Long organizationId, Integer pageNumber, Integer itemsPerPage, String date, String endDate, String client_full_name, String provider, String district,
                                                                 String street, String building, String flat, String dateOfVerif, String time, String serviceability, String noWaterToDate, String sealPresence,
                                                                 String telephone, String verificationWithDismantle, String sortCriteria, String sortOrder, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        ;
        Root<Verification> root = countQuery.from(Verification.class);
        Join<Verification, AdditionalInfo> additionalInfoJoin = root.join("info");
        Predicate predicate = ArchivalVerificationsQueryConstructorCalibrator.buildPredicatePlannedTask(root, cb, organizationId, date, endDate, client_full_name, provider, district,
                street, building, flat, dateOfVerif, time, serviceability, noWaterToDate, sealPresence, telephone, verificationWithDismantle, additionalInfoJoin);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }

    private static boolean checkString(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

