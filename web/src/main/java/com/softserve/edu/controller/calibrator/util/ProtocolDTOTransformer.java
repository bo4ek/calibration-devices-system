package com.softserve.edu.controller.calibrator.util;

import com.softserve.edu.dto.calibrator.ProtocolDTO;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;

import java.util.ArrayList;
import java.util.List;

public class ProtocolDTOTransformer {

    public static List<ProtocolDTO> toDTOFromList(List<Verification> verifications, List<String> numbersOfProtocolsFromBbi) {
        List<ProtocolDTO> resultList = new ArrayList<>();
        int i = 0;
        for (Verification verification : verifications) {
            while (i < numbersOfProtocolsFromBbi.size()) {
                resultList.add(new ProtocolDTO(
                        verification.getId(),
                        verification.getProvider().getName(),
                        verification.getCalibrator().getName(),
                        verification.getStatus().toString(),
                        verification.getComment(),
                        verification.isManual(),
                        verification.isManual() ? verification.getCalibrationTestDataManualId()
                                .getCalibrationTestManual().getCalibrationModule().getModuleNumber() : verification.getCalibrationModule().getModuleNumber(),
                        Integer.parseInt(verification.getCounter().getReleaseYear()),
                        verification.getCounter().getNumberCounter(),
                        verification.getInitialDate().toString(), verification.getReadStatus(),
                        verification.isManual() ? verification.getCalibrationTestDataManualId()
                                .getCalibrationTestManual().getCalibrationModule().getSerialNumber() : verification.getCalibrationModule().getSerialNumber(),
                        verification.isManual() ? verification.getCalibrationTestDataManualId()
                                .getCalibrationTestManual().getGenerateNumberTest() : numbersOfProtocolsFromBbi.get(i)
                ));
                i++;
                break;
            }
        }
        return resultList;
    }
}
