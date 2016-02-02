package com.softserve.edu.controller.calibrator.util;

import com.softserve.edu.dto.calibrator.ProtocolDTO;
import com.softserve.edu.entity.verification.Verification;

import java.util.ArrayList;
import java.util.List;

public class ProtocolDTOTransformer {

    public static List<ProtocolDTO> toDtofromList(List<Verification> verifications) {
        List<ProtocolDTO> resultList = new ArrayList<>();

        for(Verification verification : verifications) {
            resultList.add(new ProtocolDTO(
                    verification.getId(),
                    verification.getSentToCalibratorDate().toString(),
                    verification.getClientData().getFirstName(),
                    verification.getClientData().getLastName(),
                    verification.getClientData().getMiddleName(),
                    verification.getClientData().getClientAddress().getAddress(),
                    verification.getProvider().getName(),
                    verification.getCalibrator().getName(),
                    verification.getStatus().toString(),
                    verification.getComment()
            ));
        }
        return resultList;
    }
}
