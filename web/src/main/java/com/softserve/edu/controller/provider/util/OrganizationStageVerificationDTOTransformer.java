package com.softserve.edu.controller.provider.util;


import com.softserve.edu.dto.provider.OrganizationStageVerificationDTO;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.VerificationGroup;
import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;

import java.util.Date;

public class OrganizationStageVerificationDTOTransformer {

    public static OrganizationStageVerificationDTO toDtoFromVerification(ClientData clientData,
                                                                         Address address, String verificationId, Organization calibrator, String comment,
                                                                         AdditionalInfo info, Boolean dismantled, Boolean sealPresence, Counter counter,
                                                                         Device device, Boolean verificationWithDismantle, CalibrationTask task, VerificationGroup group, Integer queue) {

        String calibratorName = (calibrator != null) ? calibrator.getName() : null;
        String entrance = (info == null || info.getEntrance() == 0) ? null : "" + info.getEntrance();
        String doorCode = (info == null || info.getDoorCode() == 0) ? null : "" + info.getDoorCode();
        String floor = (info == null || info.getFloor() == 0) ? null : "" + info.getFloor();
        Date dateOfVerif = (info != null && info.getDateOfVerif() != null) ? info.getDateOfVerif() : null;
        Boolean serviceability = (info != null) ? info.getServiceability() : null;
        Date noWaterToDate = (info != null && info.getNoWaterToDate() != null) ? info.getNoWaterToDate() : null;
        String notes = (info != null) ? info.getNotes() : null;
        String timeFrom = (info != null && info.getTimeFrom() != null) ? info.getTimeFrom().toString() : null;
        String timeTo = (info != null && info.getTimeTo() != null) ? info.getTimeTo().toString() : null;

        Date dateOfDismantled = (counter != null && counter.getDateOfDismantled() != null) ? counter.getDateOfDismantled() : null;
        Date dateOfMounted = (counter != null && counter.getDateOfMounted() != null) ? counter.getDateOfMounted() : null;
        String numberCounter = (counter != null) ? counter.getNumberCounter() : null;
        String releaseYear = (counter != null) ? counter.getReleaseYear() : null;
        String accumulatedVolume = (counter != null) ? counter.getAccumulatedVolume() : null;
        String symbol = (counter != null && counter.getCounterType() != null) ? counter.getCounterType().getSymbol() : null;
        String standardSize = (counter != null && counter.getCounterType() != null) ? counter.getCounterType().getStandardSize() : null;
        String deviceName = device.getDeviceName();
        Device.DeviceType deviceType = device.getDeviceType();
        Long deviceId = device.getId();
        Long taskId = (task != null) ? task.getId() : null;
        Long groupId = group != null ? group.getId() : null;


        return new OrganizationStageVerificationDTO(clientData, comment, address, verificationId, calibratorName, entrance, doorCode, floor, dateOfVerif,
                serviceability, noWaterToDate, notes, timeFrom, timeTo, dismantled, dateOfDismantled, dateOfMounted, numberCounter,
                releaseYear, accumulatedVolume, symbol, standardSize, deviceName, sealPresence, deviceType, deviceId, verificationWithDismantle, taskId, groupId, queue);
    }
}
