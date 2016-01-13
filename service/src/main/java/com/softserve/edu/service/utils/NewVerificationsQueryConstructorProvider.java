package com.softserve.edu.service.utils;

import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * @deprecated this class have a lot of repeated code <br/>
 * {need to be replaced and removed}<br/>
 * use {@link com.softserve.edu.specification.SpecificationBuilder} instead
 */
@Deprecated
public class NewVerificationsQueryConstructorProvider {

	static Logger logger = Logger.getLogger(NewVerificationsQueryConstructorProvider.class);
	
	/**
	 * Method dynamically builds query to database depending on input parameters specified.
	 * @param providerId
	 * 		search by organization ID
	 * @param startDateToSearch
 * 		search by initial date of verification (optional)
	 * @param endDateToSearch
	 * @param idToSearch
* 		search by verification ID
	 * @param fullNameToSearch
	 *@param streetToSearch
	 * 		search by client's street
	 * @param providerEmployee
* 		used to additional query restriction if logged user is simple employee (not admin)
	 * @param em
* 		EntityManager needed to have a possibility to create query
*    @return CriteriaQuery<Verification>
	 */
	public static CriteriaQuery<Verification> buildSearchQuery(Long providerId, String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch,
															   String streetToSearch, String region, String district, String locality, String status, User providerEmployee, String sortCriteria, String sortOrder, String employeeSearchName, EntityManager em) {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
			Root<Verification> root = criteriaQuery.from(Verification.class);
			Join<Verification, Organization> joinSearch = root.join("provider");

		Predicate predicate = NewVerificationsQueryConstructorProvider.buildPredicate(root, cb, joinSearch, providerId, startDateToSearch, endDateToSearch, idToSearch,
				fullNameToSearch, streetToSearch, region, district, locality, status, providerEmployee, employeeSearchName);

			if((sortCriteria != null)&&(sortOrder != null)) {
				criteriaQuery.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
			} else {
				criteriaQuery.orderBy(cb.desc(root.get("initialDate")));
			}
			criteriaQuery.select(root);
			criteriaQuery.where(predicate);
			return criteriaQuery;
	}

	
	/**
	 * Method dynamically builds query to database depending on input parameters specified. 
	 * Needed to get max count of rows with current predicates for pagination
	 * @param providerId
	 * 		search by organization ID
	 * @param startDateToSearch
 * 		search by initial date of verification (optional)
	 * @param endDateToSearch
	 * @param idToSearch
* 		search by verification ID
	 * @param fullNameToSearch
* 		search by client's last name
	 * @param streetToSearch
* 		search by client's street
	 * @param providerEmployee
* 		used to additional query restriction if logged user is simple employee (not admin)
	 * @param em        */
	public static CriteriaQuery<Long> buildCountQuery(Long providerId, String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch, String streetToSearch, String region, String district, String locality, String status,
													  User providerEmployee, String employeeSearchName, EntityManager em) {
		
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Verification> root = countQuery.from(Verification.class);
			Join<Verification, Organization> joinSearch = root.join("provider");
		Predicate predicate = NewVerificationsQueryConstructorProvider.buildPredicate(root, cb, joinSearch, providerId, startDateToSearch, endDateToSearch,
				idToSearch, fullNameToSearch, streetToSearch, region, district,
				locality, status, providerEmployee, employeeSearchName);
			countQuery.select(cb.count(root));
			countQuery.where(predicate);
			return countQuery;
	}
	/**
	 * Method builds list of predicates depending on parameters passed
	 * Rule for predicates compounding - conjunction (AND)
	 *
	 *  @param root
	 * @param cb
	 * @param joinSearch
	 * @param providerId
	 * @param startDateToSearch
	 * @param endDateToSearch
	 * @param idToSearch
	 * @param streetToSearch
	 * @param providerEmployee
	 * @return Predicate
	 */
	private static Predicate buildPredicate(Root<Verification> root, CriteriaBuilder cb, Join<Verification, Organization> joinSearch, Long providerId,
											String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch, String streetToSearch, String region, String district, String locality, String status, User providerEmployee, String employeeSearchName) {
	
		String userName = providerEmployee.getUsername();
		Predicate queryPredicate = cb.conjunction();
		Set<UserRole> roles= providerEmployee.getUserRoles();
			for (UserRole userRole : roles) {
				String role = userRole.name();
				if(role.equalsIgnoreCase("PROVIDER_EMPLOYEE")) {
					Join<Verification, User> joinSearchProviderEmployee = root.join("providerEmployee", JoinType.LEFT);
					Predicate searchPredicateByUsername =cb.equal(joinSearchProviderEmployee.get("username"), userName);
					Predicate searchPredicateByEmptyField = cb.isNull(joinSearchProviderEmployee.get("username"));
					Predicate searchPredicateByProviderEmployee=cb.or(searchPredicateByUsername,searchPredicateByEmptyField);
					queryPredicate=cb.and(searchPredicateByProviderEmployee);
				}
			}


		if (status != null) {
			queryPredicate = cb.and(cb.equal(root.get("status"), Status.valueOf(status.trim())), queryPredicate);
		} else {
			queryPredicate = cb.and(cb.or(Status.SENT.getQueryPredicate(root, cb), Status.ACCEPTED.getQueryPredicate(root, cb)), queryPredicate);
		}

		queryPredicate = cb.and(cb.equal(joinSearch.get("id"), providerId), queryPredicate);
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
		if ((employeeSearchName != null)&&(employeeSearchName.length()>0)) {
			Join<Verification, User> joinProviderEmployee = root.join("providerEmployee");
			Predicate searchByProviderName = cb.like(joinProviderEmployee.get("firstName"),"%" + employeeSearchName + "%");
			Predicate searchByProviderSurname = cb.like(joinProviderEmployee.get("lastName"),"%" + employeeSearchName + "%");
			Predicate searchByProviderLastName = cb.like(joinProviderEmployee.get("middleName"),"%" + employeeSearchName + "%");
			Predicate searchPredicateByProviderEmployeeName = cb.or(searchByProviderName, searchByProviderSurname, searchByProviderLastName);
			queryPredicate = cb.and(searchPredicateByProviderEmployeeName, queryPredicate);
		}
	
			return queryPredicate;
	}
	
}
