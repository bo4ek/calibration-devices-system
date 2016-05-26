package com.softserve.edu.service.utils;

import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ArchivalVerificationsQueryConstructorVerificator.class})
public class ArchivalVerificationsQueryConstructorVerificatorTest {

    private Long verificatorId;
    private String dateToSearch;
    private String idToSearch;
    private String fullNameToSearch;
    private String streetToSearch;
    private String status;

    @Mock
    private User verificatorEmployee;
    private String sortCriteria;
    private String sortOrder;
    private String employeeSearchName;
    @Mock
    private EntityManager entityManager;
    @Mock
    CriteriaBuilder criteriaBuilder;
    @Mock
    Root<Verification> root;
    @Mock
    Join<Object, Object> join;
    @Mock
    CriteriaQuery<Verification> verificationCriteriaQuery;
    @Mock
    CriteriaQuery<Long> longCriteriaQuery;

    @Mock
    Predicate predicate;

    @Mock
    Path path;
    @InjectMocks
    ArchivalVerificationsQueryConstructorVerificator archivalVerificationsQueryConstructorVerificator;

    @Before
    public void setUp() throws Exception {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Verification.class)).thenReturn(verificationCriteriaQuery);
        when(verificationCriteriaQuery.from(Verification.class)).thenReturn(root);
        when(root.join("stateVerificator")).thenReturn(join);
        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(root.join("stateVerificatorEmployee", JoinType.LEFT)).thenReturn(join);
        when(criteriaBuilder.or(predicate, predicate)).thenReturn(predicate);
        when(criteriaBuilder.and(predicate)).thenReturn(predicate);
        when(root.get("clientData")).thenReturn(path);
        when(path.get(anyString())).thenReturn(path);
    }

    @Test
    public void testBuildSearchQueryWithAllParametrsSetNull() throws Exception {
        status = null;
        dateToSearch = null;
        idToSearch = null;
        fullNameToSearch = null;
        streetToSearch = null;
        employeeSearchName = null;
        when(criteriaBuilder.and(criteriaBuilder.or(Status.SENT_TO_VERIFICATOR.getQueryPredicate(root, criteriaBuilder),
                Status.TEST_NOK.getQueryPredicate(root, criteriaBuilder),
                Status.TEST_OK.getQueryPredicate(root, criteriaBuilder)), predicate)).thenReturn(predicate);
        when(criteriaBuilder.and(criteriaBuilder.equal(join.get("id"), verificatorId), predicate)).thenReturn(predicate);
        CriteriaQuery<Verification> expected = verificationCriteriaQuery;
        CriteriaQuery<Verification> actual = ArchivalVerificationsQueryConstructorVerificator
                .buildSearchQuery(verificatorId, dateToSearch,
                        idToSearch, fullNameToSearch, streetToSearch, status, employeeSearchName, sortCriteria,
                        sortOrder, verificatorEmployee, entityManager);
        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchQueryWithAllParametrsSetNotNull() throws Exception {
        status = "TEST_OK";
        when(criteriaBuilder.and(criteriaBuilder.equal(root.get("status"), Status.valueOf(status.trim())), predicate))
                .thenReturn(predicate);
        dateToSearch = "2015-10-27";
        idToSearch = "idToSearch";
        when(criteriaBuilder.and(criteriaBuilder.like(root.get("id"), "%" + idToSearch + "%"), predicate))
                .thenReturn(predicate);
        fullNameToSearch = "fullNameToSearch";
        sortCriteria = "id";
        sortOrder = "asc";
        when(criteriaBuilder.and(criteriaBuilder
                .like(root.get("clientData").get("lastName"), "%" + fullNameToSearch + "%"), predicate))
                .thenReturn(predicate);
        when(criteriaBuilder.like(root.get("clientData").get("firstName"), "%" + fullNameToSearch + "%"))
                .thenReturn(predicate);
        when(criteriaBuilder.like(root.get("clientData").get("lastName"), "%" + fullNameToSearch + "%"))
                .thenReturn(predicate);
        when(criteriaBuilder.like(root.get("clientData").get("middleName"), "%" + fullNameToSearch + "%"))
                .thenReturn(predicate);
        when(criteriaBuilder.or(predicate, predicate, predicate)).thenReturn(predicate);
        when(criteriaBuilder.and(predicate, predicate)).thenReturn(predicate);
        streetToSearch = "streetToSearch";
        when(criteriaBuilder.and(criteriaBuilder.
                like(root.get("clientData").get("clientAddress").get("street"), "%" + streetToSearch + "%"), predicate))
                .thenReturn(predicate);
        employeeSearchName = "employeeSearchName";
        when(root.join("stateVerificatorEmployee")).thenReturn(join);
        when(criteriaBuilder.like(join.get("firstName"), "%" + employeeSearchName + "%"))
                .thenReturn(predicate);
        when(criteriaBuilder.like(join.get("lastName"), "%" + employeeSearchName + "%"))
                .thenReturn(predicate);
        when(criteriaBuilder.like(join.get("middleName"), "%" + employeeSearchName + "%"))
                .thenReturn(predicate);
        when(criteriaBuilder.or(predicate, predicate, predicate))
                .thenReturn(predicate);
        when(criteriaBuilder.and(predicate, predicate))
                .thenReturn(predicate);

        CriteriaQuery<Verification> expected = verificationCriteriaQuery;
        CriteriaQuery<Verification> actual = ArchivalVerificationsQueryConstructorVerificator
                .buildSearchQuery(verificatorId, dateToSearch,
                        idToSearch, fullNameToSearch, streetToSearch, status, employeeSearchName, sortCriteria,
                        sortOrder, verificatorEmployee, entityManager);
        assertEquals(expected, actual);

    }

    @Test
    public void testBuildCountQuery() throws Exception {
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(longCriteriaQuery);
        when(longCriteriaQuery.from(Verification.class)).thenReturn(root);
        CriteriaQuery<Long> expected = longCriteriaQuery;
        CriteriaQuery<Long> actual = ArchivalVerificationsQueryConstructorVerificator
                .buildCountQuery(verificatorId, dateToSearch, idToSearch, fullNameToSearch,
                        streetToSearch, status, employeeSearchName, verificatorEmployee, entityManager);
        assertEquals(expected, actual);

    }
}