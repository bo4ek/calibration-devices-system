package com.softserve.edu.service.utils;

import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.calibration.RejectedInfo;
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
public class ArchivalVerificationsQueryConstructorProvider {
    static Logger logger = Logger.getLogger(ArchivalVerificationsQueryConstructorProvider.class);


    public static CriteriaQuery<Verification> buildSearchQuery(Long employeeId, String initialDateToSearch,
                                                               String endDateToSearch, String idToSearch, String fullNameToSearch,
                                                               String streetToSearch, String region, String district, String locality, String status,
                                                               String employeeName, String building, String flat, String calibratorName, String sortCriteria, String sortOrder,
                                                               User providerEmployee, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
        Root<Verification> root = criteriaQuery.from(Verification.class);
        Join<Verification, Organization> providerJoin = root.join("provider");
        Predicate predicate = ArchivalVerificationsQueryConstructorProvider.buildPredicate(root, cb, employeeId, initialDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality,
                status, employeeName, building, flat, calibratorName, providerEmployee, providerJoin);

        if ((sortCriteria != null) && (sortOrder != null)) {
            criteriaQuery.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
        } else {
            criteriaQuery.orderBy(cb.desc(root.get("initialDate")));
        }
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }


    public static CriteriaQuery<Long> buildCountQuery(Long employeeId, String initialDateToSearch,
                                                      String endDateToSeach, String idToSearch, String fullNameToSearch, String streetToSearch, String region, String district, String locality, String status, String employeeName,
                                                      String building, String flat, String calibratorName, User providerEmployee, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Verification> root = countQuery.from(Verification.class);
        Join<Verification, Organization> providerJoin = root.join("provider");
        Predicate predicate = ArchivalVerificationsQueryConstructorProvider.buildPredicate(root, cb, employeeId, initialDateToSearch, endDateToSeach, idToSearch,
                fullNameToSearch, streetToSearch, region, district, locality, status, employeeName, building, flat, calibratorName,
                providerEmployee, providerJoin);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }

    private static Predicate buildPredicate(Root<Verification> root, CriteriaBuilder cb, Long providerId,
                                            String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch,
                                            String streetToSearch, String region, String district, String locality, String searchStatus, String employeeName, String building, String flat, String calibratorName, User employee,
                                            Join<Verification, Organization> providerJoin) {

        Predicate queryPredicate = cb.conjunction();
        queryPredicate = cb.and(cb.equal(providerJoin.get("id"), providerId), queryPredicate);

        if (searchStatus != null) {
            queryPredicate = cb.and(cb.equal(root.get("status"), Status.valueOf(searchStatus.trim())), queryPredicate);
        } else {
            queryPredicate = cb.and(cb.not(cb.or(
                    Status.SENT.getQueryPredicate(root, cb),
                    Status.ACCEPTED.getQueryPredicate(root, cb),
                    Status.SENT_TO_PROVIDER.getQueryPredicate(root, cb),
                    Status.REJECTED_BY_CALIBRATOR.getQueryPredicate(root, cb)
            )), queryPredicate);
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
        if ((region != null) && (region.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("clientData").get("clientAddress").get("region"), "%" + region + "%"),
                    queryPredicate);
        }

        if ((district != null) && (district.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("clientData").get("clientAddress").get("district"), "%" + district + "%"),
                    queryPredicate);
        }

        if ((locality != null) && (locality.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("clientData").get("clientAddress").get("locality"), "%" + locality + "%"),
                    queryPredicate);
        }

        if ((flat != null) && (flat.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("clientData").get("clientAddress").get("flat"), "%" + flat + "%"),
                    queryPredicate);
        }

        if ((building != null) && (building.length() > 0)) {
            if (building.endsWith("%")) {
                queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("building"), "%" + building + "%"), queryPredicate);
            } else {
                queryPredicate = cb.and(cb.equal(root.get("clientData").get("clientAddress").get("building"), building), queryPredicate);
            }

        }

        if ((calibratorName != null) && (calibratorName.length() > 0)) {
            Join<Verification, Organization> joinCalibratorName = root.join("calibrator");
            queryPredicate = cb.and(cb.and(cb.like(joinCalibratorName.get("name"), "%" + calibratorName + "%")), queryPredicate);
        }

        if ((employeeName != null) && (employeeName.length() > 0)) {
            Join<Verification, User> joinProviderEmployee = root.join("providerEmployee");
            Predicate searchPredicateByProviderEmployeeName = cb.or(cb.like(joinProviderEmployee.get("lastName"), "%" + employeeName + "%"));
            queryPredicate = cb.and(searchPredicateByProviderEmployeeName, queryPredicate);
        }

        return queryPredicate;
    }

    public static CriteriaQuery<Verification> buildSearchRejectedProviderQuery(Long organizationId, String startDateToSearch,
                                                                               String endDateToSearch, String rejectedReason, String employeeRejected, String providerName, String customerName, String district,
                                                                               String street, String building, String flat, String verificationId, String status, String sortCriteria, String sortOrder, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
        Root<Verification> root = criteriaQuery.from(Verification.class);
        Predicate predicate = ArchivalVerificationsQueryConstructorProvider.buildPredicateRejected(root, cb, organizationId, startDateToSearch, endDateToSearch,
                rejectedReason, employeeRejected, providerName, customerName, district, street, building, flat, verificationId, status);

        if ((sortCriteria != null) && (sortOrder != null)) {
            criteriaQuery.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
        } else {
            criteriaQuery.orderBy(cb.desc(root.get("rejectedCalibratorDate")));
        }
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }

    public static CriteriaQuery<Long> buildCountRejectedQuery(Long organizationId, String startDateToSearch,
                                                              String endDateToSearch, String rejectedReason, String employeeRejected, String providerName, String customerName, String district,
                                                              String street, String building, String flat, String verificationId, String status, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Verification> root = countQuery.from(Verification.class);
        Predicate predicate = ArchivalVerificationsQueryConstructorProvider.buildPredicateRejected(root, cb, organizationId, startDateToSearch, endDateToSearch,
                rejectedReason, employeeRejected, providerName, customerName, district, street, building, flat, verificationId, status);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }


    private static Predicate buildPredicateRejected(Root<Verification> root, CriteriaBuilder cb, Long organizationId, String startDateToSearch, String endDateToSearch,
                                                    String rejectedReason, String providerEmployee, String calibratorName, String customerName, String district,
                                                    String street, String building, String flat, String verificationId, String searchStatus) {

        Join<Verification, Organization> joinOrganizationId = root.join("provider");
        Predicate queryPredicate = cb.equal(joinOrganizationId.get("id"), organizationId);

        if (verificationId != null && verificationId.length() > 0) {
            queryPredicate = cb.and(cb.like(root.get("id"), "%" + verificationId + "%"), queryPredicate);
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
            queryPredicate = cb.and(cb.between(root.get("rejectedCalibratorDate"), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate)), queryPredicate);
        }

        if (searchStatus != null) {

            queryPredicate = cb.and(cb.equal(root.get("status"), Status.valueOf(searchStatus.trim())), queryPredicate);
        } else {
            queryPredicate = cb.and(cb.or(Status.REJECTED_BY_CALIBRATOR.getQueryPredicate(root, cb),
                    Status.REJECTED_BY_PROVIDER.getQueryPredicate(root, cb)), queryPredicate);
        }

        if ((rejectedReason != null) && (rejectedReason.length() > 0)) {
            Join<Verification, RejectedInfo> rejectedInfoJoin = root.join("rejectedInfo");
            queryPredicate = cb.and(cb.like(rejectedInfoJoin.get("name"), "%" + rejectedReason + "%"), queryPredicate);
        }

        if ((providerEmployee != null) && (providerEmployee.length() > 0)) {
            Join<Verification, User> joinProviderEmployee = root.join("providerEmployee");
            queryPredicate = cb.and(cb.and(cb.like(joinProviderEmployee.get("lastName"), "%" + providerEmployee + "%")), queryPredicate);
        }

        if ((calibratorName != null) && (calibratorName.length() > 0)) {
            Join<Verification, Organization> joinCalibratorName = root.join("calibrator");
            queryPredicate = cb.and(cb.and(cb.like(joinCalibratorName.get("name"), "%" + calibratorName + "%")), queryPredicate);
        }


        if ((customerName != null) && (customerName.length() > 0)) {
            queryPredicate = cb.and(cb.and(cb.like(root.get("clientData").get("lastName"), "%" + customerName + "%")), queryPredicate);
        }

        if ((street != null) && (street.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("street"), "%" + street + "%"), queryPredicate);
        }

        if ((district != null) && (district.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("district"), "%" + district + "%"), queryPredicate);
        }

        if ((building != null) && (building.length() > 0)) {
            if (building.endsWith("%")) {
                queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("building"), "%" + building + "%"), queryPredicate);
            } else {
                queryPredicate = cb.and(cb.equal(root.get("clientData").get("clientAddress").get("building"), building), queryPredicate);
            }

        }

        if ((flat != null) && (flat.length() > 0)) {
            queryPredicate = cb.and(cb.like(root.get("clientData").get("clientAddress").get("flat"), "%" + flat + "%"), queryPredicate);
        }

        return queryPredicate;
    }
}
