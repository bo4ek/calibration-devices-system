package com.softserve.edu.service.utils;

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
public class ArchivalVerificationsQueryConstructorProvider {
static Logger logger = Logger.getLogger(ArchivalVerificationsQueryConstructorProvider.class);


	public static CriteriaQuery<Verification> buildSearchQuery(Long employeeId, String initialDateToSearch,
															   String endDateToSearch, String idToSearch, String fullNameToSearch,
															   String streetToSearch, String region, String district, String locality, String status,
															   String employeeName, String sortCriteria, String sortOrder,
															   User providerEmployee, EntityManager em) {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
			Root<Verification> root = criteriaQuery.from(Verification.class);
			Join<Verification, Organization> providerJoin = root.join("provider");
		Predicate predicate = ArchivalVerificationsQueryConstructorProvider.buildPredicate(root, cb, employeeId, initialDateToSearch, endDateToSearch, idToSearch, fullNameToSearch,  streetToSearch, region, district, locality,
				status, employeeName, providerEmployee, providerJoin);
			
			if((sortCriteria != null)&&(sortOrder != null)) {
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
													  User providerEmployee, EntityManager em) {
		
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Verification> root = countQuery.from(Verification.class);
			Join<Verification, Organization> providerJoin = root.join("provider");
		Predicate predicate = ArchivalVerificationsQueryConstructorProvider.buildPredicate(root, cb, employeeId, initialDateToSearch, endDateToSeach, idToSearch,
				fullNameToSearch, streetToSearch, region, district, locality, status, employeeName,
				providerEmployee, providerJoin);
			countQuery.select(cb.count(root));
			countQuery.where(predicate);
			return countQuery;
			}
	
	private static Predicate buildPredicate(Root<Verification> root, CriteriaBuilder cb, Long providerId,
											String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch,
											String streetToSearch, String region, String district, String locality, String searchStatus, String employeeName, User employee,
											Join<Verification, Organization> providerJoin) {

		Predicate queryPredicate = cb.conjunction();		
		queryPredicate = cb.and(cb.equal(providerJoin.get("id"), providerId), queryPredicate);
		
		if (searchStatus != null) {
			queryPredicate = cb.and(cb.equal(root.get("status"), Status.valueOf(searchStatus.trim())), queryPredicate);
		}
		else {
            queryPredicate = cb.and(cb.or(
                    Status.TEST_OK.getQueryPredicate(root, cb),
                    Status.TEST_NOK.getQueryPredicate(root, cb)
            ), queryPredicate);
        }

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
			queryPredicate = cb.and(cb.between(root.get("initialDate"), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate)), queryPredicate);

		}

		if ((idToSearch != null)&&(idToSearch.length()>0)) {
			queryPredicate = cb.and(cb.like(root.get("id"), "%" + idToSearch + "%"), queryPredicate);
		}

		if ((fullNameToSearch != null)&&(fullNameToSearch.length()>0)) {
			Predicate searchByClientFirstName = cb.like(root.get("clientData").get("firstName"), "%" + fullNameToSearch + "%");
			Predicate searchByClientLastName = cb.like(root.get("clientData").get("lastName"), "%" + fullNameToSearch + "%");
			Predicate searchByClientMiddleName = cb.like(root.get("clientData").get("middleName"), "%" + fullNameToSearch + "%");
			Predicate searchPredicateByClientFullName = cb.or(searchByClientFirstName, searchByClientLastName, searchByClientMiddleName);
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

		if ((employeeName != null)&&(employeeName.length()>0)) {
			Join<Verification, User> joinProviderEmployee = root.join("providerEmployee");
			Predicate searchByProviderName = cb.like(joinProviderEmployee.get("firstName"), "%" + employeeName + "%");
			Predicate searchByProviderSurname = cb.like(joinProviderEmployee.get("lastName"), "%" + employeeName + "%");
			Predicate searchByProviderLastName = cb.like(joinProviderEmployee.get("middleName"),
					"%" + employeeName + "%");
			Predicate searchPredicateByProviderEmployeeName = cb.or(searchByProviderName, searchByProviderSurname,
					searchByProviderLastName);
			queryPredicate = cb.and(searchPredicateByProviderEmployeeName, queryPredicate);
		}

		return queryPredicate;
	}
}
