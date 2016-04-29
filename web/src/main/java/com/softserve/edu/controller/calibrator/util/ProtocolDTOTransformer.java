package com.softserve.edu.controller.calibrator.util;

import com.softserve.edu.dto.calibrator.ProtocolDTO;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.verification.Verification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProtocolDTOTransformer {

    public static List<ProtocolDTO> toDTOFromList(List<Verification> verifications, Set<UserRole> userRoles) {
        List<ProtocolDTO> resultList = new ArrayList<>();
        boolean isVerificator = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm");
        for(UserRole userRole : userRoles) {
            if(userRole.equals(UserRole.STATE_VERIFICATOR_ADMIN) || userRole.equals(UserRole.STATE_VERIFICATOR_EMPLOYEE)) {
                isVerificator = true;
            }
        }
        for (Verification verification : verifications) {
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
                        verification.getVerificationTime(), verification.getReadStatus(),
                        verification.getCalibrationModule().getSerialNumber(),
                        verification.getNumberOfProtocol(),
                        verification.getProviderEmployee(), verification.getStateVerificatorEmployee(),
                        isVerificator ? dateFormat.format(verification.getSentToVerificatorDate()) : null,
                        verification.getRejectedMessage()
                ));
        }
        return resultList;
    }
}
