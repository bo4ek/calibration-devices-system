package com.softserve.edu.service.utils;

import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Yurko on 25.10.2015.
 */
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(NewVerificationsQueryConstructorCalibrator.class)
public class NewVerificationsQueryConstructorCalibratorTest {

    // region fields

    // region mocks

    @Mock
    private User calibratorEmployee;
    @Mock
    private EntityManager em;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private CriteriaQuery<Verification> criteriaQuery;
    @Mock
    private CriteriaQuery<Long> countQuery;
    @Mock
    private Root<Verification> root;
    @Mock
    private Join<Object, Object> calibratorJoin;
    @Mock
    private Join<Object, Object> joinCalibratorEmployee;
    @Mock
    private Predicate searchPredicateByUsername;
    @Mock
    private Predicate searchPredicateByEmptyField;
    @Mock
    private Predicate searchByCalibratorEmployee;
    @Mock
    private Predicate queryPredicate;
    @Mock
    private Path path;

    // endregion

    private Long calibratorId = null;
    private Long providerId = 11L;
    private String startDateToSearch = null;
    private String endDateToSearch = null;
    private String idToSearch = null;
    private String fullNameToSearch = null;
    private String streetToSearch = null;
    private String region = null;
    private String district = null;
    private String locality = null;
    private String status = null;
    private String sortCriteria = null;
    private String sortOrder = null;
    private String employeeSearchName = null;
    private String standardSize = null;
    private String symbol = null;
    private String nameProvider = null;
    private String  realiseYear = null;
    private String methodJoinArg = "calibrator";
    private String userName = "username";
    private UserRole userRole = UserRole.CALIBRATOR_EMPLOYEE;
    private Set<UserRole> roles = new TreeSet<>();
    private String dismantled = null;
    private String building =null;
    private String flat =null;

    // endregion

    @Before
    public void setUp() throws Exception {
        roles.add(userRole);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Verification.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Verification.class)).thenReturn(root);
        when(root.join(methodJoinArg)).thenReturn(calibratorJoin);
        when(calibratorEmployee.getUsername()).thenReturn(userName);
        when(cb.conjunction()).thenReturn(queryPredicate);
        when(calibratorEmployee.getUserRoles()).thenReturn(roles);
        when(root.join("calibratorEmployee", JoinType.LEFT)).thenReturn(joinCalibratorEmployee);
        when(cb.equal(joinCalibratorEmployee.get("username"), userName)).thenReturn(searchPredicateByUsername);
        when(cb.isNull(joinCalibratorEmployee.get("username"))).thenReturn(searchPredicateByEmptyField);
        when(cb.or(searchPredicateByUsername, searchPredicateByEmptyField)).thenReturn(searchByCalibratorEmployee);
        when(cb.and(searchByCalibratorEmployee)).thenReturn(queryPredicate);
        when(cb.and(cb.or(Status.IN_PROGRESS.getQueryPredicate(root, cb),
                Status.SENT_TO_TEST_DEVICE.getQueryPredicate(root, cb),
                Status.TEST_PLACE_DETERMINED.getQueryPredicate(root, cb),
                Status.TEST_COMPLETED.getQueryPredicate(root, cb)), queryPredicate)).thenReturn(queryPredicate);
        when(cb.and(cb.equal(calibratorJoin.get("id"), providerId), queryPredicate)).thenReturn(queryPredicate);

        when(cb.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(Verification.class)).thenReturn(root);
    }

    @Test
    public void testBuildSearchQuery() throws Exception {
        providerId = 1L;
        startDateToSearch = LocalDate.of(2015, 9, 5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        endDateToSearch = LocalDate.of(2015, 9, 10).format(DateTimeFormatter.ISO_LOCAL_DATE);

        CriteriaQuery<Verification> actual = NewVerificationsQueryConstructorCalibrator.buildSearchQuery(
                providerId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch,
                streetToSearch, region, district, locality, status,
                calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled,  building, flat, sortCriteria, sortOrder, employeeSearchName, em, null);


        verify(em.getCriteriaBuilder().createQuery(Verification.class)).from(Verification.class);
        verify(criteriaQuery).from(Verification.class);
        verify(root).join("calibrator");
        verify(criteriaQuery).select(root);
        assertEquals(criteriaQuery, actual);
    }

    @Test
    public void test1BuildSearchQuery() throws Exception {
        providerId = 1L;
        startDateToSearch = LocalDate.of(2015, 9, 5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        endDateToSearch = LocalDate.of(2015, 9, 10).format(DateTimeFormatter.ISO_LOCAL_DATE);
        sortCriteria = "id";
        sortOrder = "asc";

        CriteriaQuery<Verification> actual = NewVerificationsQueryConstructorCalibrator.buildSearchQuery(
                providerId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch,
                streetToSearch, region, district, locality, status,
                calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled,  building, flat, sortCriteria, sortOrder, employeeSearchName, em,null);

        verify(em.getCriteriaBuilder().createQuery(Verification.class)).from(Verification.class);
        verify(criteriaQuery).from(Verification.class);
        verify(root).join("calibrator");
        verify(criteriaQuery).select(root);
        assertEquals(criteriaQuery, actual);
    }

    @Test
    public void testBuildCountQuery() throws Exception {
        calibratorId = 1L;
        startDateToSearch = LocalDate.of(2015, 9, 5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        endDateToSearch = LocalDate.of(2015, 9, 10).format(DateTimeFormatter.ISO_LOCAL_DATE);
        idToSearch = "";
        fullNameToSearch = "";
        streetToSearch = "";
        status = "SENT";
        employeeSearchName = "";

        CriteriaQuery<Long> actual = NewVerificationsQueryConstructorCalibrator.buildCountQuery(
                calibratorId, startDateToSearch, endDateToSearch, idToSearch,
                fullNameToSearch, streetToSearch, region, district,
                locality, status, calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled, building, flat, employeeSearchName, em);

        verify(em.getCriteriaBuilder().createQuery(Long.class), atLeastOnce()).from(Verification.class);
        verify(cb, atLeastOnce()).createQuery(Long.class);
        verify(countQuery).from(Verification.class);
        verify(root).join("calibrator");
        verify(countQuery).select(cb.count(root));
        assertEquals(countQuery, actual);
    }

    @Test
    public void testBuildPredicate_withBuildCountQuery() throws Exception {
        calibratorId = 1L;
        startDateToSearch = "123abc";
        endDateToSearch = "xyz789";
        idToSearch = "";
        fullNameToSearch = "";
        streetToSearch = "";

        boolean actual;
        try {
            NewVerificationsQueryConstructorCalibrator.buildCountQuery(
                    calibratorId, startDateToSearch, endDateToSearch, idToSearch,
                    fullNameToSearch, streetToSearch, region, district,
                    locality, status, calibratorEmployee, symbol, nameProvider, realiseYear, dismantled, building, flat, standardSize, employeeSearchName, em);
            actual = true;
        } catch (Exception ex) { // TODO: Exception handling should be written
            actual = false;
        }
        assertEquals(false, actual);
    }

    @Test
    public void test1BuildPredicate_withBuildCountQuery() throws Exception {
        /*idToSearch = "u1";
        fullNameToSearch = "john";
        streetToSearch = "zelena";
        region = "b13";
        district = "d15";
        employeeSearchName = "jack";

        when(root.get("clientData")).thenReturn(path);
        when(root.get("clientData").get("firstName")).thenReturn(path);
        when(path.get(anyString())).thenReturn(path);

        when(cb.like(joinCalibratorEmployee.get("firstName"), "%" + employeeSearchName + "%")).
                thenReturn(queryPredicate);
        when(cb.like(any(Expression.class), anyString())).thenReturn(queryPredicate);
        when(cb.like(root.get("clientData").get("firstName"), "%" + fullNameToSearch + "%")).
                thenReturn(queryPredicate);
        when(cb.like(root.get("clientData").get("lastName"), "%" + fullNameToSearch + "%")).
                thenReturn(queryPredicate);
        when(cb.like(root.get("clientData").get("middleName"), "%" + fullNameToSearch + "%")).
                thenReturn(queryPredicate);


        NewVerificationsQueryConstructorCalibrator.buildCountQuery(
                calibratorId, startDateToSearch, endDateToSearch, idToSearch,
                fullNameToSearch, streetToSearch, region, district,
                locality, status, calibratorEmployee, employeeSearchName, em);*/
    }
}