package com.softserve.edu.controller.provider.util;

import com.softserve.edu.dto.VerificationPlanningTaskFilterSearch;
import com.softserve.edu.dto.calibrator.VerificationPlanningTaskDTO;
import com.softserve.edu.dto.provider.VerificationPageDTO;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.Verification;


import java.util.*;

public class VerificationPageDTOTransformer {

    public static List<VerificationPageDTO> toDtoFromList(List<Verification> list){

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
                            verification.getBbiProtocols()==null?false:true,
                            verification.getProcessTimeExceeding(),
                            calibrationTest,
                            verification.getDevice(),
                            null,
                            null,
                            verification.getClientData().getClientAddress().getAddress(),
                            verification.getClientData().getClientAddress().getBuilding(),
                            verification.getClientData().getClientAddress().getFlat()
            );
            if(verification.getProvider()!=null){verificationPageDTO.setNameProvider(verification.getProvider().getName());}
            if(verification.getCalibrator()!=null){verificationPageDTO.setNameCalibrator(verification.getCalibrator().getName());}
            Set<CounterType> set =(verification.getDevice() != null) ? verification.getDevice().getCounterTypeSet() : null;
            verificationPageDTO.setIsManual(verification.getIsManual());
            if(verification.getCounter()!=null && verification.getCounter().getCounterType() != null){
                verificationPageDTO.setSymbol(verification.getCounter().getCounterType().getSymbol());
                verificationPageDTO.setStandardSize(verification.getCounter().getCounterType().getStandardSize());
                if(verification.getCounter().getReleaseYear()!= null){verificationPageDTO.setRealiseYear(Integer.valueOf(verification.getCounter().getReleaseYear()));}
                verificationPageDTO.setDismantled(verification.isCounterStatus());
                verificationPageDTO.setNumberCounter(verification.getCounter().getNumberCounter());
                verificationPageDTO.setCounterId(verification.getCounter().getId());
            } else if (set != null) {
                List<CounterType> listCounterType = new ArrayList<>(set);

                //Check this pls, ask for further requirements. Here's only the view on the page new-verifications.html
                // no updates are made to DB. This part of the code calls only when user didn't enter counter details (symbol, size and year)
//                verificationPageDTO.setSymbol(listCounterType.get(0).getSymbol());
//                verificationPageDTO.setStandardSize(listCounterType.get(0).getStandardSize());
//                verificationPageDTO.setRealiseYear(listCounterType.get(0).getYearIntroduction());
                verificationPageDTO.setSymbol("-");
                verificationPageDTO.setStandardSize("-");
                verificationPageDTO.setRealiseYear(0);
                verificationPageDTO.setDismantled(verification.isCounterStatus());
            }
            resultList.add(verificationPageDTO);
        }
        return resultList;
    }

    public static List<VerificationPlanningTaskDTO> toDoFromPageContent(List<Verification> verifications,
                                                                        VerificationPlanningTaskFilterSearch searchData){

        List<VerificationPlanningTaskDTO> taskDTOs = new ArrayList<VerificationPlanningTaskDTO>();
        for (Verification verification : verifications) {
            if (verificationPlanningTaskFiltersCheck(verification, searchData)) {
                taskDTOs.add(new VerificationPlanningTaskDTO(verification.getSentToCalibratorDate(),
                        verification.getId(),
                        verification.getProvider().getName(),
                        verification.getClientData().getFullName(),
                        verification.getClientData().getClientAddress().getDistrict(),
                        verification.getClientData().getClientAddress().getStreet(),
                        verification.getClientData().getClientAddress().getBuilding(),
                        verification.getClientData().getClientAddress().getFlat(),
                        verification.getClientData().getPhone(),
                        verification.getClientData().getSecondPhone(),
                        (verification.getInfo() != null) ? verification.getInfo().getDateOfVerif() : null,
                        (verification.getInfo() != null) ? verification.getInfo().getTimeFrom() : null,
                        (verification.getInfo() != null) ? verification.getInfo().getTimeTo() : null,
                        (verification.getInfo() != null) ? verification.getInfo().getServiceability() : null,
                        (verification.getInfo() != null) ? verification.getInfo().getNoWaterToDate() : null,
                        verification.isSealPresence()
                ));
            }
        }
        return taskDTOs;
    }

    public static boolean verificationPlanningTaskFiltersCheck(Verification verification, VerificationPlanningTaskFilterSearch searchData)
    {
        Date startDate = new Date(searchData.getDate());
        Date endDate = new Date(searchData.getEndDate());
        String clientName = searchData.getClient_full_name();
        String providerName = searchData.getProvider();
        String clientDistrict = searchData.getDistrict();
        String clientStreet = searchData.getStreet();
        String clientPhone = searchData.getTelephone();
        String clientBuilding = searchData.getBuilding();
        String sealPresence = searchData.getSealPresence();
        String serviceability = searchData.getServiceability();
        if ((startDate.before(verification.getSentToCalibratorDate()) || startDate.equals(verification.getSentToCalibratorDate()))
                && (endDate.after(verification.getSentToCalibratorDate()) || endDate.equals(verification.getSentToCalibratorDate()))) {
            if ((clientName == null || clientName.isEmpty()) && (providerName == null || providerName.isEmpty())
                    && (clientDistrict == null || clientDistrict.isEmpty()) && (clientStreet == null || clientStreet.isEmpty())
                    && (clientPhone == null || clientPhone.isEmpty()) && (clientBuilding == null || clientBuilding.isEmpty())
                    && (sealPresence == null || sealPresence.isEmpty()) && (serviceability == null || serviceability.isEmpty())) {
                return true;
            } else if ((clientName != null && !clientName.isEmpty()) && verification.getClientData().getFullName().toLowerCase().contains(clientName.toLowerCase())) {
                return true;
            } else if ((providerName != null && !providerName.isEmpty()) && verification.getProvider().getName().toLowerCase().contains(providerName.toLowerCase())) {
                return true;
            } else if ((clientDistrict != null && !clientDistrict.isEmpty()) && verification.getClientData().getClientAddress().getDistrict().toLowerCase().contains(clientDistrict.toLowerCase())) {
                return true;
            } else if ((clientStreet != null && !clientStreet.isEmpty()) && verification.getClientData().getClientAddress().getStreet().toLowerCase().contains(clientStreet.toLowerCase())) {
                return true;
            } else if ((clientPhone != null && !clientPhone.isEmpty()) && verification.getClientData().getPhone().toLowerCase().contains(clientPhone.toLowerCase())) {
                return true;
            } else if ((clientBuilding != null && !clientBuilding.isEmpty()) && verification.getClientData().getClientAddress().getBuilding().toLowerCase().contains(clientBuilding.toLowerCase())) {
                return true;
            } else if ((sealPresence != null && !sealPresence.isEmpty()) && verification.isSealPresence() && sealPresence.equals("True")) {
                return true;
            } else if ((sealPresence != null && !sealPresence.isEmpty()) && !verification.isSealPresence() && sealPresence.equals("False")) {
                return true;
            } else if ((serviceability != null && !serviceability.isEmpty()) && (verification.getInfo() != null) && ((verification.getInfo().isServiceability() && serviceability.equals("True")))) {
                return true;
            } else if ((serviceability != null && !serviceability.isEmpty()) && (verification.getInfo() != null) && ((!verification.getInfo().isServiceability() && serviceability.equals("False")))) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}
