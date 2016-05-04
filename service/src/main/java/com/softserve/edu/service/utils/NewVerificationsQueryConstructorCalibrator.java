	package com.softserve.edu.service.utils;

	import com.softserve.edu.entity.organization.Organization;
	import com.softserve.edu.entity.verification.Verification;
	import com.softserve.edu.entity.user.User;
	import com.softserve.edu.entity.enumeration.user.UserRole;
	import com.softserve.edu.entity.enumeration.verification.Status;
    import com.softserve.edu.service.utils.filter.Filter;
    import org.apache.log4j.Logger;

	import javax.persistence.EntityManager;
	import javax.persistence.criteria.*;
	import java.time.LocalDate;
	import java.time.format.DateTimeFormatter;
    import java.util.*;

    /**
	 * @deprecated this class have a lot of repeated code <br/>
	 * {need to be replaced and removed}<br/>
	 * use {@link com.softserve.edu.specification.SpecificationBuilder} instead
	 */
	@Deprecated
	public class NewVerificationsQueryConstructorCalibrator {

		static Logger logger = Logger.getLogger(NewVerificationsQueryConstructorProvider.class);

		/**
		 * Method dynamically builds query to database depending on input parameters specified.
		 * @param providerId        search by organization ID
		 * @param startDateToSearch
		 * @param endDateToSearch
		 * @param idToSearch        search by verification ID
		 * @param fullNameToSearch
		 * @param streetToSearch    search by client's street
		 * @param standardSize      of counter
		 * @param nameProvider
		 * @param em
		 * @param globalSearchParams
		 */
		public static CriteriaQuery<Verification> buildSearchQuery(Long providerId, String startDateToSearch,
																   String endDateToSearch, String idToSearch, String fullNameToSearch, String streetToSearch, String region,
																   String district, String locality, String status,
																   User calibratorEmployee, String standardSize, String symbol, String nameProvider, String realiseYear, String dismantled, String building, String flat, String numberConter, String sortCriteria, String sortOrder, String employeeSearchName, EntityManager em, ArrayList<Map<String, Object>> globalSearchParams) {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
            Root<Verification> root = criteriaQuery.from(Verification.class);
            Join<Verification, Organization> calibratorJoin = root.join("calibrator");

            Predicate predicate = NewVerificationsQueryConstructorCalibrator.buildPredicate(root, cb, calibratorJoin, providerId, startDateToSearch, endDateToSearch,
					idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled, building, flat, numberConter, employeeSearchName);
			if((sortCriteria != null)&&(sortOrder != null)) {
                criteriaQuery.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
            } else {
                criteriaQuery.orderBy(cb.desc(root.get("sentToCalibratorDate")));
            }
            List<Predicate> predicates=new ArrayList<>();
            predicates.add(predicate);
            if(globalSearchParams!=null&&globalSearchParams.size()>0) {
                predicates.add(new Filter.FilterBuilder().setSearchList(globalSearchParams).build().toPredicate(root, criteriaQuery, cb));
            }
				criteriaQuery.select(root);
				criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
				return criteriaQuery;
		}

		/**
		 * Method dynamically builds query to database depending on input parameters specified.
		 * Needed to get max count of rows with current predicates for pagination
		 *
		 * @param startDateToSearch search by initial date of verification (optional)
		 * @param endDateToSearch
		 * @param idToSearch        search by verification ID
		 * @param fullNameToSearch
		 * @param streetToSearch    search by client's street
		 * @param em
		 */
		public static CriteriaQuery<Long> buildCountQuery(Long calibratorId, String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch, String streetToSearch, String region,
														  String district, String locality, String status,
														  User calibratorEmployee, String standardSize, String symbol, String nameProvider, String realiseYear, String dismantled, String building, String flat, String numberCounter, String employeeSearchName, EntityManager em) {
			
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
				Root<Verification> root = countQuery.from(Verification.class);
				Join<Verification, Organization> calibratorJoin = root.join("calibrator");
				Predicate predicate = NewVerificationsQueryConstructorCalibrator.buildPredicate(root, cb, calibratorJoin, calibratorId, startDateToSearch, endDateToSearch,
						idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled, building, flat, numberCounter, employeeSearchName);
				countQuery.select(cb.count(root));
				countQuery.where(predicate);
				return countQuery;
				}

		/**
		 * Method builds list of predicates depending on parameters passed
		 * Rule for predicates compounding - conjunction (AND)
		 * @param root
		 * @param cb
		 * @param joinSearch
		 * @param startDateToSearch
		 * @param endDateToSearch
		 * @param idToSearch
		 * @param fullNameToSearch
		 * @param streetToSearch  @return Predicate
		 */
	private static Predicate buildPredicate(Root<Verification> root, CriteriaBuilder cb, Join<Verification, Organization> joinSearch, Long calibratorId, String startDateToSearch, String endDateToSearch, String idToSearch,
											String fullNameToSearch, String streetToSearch, String region, String district, String locality, String status, User calibratorEmployee, String standardSize, String symbol, String nameProvider, String realiseYear, String dismantled, String building, String flat, String numberCounter, String employeeSearchName) {

		String userName = calibratorEmployee.getUsername();
		Predicate queryPredicate = cb.conjunction();
		Set<UserRole> roles = calibratorEmployee.getUserRoles();
		for (UserRole userRole : roles) {
			String role = userRole.name();
			if (role.equalsIgnoreCase("CALIBRATOR_EMPLOYEE")) {
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
			queryPredicate = cb.and(cb.or(Status.IN_PROGRESS.getQueryPredicate(root, cb),
					Status.SENT_TO_TEST_DEVICE.getQueryPredicate(root, cb),
                    Status.SENT_TO_DISMANTLING_TEAM.getQueryPredicate(root, cb),
					Status.TEST_PLACE_DETERMINED.getQueryPredicate(root, cb),
					Status.TEST_COMPLETED.getQueryPredicate(root, cb)), queryPredicate);
		}

		queryPredicate = cb.and(cb.equal(joinSearch.get("id"), calibratorId), queryPredicate);

		if (startDateToSearch != null && endDateToSearch != null) {
			DateTimeFormatter dbDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

			LocalDate startDate = null;
			LocalDate endDate = null;
			try {
				startDate = LocalDate.parse(startDateToSearch, dbDateTimeFormatter);
				endDate = LocalDate.parse(endDateToSearch, dbDateTimeFormatter);
			}
			catch (Exception pe) {
				logger.error("Cannot parse date", pe); //TODO: add exception catching
			}
			//verifications with date between these two dates
			queryPredicate = cb.and(cb.between(root.get("sentToCalibratorDate"), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate)), queryPredicate);

		}

		if ((idToSearch != null)&&(idToSearch.length()>0)) {
			queryPredicate = cb.and(cb.like(root.get("id"), "%" + idToSearch + "%"), queryPredicate);
		}

		if ((fullNameToSearch != null)&&(fullNameToSearch.length()>0)) {
            Predicate searchPredicateByClientFullName = cb.or(cb.like(root.get("clientData").get("lastName"), "%" + fullNameToSearch + "%"));
			queryPredicate = cb.and(searchPredicateByClientFullName, queryPredicate);
		}
		if ((streetToSearch != null)&&(streetToSearch.length()>0)) {
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

		if ((employeeSearchName != null)&&(employeeSearchName.length()>0)) {
			Join<Verification, User> joinCalibratorEmployee = root.join("calibratorEmployee");
			Predicate searchPredicateByCalibratorEmployeeName = cb.or(cb.like(joinCalibratorEmployee.get("lastName"),
                    "%" + employeeSearchName + "%"));
			queryPredicate = cb.and(searchPredicateByCalibratorEmployeeName, queryPredicate);
		}
        if ((standardSize != null) && (standardSize.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("counter").get("counterType").get("standardSize"), "%" + standardSize + "%"),
                    queryPredicate);
        }
        if ((symbol != null) && (symbol.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("counter").get("counterType").get("symbol"), "%" + symbol + "%"),
                    queryPredicate);
        }
		if ((numberCounter != null) && (numberCounter.length() > 0)) {
			queryPredicate = cb.and(
					cb.like(root.get("counter").get("numberCounter"), "%" + numberCounter + "%"),
					queryPredicate);
		}
		if ((nameProvider != null) && (nameProvider.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("provider").get("name"), "%" + nameProvider + "%"),
                    queryPredicate);
        }
        if ((realiseYear != null) && (realiseYear.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("counter").get("releaseYear"), "%" + realiseYear + "%"),
                    queryPredicate);
        }
        if ((dismantled != null && (dismantled.length() > 0))) {
            Boolean dismantledReceived = Boolean.valueOf(dismantled);
            if (dismantledReceived == true) {
                queryPredicate = cb.and(
                        cb.isTrue(root.get("counterStatus")), queryPredicate);
            } else {
                queryPredicate = cb.and(
                        cb.isFalse(root.get("counterStatus")), queryPredicate);
            }
        }
        if ((building != null) && (building.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("clientData").get("clientAddress").get("building"), "%" + building + "%"),
                    queryPredicate);
        }

        if ((flat != null) && (flat.length() > 0)) {
            queryPredicate = cb.and(
                    cb.like(root.get("clientData").get("clientAddress").get("flat"), "%" + flat + "%"),
                    queryPredicate);
        }

        return queryPredicate;
    }
}


