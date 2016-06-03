package com.softserve.edu.service.admin.impl;

import com.softserve.edu.entity.user.User;
import com.softserve.edu.repository.DeviceRepository;
import com.softserve.edu.repository.OrganizationRepository;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.repository.VerificationRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticServiceImplTest {
    private long expected;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private VerificationRepository verificationRepository;
    @Mock
    private User userExpected ;
    @InjectMocks
    private StatisticServiceImpl statisticServiceImpl;

    @Before
    public void initializeMockito() {
        MockitoAnnotations.initMocks(this);
        expected= 5L;
        userExpected = new User("Anton","0123");
    }

    @After
    public void tearDown() {
        userExpected = null;
        statisticServiceImpl = null;
    }

    @Test
    public void testCountOrganizations()  {
        when(organizationRepository.count()).thenReturn(expected);
        long factual = statisticServiceImpl.countOrganizations();
        assertEquals(factual, expected);
    }

    @Test
    public void testCountUsers(){
        when(userRepository.count()).thenReturn(expected);
        long factual = statisticServiceImpl.countUsers();
        assertEquals(factual, expected);
    }

    @Test
    public void testCountDevices()  {
        when(deviceRepository.count()).thenReturn(expected);
        long factual = statisticServiceImpl.countDevices();
        assertEquals(factual, expected);
    }

    @Test
    public void testCountVerifications()  {
        when(verificationRepository.count()).thenReturn(expected);
        long factual = statisticServiceImpl.countVerifications();
        assertEquals(factual, expected);
    }

    @Test
    public void testEmployeeExist()  {
        when(userRepository.findByUsernameIgnoreCase(anyString())).thenReturn(userExpected);
        User factual = statisticServiceImpl.employeeExist("Anton");
        assertEquals(factual, userExpected);
    }
}