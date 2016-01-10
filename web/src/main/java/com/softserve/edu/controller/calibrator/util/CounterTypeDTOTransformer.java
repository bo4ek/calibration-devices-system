package com.softserve.edu.controller.calibrator.util;


import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.dto.admin.CounterTypeDTO;

import java.util.ArrayList;
import java.util.List;

public class CounterTypeDTOTransformer {

    public static List<CounterTypeDTO> toDtofromList(List<CounterType> list) {
        List<CounterTypeDTO> resultList = new ArrayList<>();
        for (CounterType counterType : list) {
            resultList.add(new CounterTypeDTO(
                    counterType.getId(),
                    counterType.getName(),
                    counterType.getSymbol(),
                    counterType.getStandardSize(),
                    counterType.getManufacturer(),
                    counterType.getCalibrationInterval(),
                    counterType.getYearIntroduction(),
                    counterType.getGost(),
                    counterType.getDevice().getId()
            ));
        }
        return resultList;
    }

    public static List<CounterTypeDTO> toDtofromListLight(List<CounterType> list) {
        List<CounterTypeDTO> resultList = new ArrayList<>();
        for (CounterType counterType : list) {
            resultList.add(new CounterTypeDTO(
                    counterType.getId(),
                    counterType.getSymbol(),
                    counterType.getStandardSize(),
                    counterType.getDevice().getDeviceType().toString(),
                    counterType.getManufacturer()
            ));
        }
        return resultList;
    }



}
