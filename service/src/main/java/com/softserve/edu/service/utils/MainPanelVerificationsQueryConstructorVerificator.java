package com.softserve.edu.service.utils;


import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

public class MainPanelVerificationsQueryConstructorVerificator {

    static Logger logger = Logger.getLogger(MainPanelVerificationsQueryConstructorVerificator.class);


    public static CriteriaQuery<Verification> buildSearchQuery(Long organizationId, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> criteriaQuery = cb.createQuery(Verification.class);
        Root<Verification> root = criteriaQuery.from(Verification.class);
        Join<Verification, Organization> verificatorJoin = root.join("stateVerificator");
        Join<Verification, User> stateVerificatorJoin = root.join("stateVerificatorEmployee", JoinType.LEFT);

        Predicate predicate = MainPanelVerificationsQueryConstructorVerificator.buildPredicate(root, cb, organizationId, verificatorJoin,
                stateVerificatorJoin);
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        return criteriaQuery;
    }


    public static CriteriaQuery<Long> buildCountQuery(Long organizationId, EntityManager em) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Verification> root = countQuery.from(Verification.class);
        Join<Verification, Organization> verificatorJoin = root.join("stateVerificator");
        Join<Verification, User> stateVerificatorJoin = root.join("stateVerificatorEmployee", JoinType.LEFT);
        Predicate predicate = MainPanelVerificationsQueryConstructorVerificator.buildPredicate(root, cb, organizationId, verificatorJoin, stateVerificatorJoin);
        countQuery.select(cb.count(root));
        countQuery.where(predicate);
        return countQuery;
    }

    private static Predicate buildPredicate(Root<Verification> root, CriteriaBuilder cb, Long organizationId, Join<Verification, Organization> verificatorJoin,
                                            Join<Verification, User> stateVerificatorJoin) {

        Predicate queryPredicate = cb.conjunction();
        queryPredicate = cb.and(cb.equal(verificatorJoin.get("id"), organizationId), queryPredicate);

        queryPredicate = cb.and(Status.SENT_TO_VERIFICATOR.getQueryPredicate(root, cb), queryPredicate);
        queryPredicate = cb.and(cb.isNull(stateVerificatorJoin.get("username")), queryPredicate);

        return queryPredicate;
    }
}
