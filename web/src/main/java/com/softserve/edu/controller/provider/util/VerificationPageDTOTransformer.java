package com.softserve.edu.controller.provider.util;

import com.softserve.edu.dto.calibrator.VerificationPlanningTaskDTO;
import com.softserve.edu.dto.provider.RejectedVerificationsProviderPageDTO;
import com.softserve.edu.dto.provider.VerificationPageDTO;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;


import java.util.*;

public class VerificationPageDTOTransformer {

    public static List<VerificationPageDTO> toDtoFromList(List<Verification> list) {

        List<VerificationPageDTO> resultList = new ArrayList<>();
        CalibrationTest calibrationTest;
        for (Verification verification : list) {
            boolean isCalibrationTests = verification.getCalibrationTests().iterator().hasNext();
            if (isCalibrationTests) {
                calibrationTest = verification.getCalibrationTests().iterator().next();
            } else {
                calibrationTest = null;
            }
            VerificationPageDTO verificationPageDTO = new VerificationPageDTO(
                    verification.getId(),
                    verification.getInitialDate(),
                    verification.getClientData().getLastName(),
                    verification.getClientData().getClientAddress().getStreet(),
                    verification.getClientData().getClientAddress().getRegion(),
                    verification.getStatus(),
                    verification.getReadStatus(),
                    verification.getProviderEmployee(),
                    verification.getCalibratorEmployee(),
                    verification.getStateVerificatorEmployee(),
                    verification.getClientData().getFirstName(),
                    verification.getClientData().getFullName(),
                    verification.getClientData().getClientAddress().getDistrict(),
                    verification.getClientData().getClientAddress().getLocality(),
                    verification.getClientData().getPhone(),
                    verification.getBbiProtocols() == null ? false : true,
                    verification.getProcessTimeExceeding(),
                    calibrationTest,
                    verification.getDevice(),
                    null,
                    null,
                    verification.getClientData().getClientAddress().getAddress(),
                    verification.getClientData().getClientAddress().getBuilding(),
                    verification.getClientData().getClientAddress().getFlat(),
                    verification.getComment()
            );

            if (verification.getProvider() != null) {
                verificationPageDTO.setNameProvider(verification.getProvider().getName());
            }
            if (verification.getCalibrator() != null) {
                verificationPageDTO.setNameCalibrator(verification.getCalibrator().getName());
            }
            Set<CounterType> set = (verification.getDevice() != null) ? verification.getDevice().getCounterTypeSet() : null;
            verificationPageDTO.setIsManual(verification.isManual());
            if (verification.getCounter() != null && verification.getCounter().getCounterType() != null) {
                verificationPageDTO.setSymbol(verification.getCounter().getCounterType().getSymbol());
                verificationPageDTO.setStandardSize(verification.getCounter().getCounterType().getStandardSize());
                if (verification.getCounter().getReleaseYear() != null) {
                    verificationPageDTO.setRealiseYear(verification.getCounter().getReleaseYear());
                }
                verificationPageDTO.setDismantled(verification.isCounterStatus());
                verificationPageDTO.setNumberCounter(verification.getCounter().getNumberCounter());
                verificationPageDTO.setCounterId(verification.getCounter().getId());
            } else if (set != null) {
                verificationPageDTO.setSymbol(null);
                verificationPageDTO.setStandardSize(null);
                verificationPageDTO.setRealiseYear(null);
                verificationPageDTO.setDismantled(verification.isCounterStatus());
            }
            resultList.add(verificationPageDTO);
        }
        return resultList;
    }

    public static List<VerificationPlanningTaskDTO> toDoFromPageContent(List<Verification> verifications) {
        List<VerificationPlanningTaskDTO> taskDTOs = new ArrayList<VerificationPlanningTaskDTO>(verifications.size());
        for (Verification verification : verifications) {
            VerificationPlanningTaskDTO dto = new VerificationPlanningTaskDTO();
            dto.setVerificationId(verification.getId());
            dto.setProviderName(verification.getProvider() != null ? verification.getProvider().getName() : null);
            if (verification.getClientData() != null) {
                dto.setPhone(verification.getClientData().getPhone());
                dto.setSecondphone(verification.getClientData().getSecondPhone());
                dto.setClientFullName(verification.getClientData().getFullName());
                if (verification.getClientData().getClientAddress() != null) {
                    dto.setDistrict(verification.getClientData().getClientAddress().getDistrict());
                    dto.setStreet(verification.getClientData().getClientAddress().getStreet());
                    dto.setBuilding(verification.getClientData().getClientAddress().getBuilding());
                    dto.setFlat(verification.getClientData().getClientAddress().getFlat());
                }
            }
            if (verification.getInfo() != null) {
                dto.setDateOfVerif(verification.getInfo().getDateOfVerif());
                String time;
                if ((verification.getInfo().getTimeFrom() != null) || (verification.getInfo().getTimeTo() != null)) {
                    time = verification.getInfo().getTimeFrom() + " - " + verification.getInfo().getTimeTo();
                } else {
                    time = "";
                }
                dto.setTime(time);
                dto.setServiceability(verification.getInfo().getServiceability());
                dto.setNoWaterToDate(verification.getInfo().getNoWaterToDate());
                dto.setNotes(verification.getInfo().getNotes());
            }
            dto.setSealPresence(verification.isSealPresence());
            dto.setVerificationWithDismantle(verification.isVerificationWithDismantle());
            dto.setInitialDate(verification.getInitialDate());
            taskDTOs.add(dto);
        }
        return taskDTOs;
    }

    public static List<RejectedVerificationsProviderPageDTO> toDoFromContent(List<Verification> verifications) {
        List<RejectedVerificationsProviderPageDTO> taskDTOs = new ArrayList<>(verifications.size());
        for (Verification verification : verifications) {
            RejectedVerificationsProviderPageDTO dto = new RejectedVerificationsProviderPageDTO();
            dto.setVerificationId(verification.getId());
            dto.setCalibratorName(verification.getCalibrator() != null ? verification.getCalibrator().getName() : null);
            if (verification.getClientData() != null && verification.getClientData().getClientAddress() != null) {
                    dto.setDistrict(verification.getClientData().getClientAddress().getDistrict());
                    dto.setStreet(verification.getClientData().getClientAddress().getStreet());
                    dto.setBuilding(verification.getClientData().getClientAddress().getBuilding());
                    dto.setFlat(verification.getClientData().getClientAddress().getFlat());
            }
            dto.setRejectedReason(verification.getRejectedInfo().getName());
            dto.setEmployeeProvider(verification.getProviderEmployee().getLastName()+" "+verification.getProviderEmployee().getFirstName()+" "+verification.getProviderEmployee().getMiddleName());
            dto.setRejectedCalibratorDate(verification.getRejectedCalibratorDate());
            dto.setClient_full_name(verification.getClientData().getFullName());
            dto.setStatus(verification.getStatus().toString());
            taskDTOs.add(dto);
        }
        return taskDTOs;
    }
    }

