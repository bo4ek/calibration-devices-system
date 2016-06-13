package com.softserve.edu.service.utils;

import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.enumeration.organization.OrganizationType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.print.DocFlavor;

/**
 * @deprecated this class have a lot of repeated code <br/>
 * {need to be replaced and removed}<br/>
 * use {@link com.softserve.edu.specification.SpecificationBuilder} instead
 */
@Deprecated
public class ArchivalOrganizationsQueryConstructorAdmin {
    static Logger logger = Logger.getLogger(ArchivalOrganizationsQueryConstructorAdmin.class);

    public static CriteriaQuery<Organization> buildSearchQuery(String id, String name, String email, String phone, String type, String region, String district, String locality, String streetToSearch, String sortCriteria, String sortOrder, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Organization> criteriaQuery = cb.createQuery(Organization.class);
        Root<Organization> root = criteriaQuery.from(Organization.class);

        Predicate predicate = ArchivalOrganizationsQueryConstructorAdmin.buildPredicate(id, name, email, type, phone, region, district, locality, streetToSearch, root, cb);
        if ((sortCriteria != null) && (sortOrder != null)) {
            criteriaQuery.orderBy(SortCriteriaOrganization.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, cb, sortOrder));
        } else {
            criteriaQuery.orderBy(cb.desc(root.get("id")));
        }
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }

    public static CriteriaQuery<Long> buildCountQuery(String id, String name,
                                                      String email, String phone, String type, String region, String district, String locality, String streetToSearch, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Organization> root = countQuery.from(Organization.class);

        Predicate predicate = ArchivalOrganizationsQueryConstructorAdmin.buildPredicate(id, name, email, type, phone, region, district, locality, streetToSearch, root, cb);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }

    private static Predicate buildPredicate(String idToSearch, String name,
                                            String email, String phone, String type, String region, String district, String locality, String streetToSearch, Root<Organization> root, CriteriaBuilder cb) {
        Predicate queryPredicate = cb.conjunction();

        if (StringUtils.isNotEmpty(idToSearch)) {
            queryPredicate = cb.and(cb.equal(root.get("id"), Long.valueOf(idToSearch)), queryPredicate);
        }

        if (StringUtils.isNotEmpty(name)) {
            queryPredicate = cb.and(cb.like(root.get("name"), "%" + name + "%"), queryPredicate);
        }

        if (StringUtils.isNotEmpty(email)) {
            queryPredicate = cb.and(cb.like(root.get("email"), "%" + email + "%"), queryPredicate);
        }
        if (StringUtils.isNotEmpty(phone)) {
            queryPredicate = cb.and(
                    cb.like(root.get("phone"), "%" + phone + "%"), queryPredicate);
        }
        if (StringUtils.isNotEmpty(type)) {
            OrganizationType organizationType = OrganizationType.valueOf(type.trim());
            queryPredicate = cb.and(cb.isMember(organizationType, root.get("organizationTypes")), queryPredicate);
        }
        if (StringUtils.isNotEmpty(region)) {
            queryPredicate = cb.and(cb.like(root.get("address").get("region"), "%" + region + "%"), queryPredicate);
        }
        if (StringUtils.isNotEmpty(district)) {
            queryPredicate = cb.and(cb.like(root.get("address").get("district"), "%" + district + "%"), queryPredicate);
        }
        if (StringUtils.isNotEmpty(locality)) {
            queryPredicate = cb.and(cb.like(root.get("address").get("locality"), "%" + locality + "%"), queryPredicate);
        }
        if (StringUtils.isNotEmpty(streetToSearch)) {
            queryPredicate = cb.and(cb.like(root.get("address").get("street"), "%" + streetToSearch + "%"), queryPredicate);
        }
        return queryPredicate;
    }
}
