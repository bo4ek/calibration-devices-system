package com.softserve.edu.repository.impl;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.repository.CalibrationModuleRepository;
import com.softserve.edu.repository.CalibrationModuleRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roman on 13.11.15.
 *
 */

@Repository
public class CalibrationModuleRepositoryImpl implements CalibrationModuleRepositoryCustom {

    @Autowired
    CalibrationModuleRepository calibrationModuleRepository;

    public CalibrationModule saveWithGenerating(CalibrationModule calibrationModule) {
        calibrationModuleRepository.save(calibrationModule);
        generateModuleNumber(calibrationModule);
        return calibrationModuleRepository.save(calibrationModule);
    }

    private void generateModuleNumber(CalibrationModule calibrationModule) {
        Map<Device.DeviceType, Integer> moduleNumbers = new HashMap<>();
        Device.DeviceType[] deviceTypes = Device.DeviceType.values();
        for (int i = 0; i < deviceTypes.length; i++) {
            moduleNumbers.put(deviceTypes[i], i + 1);
        }

        Device.DeviceType deviceType = (Device.DeviceType) calibrationModule.getDeviceType().toArray()[0];
        if (deviceType.getId() == 1) {
            calibrationModule.setModuleNumber(String.format("%1d%03d", moduleNumbers.get(deviceType) + 1, calibrationModule.getModuleId()));
        } else if (deviceType.getId() == 2) {
            calibrationModule.setModuleNumber(String.format("%1d%03d", moduleNumbers.get(deviceType) - 1, calibrationModule.getModuleId()));
        }
    }
}