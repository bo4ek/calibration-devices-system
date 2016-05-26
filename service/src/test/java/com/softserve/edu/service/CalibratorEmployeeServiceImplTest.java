package com.softserve.edu.service;

import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.repository.OrganizationRepository;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.calibrator.impl.CalibratorEmployeeServiceImpl;
import com.softserve.edu.service.provider.buildGraphic.GraphicBuilder;
import com.softserve.edu.service.provider.buildGraphic.GraphicBuilderMainPanel;
import com.softserve.edu.service.provider.buildGraphic.MonthOfYear;
import com.softserve.edu.service.provider.buildGraphic.ProviderEmployeeGraphic;
import com.softserve.edu.service.utils.EmployeeDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EmployeeDTO.class, CalibratorEmployeeServiceImpl.class, GraphicBuilder.class,
        GraphicBuilderMainPanel.class})
public class CalibratorEmployeeServiceImplTest {

    // mocking fields
    @Mock private UserRepository calibratorEmployeeRepository;
    @Mock private VerificationRepository verificationRepository;
    @Mock private OrganizationRepository organizationRepository;

    @InjectMocks
    private CalibratorEmployeeServiceImpl calibratorEmployeeService;

    // User fields
    private final String PASSWORD = "pass";
    private final String USERNAME = "usr";
    private final String FIRST_NAME = "firstName";
    private final String LAST_NAME = "lastName";
    private final String MIDDLE_NAME = "middleName";

    // mocking method parameters and local variables
    private List<String> role;
    private final Long organizationId = 123L;
    @Mock private User calibratorEmployee;
    @Mock private Organization organization;
    @Mock private Set<User> userSet;
    @Mock private Stream<User> userStream;
    @Mock private List<User> userList;
    @Mock private ArrayList<EmployeeDTO> calibratorListEmployee;
    @Mock private EmployeeDTO userPage;
    @Mock private Date from;
    @Mock private Date to;
    @Mock private List<Verification> verifications;
    @Mock private List<MonthOfYear> monthList;
    @Mock private List<ProviderEmployeeGraphic> graficData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(EmployeeDTO.class);
        PowerMockito.mockStatic(GraphicBuilder.class);
        PowerMockito.mockStatic(GraphicBuilderMainPanel.class);
        role = new ArrayList<String>();
        when(calibratorEmployee.getPassword()).thenReturn(PASSWORD);
        when(calibratorEmployee.getUsername()).thenReturn(USERNAME);
        when(calibratorEmployee.getFirstName()).thenReturn(FIRST_NAME);
        when(calibratorEmployee.getLastName()).thenReturn(LAST_NAME);
        when(calibratorEmployee.getMiddleName()).thenReturn(MIDDLE_NAME);
        when(calibratorEmployeeRepository.findOne(USERNAME)).thenReturn(calibratorEmployee);
        when(calibratorEmployee.getOrganization()).thenReturn(organization);
        when(organization.getId()).thenReturn(organizationId);
        when(calibratorEmployeeRepository.findAllAvailableUsersByRoleAndOrganizationId(UserRole.CALIBRATOR_EMPLOYEE,
                organizationId)).thenReturn(userSet);
        when(userSet.stream()).thenReturn(userStream);
        // when(EmployeeDTO.giveListOfEmployeeDTOs(userList)).thenReturn(calibratorListEmployee);
        when(organizationRepository.findOne(organizationId)).thenReturn(organization);
        when(verificationRepository.findByCalibratorAndSentToCalibratorDateBetween(organization, from, to))
                .thenReturn(verifications);
        try {
            when(GraphicBuilder.listOfMonths(from, to)).thenReturn(monthList);
            when(GraphicBuilderMainPanel.builderData(verifications, monthList, organization))
                    .thenReturn(graficData);
        } catch(ParseException e) {}

    }

    @Test
    public void testAddEmployee() {
        calibratorEmployeeService.addEmployee(calibratorEmployee);
        verify(calibratorEmployee).getPassword();
        verify(calibratorEmployeeRepository).save(calibratorEmployee);
    }

    @Test
    public void testOneCalibratorEmployee() {
        User actual = calibratorEmployeeService.oneCalibratorEmployee(USERNAME);
        User expected = calibratorEmployee;
        verify(calibratorEmployeeRepository).findOne(USERNAME);
        assertEquals("the users returned from the method are not equal", expected, actual);
    }

    @Test
    public void testGetAllCalibratorsWhenListOfRolesContainsCalibratorAdmin() {
        Collections.addAll(role, "CALIBRATOR_ADMIN", "CALIBRATOR_EMPLOYEE");
        calibratorEmployeeService.getAllCalibrators(role, calibratorEmployee);
        verify(calibratorEmployee).getOrganization();
        verify(organization).getId();
        verify(calibratorEmployeeRepository).findAllAvailableUsersByRoleAndOrganizationId(UserRole.CALIBRATOR_EMPLOYEE,
                organizationId);
        verify(userSet).stream();
    }

    @Test
    public void testGetAllCalibratorsWithoutCalibratorAdminInListOfRoles() {
        Collections.addAll(role, "CALIBRATOR_EMPLOYEE");
        try {
            PowerMockito.whenNew(ArrayList.class).withNoArguments().thenReturn(calibratorListEmployee);
            PowerMockito.whenNew(EmployeeDTO.class).withArguments(USERNAME, FIRST_NAME, LAST_NAME,
                    MIDDLE_NAME, role.get(0)).thenReturn(userPage);
        } catch (Exception e) {}
        calibratorEmployeeService.getAllCalibrators(role, calibratorEmployee);
        verify(calibratorEmployee).getUsername();
        verify(calibratorEmployee).getFirstName();
        verify(calibratorEmployee).getLastName();
        verify(calibratorEmployee).getMiddleName();
        verify(calibratorListEmployee).add(userPage);
    }

    @Test
    public void testBuildGraphicMainPanel() {
        calibratorEmployeeService.buidGraphicMainPanel(from, to, organizationId);
        verify(organizationRepository).findOne(organizationId);
        verify(verificationRepository).findByCalibratorAndSentToCalibratorDateBetween(organization, from, to);
        try {
            PowerMockito.verifyStatic();
            GraphicBuilder.listOfMonths(from, to);
            PowerMockito.verifyStatic();
            GraphicBuilderMainPanel.builderData(verifications, monthList, organization);
        } catch(ParseException e) {}
    }
}