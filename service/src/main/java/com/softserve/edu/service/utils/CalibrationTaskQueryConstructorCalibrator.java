package com.softserve.edu.service.utils;

import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class CalibrationTaskQueryConstructorCalibrator {

    static Logger logger = Logger.getLogger(CalibrationTaskQueryConstructorCalibrator.class);

    public static CriteriaQuery<Long> buildCountQuery(String startDateToSearch, String endDateToSearch, String name,
                                                      String leaderFullName, String leaderPhone,
                                                      User calibratorEmployee, EntityManager em, Boolean allTests) throws ParseException {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CalibrationTask> root = countQuery.from(CalibrationTask.class);
        Predicate predicate = CalibrationTaskQueryConstructorCalibrator.buildPredicate(root, cb, startDateToSearch, endDateToSearch,
                name, leaderFullName, leaderPhone, calibratorEmployee, countQuery, allTests);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }


    public static CriteriaQuery<CalibrationTask> buildSearchQuery(String startDateToSearch, String endDateToSearch, String name,
                                                                  String leaderFullName, String leaderPhone,
                                                                  User calibratorEmployee, String sortCriteria, String sortOrder, EntityManager em, Boolean allTests) throws ParseException {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CalibrationTask> criteriaQuery = cb.createQuery(CalibrationTask.class);
        Root<CalibrationTask> root = criteriaQuery.from(CalibrationTask.class);

        Predicate predicate = CalibrationTaskQueryConstructorCalibrator.buildPredicate(root, cb, startDateToSearch, endDateToSearch,
                name, leaderFullName, leaderPhone, calibratorEmployee, criteriaQuery, allTests);


        if ((sortCriteria.equals("default")) && (sortOrder.equals("default"))) {
            criteriaQuery.orderBy(cb.desc(root.get("dateOfTask")), cb.asc((root.get("module").get("moduleNumber"))));
        } else if ((sortOrder != null)) {
            if (sortCriteria.contains(".")) {
                String[] fields = sortCriteria.split("\\.");
                if (sortOrder.equalsIgnoreCase("asc")) {
                    criteriaQuery.orderBy(cb.asc(root.get(fields[0]).get(fields[1])));
                } else {
                    criteriaQuery.orderBy(cb.desc(root.get(fields[0]).get(fields[1])));
                }
            } else {
                if (sortOrder.equalsIgnoreCase("asc")) {
                    criteriaQuery.orderBy(cb.asc(root.get(sortCriteria)));
                } else {
                    criteriaQuery.orderBy(cb.desc(root.get(sortCriteria)));
                }
            }
        }
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);

        return criteriaQuery;
    }

    /**
     * Method builds list of predicates
     */
    private static Predicate buildPredicate(Root<CalibrationTask> root, CriteriaBuilder cb,
                                            String startDateToSearch, String endDateToSearch,
                                            String name, String leaderFullName, String leaderPhone, User calibratorEmployee,
                                            CriteriaQuery criteriaQuery, Boolean allTests) throws ParseException {

        String userName = calibratorEmployee.getUsername();
        Predicate queryPredicate = cb.conjunction();

        /*Predicate searchPredicateByUsername = cb.equal(root.get("user").get("username"), userName);
        queryPredicate = cb.and(searchPredicateByUsername, queryPredicate);*/

        Predicate searchPredicateByOrganization = cb.equal(root.get("team").get("organization").get("id"),
                calibratorEmployee.getOrganization().getId());
        queryPredicate = cb.and(searchPredicateByOrganization, queryPredicate);

        queryPredicate = cb.and(root.get("team").isNotNull(), queryPredicate);

        if (!allTests) {
            Subquery<Long> subQuerySubQueryCountOfVerifications = criteriaQuery.subquery(Long.class);
            Root fromVerification = subQuerySubQueryCountOfVerifications.from(Verification.class);
            subQuerySubQueryCountOfVerifications.where(cb.and(cb.equal(fromVerification.get("task").get("id"), root.get("id"))));
            subQuerySubQueryCountOfVerifications.select(cb.count(fromVerification));

            Subquery<Long> subQuerySubQueryCountOfCompletedVerifications = criteriaQuery.subquery(Long.class);
            fromVerification = subQuerySubQueryCountOfCompletedVerifications.from(Verification.class);
            subQuerySubQueryCountOfCompletedVerifications.where(cb.and(cb.isTrue(fromVerification.get("counterStatus")), cb.equal(fromVerification.get("task").get("id"), root.get("id"))));
            subQuerySubQueryCountOfCompletedVerifications.select(cb.count(fromVerification));

            queryPredicate = cb.and(cb.greaterThan(subQuerySubQueryCountOfVerifications.getSelection(), 0L), queryPredicate);
            queryPredicate = cb.and(cb.greaterThan(subQuerySubQueryCountOfVerifications.getSelection(), subQuerySubQueryCountOfCompletedVerifications.getSelection()), queryPredicate);
        }

        if (startDateToSearch != null && endDateToSearch != null) {
            DateTimeFormatter dbDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

            LocalDate startDate = LocalDate.parse(startDateToSearch, dbDateTimeFormatter);
            LocalDate endDate = LocalDate.parse(endDateToSearch, dbDateTimeFormatter);
            queryPredicate = cb.and(cb.between(root.get("createTaskDate"), java.sql.Date.valueOf(startDate),
                    java.sql.Date.valueOf(endDate)), queryPredicate);

        }


        if (leaderFullName != null && leaderFullName.length() > 0) {
            queryPredicate = cb.and(
                    cb.like(root.get("team").get("leaderFullName"), "%" + leaderFullName + "%"),
                    queryPredicate);
        }
        if ((name != null) && (name.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("team").get("name"), "%" + name + "%"),
                    queryPredicate);
        }

        if ((leaderPhone != null) && (leaderPhone.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("team").get("leaderPhone"), "%" + leaderPhone + "%"),
                    queryPredicate);
        }

        return queryPredicate;
    }
}
