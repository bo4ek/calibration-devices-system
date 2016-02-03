package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.repository.OrganizationRepository;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.service.utils.EmployeeDTO;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Misha on 10/18/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CalibratorServiceImplTest {

    private CalibratorServiceImpl calibratorService;

    @Mock
    private OrganizationRepository calibratorRepository;

    @Mock
    private List listOfRole;

    @Spy
    private User user;

    @Spy
    private HashSet<User> setOfUser;

    @Mock
    private Organization organization;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() {
        calibratorService = new CalibratorServiceImpl();
        when(user.getUsername()).thenReturn("gag");
        when(user.getFirstName()).thenReturn("gag");
        when(user.getLastName()).thenReturn("gag");
        when(user.getMiddleName()).thenReturn("gag");
    }

    @Test(expected = IOException.class)
    public void testUploadBbiIncorrectIn()throws IOException {
        InputStream in = new FileInputStream("R:/Incorrect/");
        calibratorService.uploadBbi(in,"1","originalFileFullName.bbi");
    }

    @Test(expected = NullPointerException.class)
    public void testUploadBbiNullIn()throws IOException {
        calibratorService.uploadBbi(null,"1","originalFileFullName.bbi");
    }


    @Test
    public void testgetAllCalibratorEmployeeRoleCalibratorAdmin() {
        when(listOfRole.contains(UserRole.CALIBRATOR_ADMIN.name())).thenReturn(true);
        when(organization.getId()).thenReturn(11L);
        setOfUser.add(user);
        user.setOrganization(organization);
        when(userRepository.findAllAvailableUsersByEmployeeAndAdminRoleAndOrganizationId(UserRole.CALIBRATOR_EMPLOYEE, UserRole.CALIBRATOR_ADMIN, 11L))
                .thenReturn(setOfUser);
        calibratorService.setUserRepository(userRepository);
        List<EmployeeDTO> list = calibratorService.getAllCalibratorEmployee(listOfRole, user);
        assertNotEquals(0, list.size());
    }

    @Test
    public void testgetAllCalibratorEmployeeRoleCalibratorAdminNotNullList() {
        when(listOfRole.contains(UserRole.CALIBRATOR_ADMIN.name())).thenReturn(true);
        when(organization.getId()).thenReturn(11L);
        setOfUser.add(user);
        user.setOrganization(organization);
        when(userRepository.findAllAvailableUsersByRoleAndOrganizationId(UserRole.CALIBRATOR_EMPLOYEE, 11L))
                .thenReturn(setOfUser);
        calibratorService.setUserRepository(userRepository);
        List<EmployeeDTO> list = calibratorService.getAllCalibratorEmployee(listOfRole, user);
        assertNotNull(list);
    }

    @Test
    public void testgetAllCalibratorEmployeeNotCalibratorAdmin() {
        when(listOfRole.contains(anyString())).thenReturn(false);
        when(listOfRole.get(0)).thenReturn("CALIBRATOR_EMPLOYEE");
        List<EmployeeDTO> listEmployee = calibratorService.getAllCalibratorEmployee(listOfRole, user);
        assertEquals(1, listEmployee.size());
    }


}
