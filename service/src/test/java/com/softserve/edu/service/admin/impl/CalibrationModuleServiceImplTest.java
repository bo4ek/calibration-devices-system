package com.softserve.edu.service.admin.impl;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.AdditionInfoOrganization;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import com.softserve.edu.repository.CalibrationModuleRepository;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.service.utils.filter.Filter;
import com.softserve.edu.service.utils.filter.internal.Comparison;
import com.softserve.edu.service.utils.filter.internal.Condition;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({CalibrationModuleServiceImpl.class, Filter.class, CalibrationModuleRepository.class})
//@RunWith(MockitoJUnitRunner.class)
public class CalibrationModuleServiceImplTest {
    /*@Mock
    CalibrationModuleRepository calibrationModuleRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CalibrationModule calibrationModule;
    @Mock
    Pageable pageable;
    @Mock
    Filter filter;
    @Mock
    User user;
    @Mock
    Page<CalibrationModule> calibrationModulePage;
    @Mock
    List<CalibrationModule> calibrationModulesList;
    @Mock
    Organization organization;
    @Mock
    Logger logger;
    @Mock
    Filter.FilterBuilder filterBuilder;
    private Long id;
    @Mock
    AdditionInfoOrganization additionalInfo;
    @Mock
    CalibrationTask calibrationTask;

    @InjectMocks
    CalibrationModuleServiceImpl calibrationModuleService;

    @Before
    public void setUp() throws Exception {
        when(calibrationModuleRepository.save(calibrationModule)).thenReturn(calibrationModule);
        when(calibrationModuleRepository.findOne(id)).thenReturn(calibrationModule);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Test
    public void testAddCalibrationModuleCatchException() throws Exception {
        thrown.expect(NullPointerException.class);
        calibrationModuleService.addCalibrationModule(null);
    }

    @Test
    public void testAddCalibrationModule() {
        when(calibrationModuleRepository.saveWithGenerating(calibrationModule)).thenReturn(calibrationModule);
        CalibrationModule expected = calibrationModule;
        CalibrationModule actual = calibrationModuleService.addCalibrationModule(calibrationModule);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindModuleById() throws Exception {
        CalibrationModule expected = calibrationModule;
        CalibrationModule actual = calibrationModuleService.findModuleById(id);
        assertEquals(expected, actual);
    }

    @Test
    public void testDisableCalibrationModule() throws Exception {
        calibrationModuleService.disableCalibrationModule(id);
        assertFalse(calibrationModuleService.findModuleById(id).getIsActive());
    }

    @Test
    public void testGetFilteredPageOfCalibrationModule() throws Exception {
        PowerMockito.whenNew(Filter.FilterBuilder.class).withNoArguments().thenReturn(filterBuilder);
        Map<String, Object> searchKeys = new HashMap<>();
        searchKeys.put("isActive", "true");
        searchKeys.put("employeeFullName", "fullName");
        when(filterBuilder.setSearchMap(searchKeys)).thenReturn(filterBuilder);
        when(filterBuilder.setSearchMap(searchKeys).build()).thenReturn(filter);
        when(calibrationModuleRepository.findAll(filter, pageable)).thenReturn(calibrationModulePage);
        Page<CalibrationModule> expected = calibrationModulePage;
        Page<CalibrationModule> actual = calibrationModuleService.getFilteredPageOfCalibrationModule(searchKeys, pageable);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindAllModules() throws Exception {
        when(calibrationModuleRepository.findAll(pageable)).thenReturn(calibrationModulePage);
        Page expected = calibrationModulePage;
        Page actual = calibrationModuleService.findAllModules(pageable);
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateCalibrationModule() throws Exception {
        calibrationModuleService.updateCalibrationModule(id, calibrationModule);
        verify(calibrationModule).updateFields(calibrationModule);
    }

    @Test
    public void testFindAllCalibrationModuleNumbers() throws Exception {
        CalibrationModule.ModuleType moduleType = CalibrationModule.ModuleType.INSTALLATION_FIX;
        Date workDate = new Date();
        Date dateOfTask = new Date(1322688571000L);
        Device.DeviceType deviceType = Device.DeviceType.THERMAL;
        String username = "username";
        String moduleNumber = "moduleNumber";
        String codeEDRPOU = "code";
        Long organizationId = 100L;
        List<Condition> conditions = new ArrayList<>();
        List<CalibrationModule> modules = new ArrayList<>();
        Set<CalibrationTask> tasks = new HashSet<>();
        tasks.add(calibrationTask);
        modules.add(calibrationModule);
        when(user.getOrganization()).thenReturn(organization);
        when(user.getOrganization().getId()).thenReturn(organizationId);
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.like).setField("moduleType").setValue(moduleType).build());
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.eq).setField("workDate").setValue(workDate).build());
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.eq).setField("deviceType").setValue(deviceType).build());
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.eq).setField("organizationCode").setValue(user.getOrganization().getId())
                .build());
        filter.addConditionList(conditions);
        calibrationModulesList.add(calibrationModule);
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(user);
        PowerMockito.whenNew(Filter.class).withNoArguments().thenReturn(filter);
        when(calibrationModuleRepository.findAll(filter)).thenReturn(modules);
        when(calibrationModule.getSerialNumber()).thenReturn(moduleNumber);
        when(user.getOrganization()).thenReturn(organization);
        when(organization.getAdditionInfoOrganization()).thenReturn(additionalInfo);
        when(additionalInfo.getCodeEDRPOU()).thenReturn(codeEDRPOU);
        when(calibrationModule.getTasks()).thenReturn(tasks);
        when(calibrationTask.getDateOfTask()).thenReturn(dateOfTask);
        List<String> expected = new ArrayList<String>();
        expected.add(moduleNumber);
        List<String> actual = calibrationModuleService.findAllSerialNumbers(moduleType, workDate, deviceType, username);
        assertEquals(expected, actual);
    }

    @Test
    public void testfindAllCalibrationModuleNumbersCatchUserNullException() {
        thrown.expect(NullPointerException.class);
        CalibrationModule.ModuleType moduleType = CalibrationModule.ModuleType.INSTALLATION_FIX;
        Date workDate = new Date();
        Device.DeviceType deviceType = Device.DeviceType.THERMAL;
        String username = "username";
        String serialNumber = "serialNumber";
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(null);
        calibrationModuleService.findAllSerialNumbers(moduleType, workDate, deviceType, username);
        verify(logger).error("Cannot found user!");
    }

    @Test
    public void testFindAllCalibrationModulesNumbersCatchNoModulesException() throws Exception {
        thrown.expect(NullPointerException.class);
        CalibrationModule.ModuleType moduleType = CalibrationModule.ModuleType.INSTALLATION_FIX;
        Date workDate = new Date();
        Device.DeviceType deviceType = Device.DeviceType.THERMAL;
        String username = "username";
        Long organizationId = 100L;
        when(user.getOrganization()).thenReturn(organization);
        when(user.getOrganization().getId()).thenReturn(organizationId);
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(user);
        PowerMockito.whenNew(Filter.class).withNoArguments().thenReturn(filter);
        when(calibrationModuleRepository.findAll(filter)).thenReturn(null);
        calibrationModuleService.findAllSerialNumbers(moduleType, workDate, deviceType, username);
        verify(logger).error("Cannot found modules for the choosen workDate " + workDate);
    }*/
}