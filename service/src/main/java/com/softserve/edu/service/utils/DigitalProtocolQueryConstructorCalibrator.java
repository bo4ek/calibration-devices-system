	package com.softserve.edu.service.utils;

	import com.softserve.edu.entity.enumeration.user.UserRole;
	import com.softserve.edu.entity.enumeration.verification.Status;
	import com.softserve.edu.entity.organization.Organization;
	import com.softserve.edu.entity.user.User;
	import com.softserve.edu.entity.verification.Verification;
	import com.softserve.edu.service.utils.filter.Filter;
	import org.apache.log4j.Logger;

	import javax.persistence.EntityManager;
	import javax.persistence.criteria.*;
	import java.time.LocalDate;
	import java.time.format.DateTimeFormatter;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Map;
	import java.util.Set;

	/**
     * @deprecated this class have a lot of repeated code <br/>
     * {need to be replaced and removed}<br/>
     * use {@link com.softserve.edu.specification.SpecificationBuilder} instead
     */
    @Deprecated
    public class DigitalProtocolQueryConstructorCalibrator {

        static Logger logger = Logger.getLogger(NewVerificationsQueryConstructorProvider.class);

		/**
		 * Method dynamically builds query to database depending on input parameters specified.
		 * Needed to get max count of rows with current predicates for pagination
		 * @param verificatorID - search by organization ID
		 * @param dateToSearch - search by date
		 * @param idToSearch  - search by id
		 * @param status - search by status
		 * @param verificatorEmployee - search by verificatorEmployee
		 * @param nameProvider - search by nameProvider
		 * @param nameCalibrator - search by nameCalibrator
		 * @param numberOfCounter - search by numberOfCounter
		 * @param numberOfProtocol - search by numberOfProtocol
		 * @param sentToVerificatorDate - search by sentToVerificatorDate
		 * @param serialNumber - search by serialNumber
		 * @param em
		 * @return
		 */
        public static CriteriaQuery<Long> buildCountQuery(Long verificatorID, String dateToSearch, String idToSearch, String status,
                                                          User verificatorEmployee, String nameProvider, String nameCalibrator, String numberOfCounter,
                                                          String numberOfProtocol, String sentToVerificatorDate, String serialNumber, EntityManager em) {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Verification> root = countQuery.from(Verification.class);
            Join<Verification, Organization> verificatorJoin = root.join("calibrator");
            Predicate predicate = DigitalProtocolQueryConstructorCalibrator.buildPredicate(root, cb, verificatorJoin, verificatorID, dateToSearch, idToSearch,
					status, verificatorEmployee, nameProvider, nameCalibrator, numberOfCounter,
					numberOfProtocol, sentToVerificatorDate, serialNumber);
            countQuery.select(cb.count(root));
            countQuery.where(predicate);
            return countQuery;
        }

		/**
		 * Method dynamically builds query to database depending on input parameters specified.
		 * @param verificatorID - search by organization ID
		 * @param dateToSearch - search by date
		 * @param idToSearch  - search by id
		 * @param status - search by status
		 * @param verificatorEmployee - search by verificatorEmployee
		 * @param nameProvider - search by nameProvider
		 * @param nameCalibrator - search by nameCalibrator
		 * @param numberOfCounter - search by numberOfCounter
		 * @param numberOfProtocol - search by numberOfProtocol
		 * @param sentToVerificatorDate - search by sentToVerificatorDate
		 * @param serialNumber - search by serialNumber
		 * @param em
		 * @return
		 */
        public static CriteriaQuery<Verification> buildSearchQuery(Long verificatorID, String dateToSearch, String idToSearch, String status,
                                                                   User verificatorEmployee,  String nameProvider,String nameCalibrator, String numberOfCounter,
                                                                   String numberOfProtocol, String sentToVerificatorDate, String serialNumber, String sortCriteria, String sortOrder, EntityManager em) {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
            Root<Verification> root = criteriaQuery.from(Verification.class);
            Join<Verification, Organization> verificatorJoin = root.join("calibrator");

            Predicate predicate = DigitalProtocolQueryConstructorCalibrator.buildPredicate(root, cb, verificatorJoin, verificatorID, dateToSearch, idToSearch,
					status, verificatorEmployee, nameProvider, nameCalibrator, numberOfCounter,
					numberOfProtocol, sentToVerificatorDate, serialNumber);

            if ((sortCriteria.equals("default")) && (sortOrder.equals("default"))) {
                criteriaQuery.orderBy(cb.desc(root.get("initialDate")), cb.asc((root.get("calibrationModule").get("serialNumber"))),
                        cb.asc(root.get("numberOfProtocol")));
            } else if ((sortCriteria != null) && (sortOrder != null)) {
                criteriaQuery.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
            }
            criteriaQuery.select(root);
            criteriaQuery.where(predicate);

            return criteriaQuery;
        }

		/**
		 * Method builds list of predicates
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
                if(role.equalsIgnoreCase("CALIBRATOR_EMPLOYEE")) {
                    Join<Verification, User> joinCalibratorEmployee = root.join("calibratorEmployee", JoinType.LEFT);
                    Predicate searchPredicateByUsername = cb.equal(joinCalibratorEmployee.get("username"), userName);
                    Predicate searchPredicateByEmptyField = cb.isNull(joinCalibratorEmployee.get("username"));
                    Predicate searchByCalibratorEmployee = cb.or(searchPredicateByUsername, searchPredicateByEmptyField);
                    queryPredicate = cb.and(searchByCalibratorEmployee);
                }
            }

            if (status != null) {
                queryPredicate = cb.and(cb.equal(root.get("status"), Status.valueOf(status.trim())), queryPredicate);
            } else {
                queryPredicate = cb.and(Status.TEST_COMPLETED.getQueryPredicate(root, cb), queryPredicate);
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


