package com.softserve.edu.service.utils;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class VerificationsQueryConstructor {

    public static CriteriaQuery<Verification> buildSearchQuery(String moduleNumber, java.sql.Date testDate, User employee, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
        Root<Verification> root = criteriaQuery.from(Verification.class);

        Predicate predicate = buildQuery(root, cb, employee, moduleNumber, testDate);

        criteriaQuery.select(root);
        criteriaQuery.where(predicate);

        return criteriaQuery;
    }

    public static Predicate buildQuery(Root<Verification> root, CriteriaBuilder cb, User employee, String moduleNumber,
                                       java.sql.Date testDate) {
        Join<Verification, CalibrationTest> testJoin = root.join("calibrationTests");
        Join<Verification, CalibrationModule> moduleJoin = root.join("calibrationModule");

        return buildPredicate(root, cb, moduleJoin, testJoin, employee, moduleNumber, testDate);
    }

    public static CriteriaQuery<Long> buildCountQuery(String moduleNumber, java.sql.Date testDate, User employee, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Verification> root = criteriaQuery.from(Verification.class);

        Predicate predicate = buildQuery(root, cb, employee, moduleNumber, testDate);

        criteriaQuery.select(cb.count(root));
        criteriaQuery.where(predicate);

        return criteriaQuery;
    }

    private static Predicate buildPredicate(Root<Verification> root, CriteriaBuilder cb, Join<Verification, CalibrationModule> moduleJoin,
                                            Join<Verification, CalibrationTest> testJoin, User employee, String moduleNumber, java.sql.Date testDate) {

        String userName = employee.getUsername();
        Predicate queryPredicate = cb.conjunction();
        Set<UserRole> roleList = employee.getUserRoles();
        if(roleList.contains(UserRole.STATE_VERIFICATOR_EMPLOYEE)) {
            Join<Verification, User> joinVerificatorEmployee = root.join("stateVerificatorEmployee", JoinType.LEFT);
            Predicate searchPredicateByUsername = cb.equal(joinVerificatorEmployee.get("username"), userName);
            Predicate searchPredicateByEmptyField = cb.isNull(joinVerificatorEmployee.get("username"));
            Predicate searchByVerificatorEmployee = cb.or(searchPredicateByUsername, searchPredicateByEmptyField);
            queryPredicate = cb.and(searchByVerificatorEmployee, queryPredicate);
        } else {
            Join<Verification, Organization> verificatorJoin = root.join("stateVerificator");
            Predicate searchPredicateByOrganizationId = cb.equal(verificatorJoin.get("id"), employee.getOrganization().getId());
            queryPredicate = cb.and(searchPredicateByOrganizationId, queryPredicate);
        }

        Predicate serchPredicateByModuleNumber = cb.equal(moduleJoin.get("moduleNumber"), moduleNumber);
        queryPredicate=cb.and(serchPredicateByModuleNumber, queryPredicate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDate);
        calendar.add(Calendar.DATE, 1);
        Date endTestDate = new java.sql.Date(calendar.getTimeInMillis());
        Predicate searchPredicateByTestDate = cb.between(testJoin.<java.sql.Date>get("dateTest"), testDate, endTestDate);
        queryPredicate=cb.and(searchPredicateByTestDate, queryPredicate);

        queryPredicate = cb.and(cb.equal(root.get("status"), Status.SENT_TO_VERIFICATOR), queryPredicate);
        queryPredicate = cb.and(cb.isFalse(root.get("signed")), queryPredicate);

        return queryPredicate;
    }
}
