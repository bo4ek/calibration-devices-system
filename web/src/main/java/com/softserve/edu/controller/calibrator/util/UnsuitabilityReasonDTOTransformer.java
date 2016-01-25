package com.softserve.edu.controller.calibrator.util;

import com.softserve.edu.dto.admin.UnsuitabilityReasonDTO;
import com.softserve.edu.entity.device.UnsuitabilityReason;

import java.util.ArrayList;
import java.util.List;

public class UnsuitabilityReasonDTOTransformer {

    public static List<UnsuitabilityReasonDTO> toDTOFromList(List<UnsuitabilityReason> unsuitabilityReasons) {
        List<UnsuitabilityReasonDTO> resultList = new ArrayList<>();
        for (UnsuitabilityReason unsuitabilityReason : unsuitabilityReasons) {
            resultList.add(new UnsuitabilityReasonDTO(
                    unsuitabilityReason.getId(),
                    unsuitabilityReason.getName()
            ));
        }
        return resultList;
    }
}
