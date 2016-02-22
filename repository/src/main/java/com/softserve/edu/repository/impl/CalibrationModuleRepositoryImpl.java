package com.softserve.edu.repository.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.repository.CalibrationModuleRepository;
import com.softserve.edu.repository.CalibrationModuleRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by roman on 13.11.15.
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

        List<Integer> listOfWater = new ArrayList<>();
        List<Integer> listOfThermal = new ArrayList<>();

        listOfWater.add(Constants.MODULE_WATER_NUMBER_INITIAL);
        listOfThermal.add(Constants.MODULE_THERMAL_NUMBER_INITIAL);

        for (CalibrationModule oneCalibrationModule : calibrationModuleRepository.findAll()) {
            if (oneCalibrationModule.getModuleNumber() != null) {
                if (oneCalibrationModule.getModuleNumber().startsWith(Integer.toString(Constants.MODULE_WATER_ID))) {
                    listOfWater.add(Integer.parseInt(oneCalibrationModule.getModuleNumber()));
                }else if (oneCalibrationModule.getModuleNumber().startsWith(Integer.toString(Constants.MODULE_THERMAL_ID))) {
                    listOfThermal.add(Integer.parseInt(oneCalibrationModule.getModuleNumber()));
                }
            }
        }

        Collections.sort(listOfWater);
        Collections.sort(listOfThermal);

        Integer moduleNumber = null;
        Set<Device.DeviceType> setOfDeviceType = calibrationModule.getDeviceType();
        if (setOfDeviceType.size() > 1) {
            moduleNumber = listOfThermal.get(listOfThermal.size() - 1) + Constants.MODULE_ID_INCREMENT;
            calibrationModule.setModuleNumber(moduleNumber.toString());
        } else {
            for (Device.DeviceType deviceType : setOfDeviceType) {
                switch (deviceType.getId()) {
                    case (1): {
                        moduleNumber = listOfWater.get(listOfWater.size() - 1) + Constants.MODULE_ID_INCREMENT;
                        calibrationModule.setModuleNumber(moduleNumber.toString());
                        break;
                    }
                    default: {
                        moduleNumber = listOfThermal.get(listOfThermal.size() - 1) + Constants.MODULE_ID_INCREMENT;
                        calibrationModule.setModuleNumber(moduleNumber.toString());
                        break;
                    }
                }
            }
        }
    }
}