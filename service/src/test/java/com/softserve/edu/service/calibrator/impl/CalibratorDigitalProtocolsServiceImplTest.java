package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.calibrator.CalibratorDigitalProtocolsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Veronika 11.2015
 */

@RunWith(MockitoJUnitRunner.class)

public class CalibratorDigitalProtocolsServiceImplTest {

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private EntityManager em;

    @Mock
    private User user;

    @Mock
    private Verification verification;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    CriteriaQuery<Verification> cq;

    @Mock
    Root<Verification> verifications;

    @Mock
    TypedQuery<Verification> typedQuery;

    @Mock
    Path path;

    @InjectMocks
    CalibratorDigitalProtocolsService digitalProtocolsService = new CalibratorDigitalProtocolsServiceImpl();

    @Test
    public void testCountByCalibratorEmployee_usernameAndStatus() {
        Long expected = 1L;
        Status status = Status.TEST_COMPLETED;
        String username = "user";
        OngoingStubbing<Long> longOngoingStubbing = when(verificationRepository.countByCalibratorEmployeeUsernameAndStatus(username, status))
                .thenReturn(expected);
        when(user.getUsername()).thenReturn(username);

        Long actual = digitalProtocolsService.countByCalibratorEmployeeUsernameAndStatus(user, status);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFindPageOfVerificationsByCalibratorIdAndStatus() {

        List<Verification> expected = Collections.singletonList(verification);
        Status status = Status.TEST_COMPLETED;
        int pageNumber = 1;
        int itemsPerPage = 1;

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Verification.class)).thenReturn(cq);
        when(cq.from(Verification.class)).thenReturn(verifications);

        when(verifications.get("calibratorEmployee")).thenReturn(path);
        when(verifications.get("status")).thenReturn(path);

        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expected);

        List<Verification> actual = digitalProtocolsService.findPageOfVerificationsByCalibratorIdAndStatus(user, pageNumber, itemsPerPage, status);

        verify(cq).where(cb.and(cb.equal(path, user), cb.equal(path, status)));
        verify(typedQuery).setFirstResult((pageNumber - 1) * itemsPerPage);
        verify(typedQuery).setMaxResults(itemsPerPage);

        Assert.assertEquals(expected, actual);
    }
}