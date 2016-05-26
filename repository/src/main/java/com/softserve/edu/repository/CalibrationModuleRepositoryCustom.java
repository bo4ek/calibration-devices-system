package com.softserve.edu.repository;

import com.softserve.edu.entity.device.CalibrationModule;

public interface CalibrationModuleRepositoryCustom {

    CalibrationModule saveWithGenerating(CalibrationModule calibrationModule);

}
