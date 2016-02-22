package com.softserve.edu.service.utils;

import com.softserve.edu.entity.catalogue.District;
import com.softserve.edu.entity.catalogue.Locality;
import com.softserve.edu.entity.catalogue.Region;
import com.softserve.edu.entity.catalogue.Street;
import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

public class StreetsQueryConstructor {

    public static CriteriaQuery<Street> buildSearchQuery(String city, String district, String region,
                                                       String streetName, String sortCriteria, String sortOrder,
                                                       EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Street> criteriaQuery = cb.createQuery(Street.class);
        Root<Street> root = criteriaQuery.from(Street.class);
        Join<Street, Locality> localityJoin = root.join("locality");
        Join<Locality, District> districtJoin = localityJoin.join("district");
        Join<District, Region> regionJoin = districtJoin.join("region");
        Predicate predicate = StreetsQueryConstructor.buildPredicate(root, cb, localityJoin, districtJoin, regionJoin,
                city, district, region, streetName);

        if (!((sortCriteria.equals("undefined")) || (sortOrder.equals("undefined")))) {
            criteriaQuery.orderBy(SortCriteriaStreet.valueOf(sortCriteria.toUpperCase()).getSortOrder(root, localityJoin,
                    districtJoin, regionJoin, cb, sortOrder));
        }
        criteriaQuery.select(root).distinct(true);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }

    public static CriteriaQuery<Long> buildCountQuery(String city, String district, String region,
                                                      String streetName, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Street> root = countQuery.from(Street.class);
        Join<Street, Locality> localityJoin = root.join("locality");
        Join<Locality, District> districtJoin = localityJoin.join("district");
        Join<District, Region> regionJoin = districtJoin.join("region");
        Predicate predicate = StreetsQueryConstructor.buildPredicate(root, cb, localityJoin, districtJoin, regionJoin,
                city, district, region, streetName);

        countQuery.select(cb.countDistinct(root));
        countQuery.where(predicate);
        return countQuery;
    }

    private static Predicate buildPredicate(Root<Street> root, CriteriaBuilder cb,Join<Street, Locality> localityJoin,
                                            Join<Locality, District> districtJoin, Join<District, Region> regionJoin,
                                            String city, String district, String region, String streetName) {
        Predicate queryPredicate = cb.conjunction();

        if (StringUtils.isNotEmpty(city)) {
            queryPredicate = cb.and(cb.like(localityJoin.get("designation"), "%" + city + "%"), queryPredicate);
        }
        if (StringUtils.isNotEmpty(district)) {
            queryPredicate = cb.and(cb.like(districtJoin.get("designation"), "%" + district + "%"), queryPredicate);
        }
        if (StringUtils.isNotEmpty(region)) {
            queryPredicate = cb.and(cb.like(regionJoin.get("designation"), "%" + region + "%"), queryPredicate);
        }
        if (StringUtils.isNotEmpty(streetName)) {
            queryPredicate = cb.and(cb.like(root.get("designation"), "%" + streetName + "%"), queryPredicate);
        }
        return queryPredicate;
    }
}
