package com.softserve.edu.service;

import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.service.utils.NewVerificationsQueryConstructorCalibrator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;

/**
 * Created by Roman on 25.10.2015.
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class NewVerificationsQueryConstructorCalibratorTest {

    // .buildSearchQuery() arguments
    private final Long providerId = 11L;
    private final String startDateToSearch = null; //"2015-04-20";
    private final String endDateToSearch = null; //"2015-05-20";
    private final String idToSearch = null; //"idToSearch";
    private final String fullNameToSearch = null; //"fullNameToSearch";
    private final String streetToSearch = null; //"streetToSearch";
    private final String region = null; //"region";
    private final String district = null; //"district";
    private final String locality = null; //"locality";
    private final String status = null; //"SENT";
    @Mock private User calibratorEmployee;
    private final String standardSize =null;
    private final String symbol = null;
    private final String nameProvider = null;
    private final String realiseYear = null;
    private final String sortCriteria = null; //"sortCriteria";
    private final String sortOrder = null; //"sortOrder";
    private final String employeeSearchName = null; //"employeeSearchName";
    @Mock private EntityManager em;
    private final String methodJoinArg = "calibrator";
    private final String dismantled=null;
    private final String building=null;
    private final String flat = null;

    // .buildSearchQuery() local variables' mocks
    @Mock private CriteriaBuilder cb;
    @Mock private CriteriaQuery<Verification> criteriaQuery;
    @Mock private Root<Verification> root;
    @Mock private Join<Object, Object> calibratorJoin;
    private final String userName = "userName";
    @Mock private Predicate queryPredicate;
    private final UserRole userRole = UserRole.CALIBRATOR_EMPLOYEE;
    private Set<UserRole> roles = new HashSet<UserRole>();
    @Mock private Join<Object, Object> joinCalibratorEmployee;
    @Mock private Predicate searchPredicateByUsername;
    @Mock private Predicate searchPredicateByEmptyField;
    @Mock private Predicate searchByCalibratorEmployee;

    @Before
    public void setUp() throws Exception {
        roles.add(userRole);
        // MockitoAnnotations.initMocks(this);
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
    }


    @Test
    public void testBuildSearchQueryWithSortCriteriaAndOrderSetToNull() throws Exception {
        NewVerificationsQueryConstructorCalibrator.buildSearchQuery(providerId, startDateToSearch,
                endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district,
                locality, status, calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled, building, flat, sortCriteria, sortOrder, employeeSearchName, em,null);
        verify(em).getCriteriaBuilder();
        verify(cb).createQuery(Verification.class);
        verify(criteriaQuery).from(Verification.class);
        verify(root).join(methodJoinArg);
        verify(criteriaQuery).select(root);
    }

    @Test
    public void testBuildCountQuery() throws Exception {

    }


    @Test
    public void testBuildPredicateWithArgumentsSetToNull() {
        NewVerificationsQueryConstructorCalibrator.buildSearchQuery(providerId, startDateToSearch,
                endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled, building, flat, sortCriteria, sortOrder, employeeSearchName, em,null);
        verify(calibratorEmployee).getUsername();
        verify(cb).conjunction();
        verify(calibratorEmployee).getUserRoles();

        // inside for loop of the private .buildPredicate() method
        verify(root, times(1)).join("calibratorEmployee", JoinType.LEFT);
        verify(cb, times(1)).equal(joinCalibratorEmployee.get("username"), userName);
        verify(cb, times(1)).isNull(joinCalibratorEmployee.get("username"));
        verify(cb, times(1)).or(searchPredicateByUsername, searchPredicateByEmptyField);
        verify(cb, times(1)).and(searchByCalibratorEmployee);

        /* verify(cb).and(cb.or(Status.IN_PROGRESS.getQueryPredicate(root, cb),
                Status.SENT_TO_TEST_DEVICE.getQueryPredicate(root, cb),
                Status.TEST_PLACE_DETERMINED.getQueryPredicate(root, cb),
                Status.TEST_COMPLETED.getQueryPredicate(root, cb)), queryPredicate); */
        // verify(cb).and(cb.equal(calibratorJoin.get("id"), providerId), queryPredicate);
    }
}