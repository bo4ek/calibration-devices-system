package com.softserve.edu.controller.calibrator.util;

import com.softserve.edu.dto.calibrator.ProtocolDTO;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.verification.Verification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProtocolDTOTransformer {

    public static List<ProtocolDTO> toDTOFromList(List<Verification> verifications, List<String> numbersOfProtocolsFromBbi, Set<UserRole> userRoles) {
        List<ProtocolDTO> resultList = new ArrayList<>();
        int i = 0;
        boolean isVerificator = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm");
        for(UserRole userRole : userRoles) {
            if(userRole.equals(UserRole.STATE_VERIFICATOR_ADMIN) || userRole.equals(UserRole.STATE_VERIFICATOR_EMPLOYEE)) {
                isVerificator = true;
            }
        }
        for (Verification verification : verifications) {
            while (i < numbersOfProtocolsFromBbi.size()) {
                resultList.add(new ProtocolDTO(
                        verification.getId(),
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
                                .getCalibrationTestManual().getGenerateNumberTest() : numbersOfProtocolsFromBbi.get(i),
                        verification.getProviderEmployee(), verification.getStateVerificatorEmployee(),
                        isVerificator ? dateFormat.format(verification.getSentToVerificatorDate()) : null
                ));
                i++;
                break;
            }
        }
        return resultList;
    }
}
