package com.softserve.edu.service;

import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.utils.NewVerificationsQueryConstructorProvider;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NewVerificationsQueryConstructorProviderTest {

    static Logger logger = Logger.getLogger(NewVerificationsQueryConstructorProvider.class);
    final static Long providerId = 1L;

    final static String dateToSearch = "dateToSearch";
    final static String idToSearch = "idToSearch";
    final static String lastNameToSearch = "yy";
    final static String streetToSearch = "streetToSearch";
    final static  String status = "TEST_OK";
    final static String employeeSearchName = "employeeSearchName";
    final static String userName = "userName";
    final static String sortCriteria = "str";
    final static String sortOrder = "str2";
    final static String sortCriteriaNull = null;
    final static String sortOrderNull = null;
    final static  String region = "region";
    final static  String district = "district";
    final static  String locality = "locality";

    @InjectMocks
    private NewVerificationsQueryConstructorProvider newVerificationsQueryConstructorProvider;

    @Mock
    private Root<Verification> root;

    @Mock
    CriteriaQuery<Long> countQuery;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Join<Object, Object> joinSearch;

    @Mock
    private User providerEmployee;

    @Mock
    private CriteriaQuery<Verification> criteriaQuery;

    @Mock
    private EntityManager em;

    @Mock
    private Predicate predicate;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuildSearchQuery() throws Exception {
       /*
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Verification.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Verification.class)).thenReturn(root);

        newVerificationsQueryConstructorProvider.buildSearchQuery(providerId, dateToSearch, idToSearch, lastNameToSearch,
                streetToSearch, region, district, locality, status, providerEmployee, sortCriteria, sortOrder, employeeSearchName, em);
        verify(em).getCriteriaBuilder();
        verify(cb).createQuery(Verification.class);
        verify(criteriaQuery.from(Verification.class));
        CriteriaQuery<Verification> query = newVerificationsQueryConstructorProvider.buildSearchQuery( providerId,  dateToSearch, idToSearch, lastNameToSearch,
                streetToSearch, status, providerEmployee, sortCriteria, sortOrder, employeeSearchName, em);
        Set<String> params = new HashSet<String>(Arrays.<String>asList( dateToSearch, idToSearch, lastNameToSearch,
                streetToSearch, status, sortCriteria, sortOrder, employeeSearchName));
        Assert.assertEquals(query.getParameters(), params);*/
    }

    @Test
    public void testBuildCountQuery() throws Exception {
   /* when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Long.class)).thenReturn(countQuery);
        when(criteriaQuery.from(Verification.class)).thenReturn(root);
        when(root.join("provider")).thenReturn(joinSearch);

        Method buildPredicate = newVerificationsQueryConstructorProvider.getClass().getDeclaredMethod("buildPredicate", Root.class, CriteriaBuilder.class, Join.class, Long.class,
                String.class, String.class, String.class, String.class, String.class, User.class, String.class
        );
        buildPredicate.setAccessible(true);
        when(buildPredicate.invoke(newVerificationsQueryConstructorProvider, root, cb, joinSearch, providerId,
                        dateToSearch, idToSearch, lastNameToSearch, streetToSearch, status, providerEmployee, employeeSearchName)
        ).thenReturn(predicate);

       doNothing().when(countQuery.select(cb.count(root)));*/
    }
}