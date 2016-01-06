package com.softserve.edu.service.admin.impl;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import com.softserve.edu.repository.CalibrationModuleRepository;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.service.admin.CalibrationModuleService;
import com.softserve.edu.service.utils.filter.Filter;
import com.softserve.edu.service.utils.filter.internal.Comparison;
import com.softserve.edu.service.utils.filter.internal.Condition;
import com.softserve.edu.service.utils.filter.internal.Type;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Created by Pavlo on 02.11.2015.
 */
@Service
public class CalibrationModuleServiceImpl implements CalibrationModuleService {
    @Autowired
    CalibrationModuleRepository calibrationModuleRepository;
    @Autowired
    UserRepository userRepository;
    private Logger logger = Logger.getLogger(CalibrationModule.class);

    public CalibrationModule addCalibrationModule(CalibrationModule calibrationModule) {
        if (calibrationModule == null) {
            throw new NullPointerException("Adding null pointer to calibration modules");
        } else return calibrationModuleRepository.saveWithGenerating(calibrationModule);
    }

    public CalibrationModule findModuleById(Long calibrationModuleId) {
        return calibrationModuleRepository.findOne(calibrationModuleId);
    }

    public void deleteCalibrationModule(Long moduleId) {
        calibrationModuleRepository.delete(moduleId);
    }

    public void disableCalibrationModule(Long calibrationModuleId) {
        CalibrationModule calibrationModule = calibrationModuleRepository.findOne(calibrationModuleId);
        calibrationModule.setIsActive(false);
        calibrationModuleRepository.save(calibrationModule);
    }

    public void enableCalibrationModule(Long calibrationModuleId) {
        CalibrationModule calibrationModule = calibrationModuleRepository.findOne(calibrationModuleId);
        calibrationModule.setIsActive(true);
        calibrationModuleRepository.save(calibrationModule);
    }


    public Page<CalibrationModule> getFilteredPageOfCalibrationModule(Map<String, Object> searchKeys, Pageable pageable) {
        return calibrationModuleRepository.findAll(new Filter.FilterBuilder().setSearchMap(searchKeys).build(), pageable);
    }

    public Page<CalibrationModule> findAllModules(Pageable pageable) {
        return calibrationModuleRepository.findAll(pageable);
    }

    public List<CalibrationModule> findAllModules() {
        return calibrationModuleRepository.findAll();
    }

    @Override
    public List<CalibrationModule> findAllActing() {
        return calibrationModuleRepository.findAllActing();
    }


    public void updateCalibrationModule(Long moduleId, CalibrationModule calibrationModule) {
        CalibrationModule changedCalibrationModule = calibrationModuleRepository.findOne(moduleId);
        changedCalibrationModule.updateFields(calibrationModule);
        calibrationModuleRepository.save(changedCalibrationModule);
    }

    public List<String> findAllSerialNumbers(CalibrationModule.ModuleType moduleType,
                                             Date workDate, Device.DeviceType deviceType,
                                             String userName) {
        Filter filter = new Filter();
        List<Condition> conditions = new ArrayList<>();
        String organizationCode = userRepository.findOne(userName)
                .getOrganization().getAdditionInfoOrganization().getCodeEDRPOU();
        List<String> NumbersList = new ArrayList<>();
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.eq).setType(Type.enumerated).setField("moduleType").setValue(moduleType).build());
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.gt).setField("workDate").setType(Type.date).setValue(workDate).build());
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.eq).setField("isActive").setValue(true).build());
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.eq).setField("organizationCode").setValue(organizationCode).build());
        filter.addConditionList(conditions);
        List<CalibrationModule> modules = calibrationModuleRepository.findAll(filter);
        if (modules != null) {
            for (CalibrationModule module : modules) {
                if (module.getDeviceType().contains(deviceType)) {
                    NumbersList.add(module.getSerialNumber());
                }
            }
        }
        return NumbersList;
    }

    @Override
    @Transactional(readOnly = true)
    public Date getEarliestDate() {
        return calibrationModuleRepository.findEarliestDateAvailableCalibrationModule();
    }

}