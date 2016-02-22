package com.softserve.edu.service.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import org.apache.log4j.Logger;

import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;

/**
 * @deprecated this class have a lot of repeated code <br/>
 * {need to be replaced and removed}<br/>
 * use {@link com.softserve.edu.specification.SpecificationBuilder} instead
 */
@Deprecated
public class NewVerificationsQueryConstructorVerificator {

    static Logger logger = Logger.getLogger(NewVerificationsQueryConstructorVerificator.class);

    /**
     * Method dynamically builds query to database depending on input parameters specified.
     *
     * @param verificatorID
     * 		search by organization ID
     * @param dateToSearch
     * 		search by initial date of verification (optional)
     * @param idToSearch
     * 		search by verification ID
     * @param verificatorEmployee
     * 		used to additional query restriction if logged user is simple employee (not admin)
     * @param em
     * 		EntityManager needed to have a possibility to create query
     * @return CriteriaQuery<Verification>
     */
    public static CriteriaQuery<Verification> buildSearchQuery(Long verificatorID, String dateToSearch, String idToSearch, String status,
                                                               User verificatorEmployee,  String nameProvider,String nameCalibrator, String numberOfCounter,
                                                               String numberOfProtocol,
                                                               String sentToVerificatorDate, String serialNumber, String sortCriteria, String sortOrder, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
        Root<Verification> root = criteriaQuery.from(Verification.class);
        Join<Verification, Organization> verificatorJoin = root.join("stateVerificator");

        Predicate predicate = NewVerificationsQueryConstructorVerificator.buildPredicate(root, cb, verificatorJoin, verificatorID, dateToSearch, idToSearch,
                status, verificatorEmployee, nameProvider, nameCalibrator, numberOfCounter,
                numberOfProtocol, sentToVerificatorDate, serialNumber);

        if ((sortCriteria.equals("default")) && (sortOrder.equals("default"))) {
            criteriaQuery.orderBy(cb.desc(root.get("sentToVerificatorDate")),cb.desc(root.get("initialDate")),
                    cb.asc((root.get("calibrationModule").get("serialNumber"))), cb.asc(root.get("numberOfProtocol")));
        } else if ((sortCriteria != null) && (sortOrder != null)) {
            criteriaQuery.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
        }
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);

        return criteriaQuery;
    }

    /**
     * Method dynamically builds query to database depending on input parameters specified.
     * Needed to get max count of rows with current predicates for pagination
     *
     * 		search by client's last name
     * @param verificatorID
     * 		search by organization ID
     * @param dateToSearch
     * 		search by initial date of verification (optional)
     * @param idToSearch
     * 		search by verification ID
     * 		search by client's street
     * @param verificatorEmployee
 * 		used to additional query restriction if logged user is simple employee (not admin)
     * @param em
* 		EntityManager needed to have a possibility to create query    @return CriteriaQuery<Long>
     */
    public static CriteriaQuery<Long> buildCountQuery(Long verificatorID, String dateToSearch, String idToSearch, String status,
                                                      User verificatorEmployee, String nameProvider, String nameCalibrator, String numberOfCounter,
                                                      String numberOfProtocol,
                                                      String sentToVerificatorDate, String serialNumber, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Verification> root = countQuery.from(Verification.class);
        Join<Verification, Organization> verificatorJoin = root.join("stateVerificator");
        Predicate predicate = NewVerificationsQueryConstructorVerificator.buildPredicate(root, cb, verificatorJoin, verificatorID, dateToSearch, idToSearch,
                status, verificatorEmployee, nameProvider, nameCalibrator, numberOfCounter,
                numberOfProtocol, sentToVerificatorDate, serialNumber);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }

    /**
     * Method builds list of predicates depending on parameters passed
     * Rule for predicates compounding - conjunction (AND)
     */
    private static Predicate buildPredicate(Root<Verification> root, CriteriaBuilder cb, Join<Verification, Organization> joinSearch, Long verificatorId,
                                            String dateToSearch, String idToSearch, String status, User verificatorEmployee, String nameProvider, String nameCalibrator,
                                            String numberOfCounter, String numberOfProtocol,
                                            String sentToVerificatorDate, String serialNumber) {

        String userName = verificatorEmployee.getUsername();
        Predicate queryPredicate = cb.conjunction();
        Set<UserRole> roles= verificatorEmployee.getUserRoles();
        for (UserRole userRole : roles) {
            String role = userRole.name();
            if(role.equalsIgnoreCase("STATE_VERIFICATOR_EMPLOYEE")) {
                Join<Verification, User> joinVerificatorEmployee = root.join("stateVerificatorEmployee", JoinType.LEFT);
                Predicate searchPredicateByUsername =cb.equal(joinVerificatorEmployee.get("username"), userName);
                Predicate searchPredicateByEmptyField = cb.isNull(joinVerificatorEmployee.get("username"));
                Predicate searchByVerificatorEmployee=cb.or(searchPredicateByUsername,searchPredicateByEmptyField);
                queryPredicate=cb.and(searchByVerificatorEmployee);
            }
        }

        if (status != null) {
        	queryPredicate = cb.and(cb.equal(root.get("status"), Status.valueOf(status.trim())), queryPredicate);
        } else {
        	queryPredicate = cb.and(cb.or(Status.SENT_TO_VERIFICATOR.getQueryPredicate(root, cb),
					Status.TEST_NOK.getQueryPredicate(root, cb),
					Status.TEST_OK.getQueryPredicate(root, cb)), queryPredicate);
        }

        queryPredicate = cb.and(cb.equal(joinSearch.get("id"), verificatorId), queryPredicate);

        if (dateToSearch != null) {
            DateTimeFormatter dbDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate date = null;
            try {
                date = LocalDate.parse(dateToSearch.substring(0, 10), dbDateTimeFormatter);
            } catch (Exception pe) {
                logger.error("Cannot parse date", pe);
            }
            queryPredicate = cb.and(cb.equal(root.get("initialDate"), java.sql.Date.valueOf(date)), queryPredicate);
        }



        if ((idToSearch != null)&&(idToSearch.length()>0)) {
            queryPredicate = cb.and(cb.like(root.get("id"), "%" + idToSearch + "%"), queryPredicate);
        }

        if (nameProvider != null && nameProvider.length() > 0) {
            queryPredicate = cb.and(
                    cb.like(root.get("provider").get("name"), "%" + nameProvider + "%"),
                    queryPredicate);
        }
        if ((nameCalibrator != null) && (nameCalibrator.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("calibrator").get("name"), "%" + nameCalibrator + "%"),
                    queryPredicate);
        }

        if ((numberOfCounter != null) && (numberOfCounter.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("counter").get("numberCounter"), "%" + numberOfCounter + "%"),
                    queryPredicate);
        }

        if ((numberOfProtocol != null) && (numberOfProtocol.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("numberOfProtocol"), "%" + numberOfProtocol + "%"),
                    queryPredicate);
        }

        if ((serialNumber != null) && (serialNumber.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("calibrationModule").get("serialNumber"), "%" + serialNumber + "%"),
                    queryPredicate);
        }

        return queryPredicate;
    }
}


