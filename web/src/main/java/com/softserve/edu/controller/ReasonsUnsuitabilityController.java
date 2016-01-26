package com.softserve.edu.controller;

import com.softserve.edu.dto.admin.UnsuitabilityReasonDTO;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.device.UnsuitabilityReason;
import com.softserve.edu.repository.CounterRepository;
import com.softserve.edu.service.admin.CounterTypeService;
import com.softserve.edu.service.admin.UnsuitabilityReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReasonsUnsuitabilityController {

    @Autowired
    private CounterTypeService counterTypeService;

    @Autowired
    private CounterRepository counterRepository;

    /**
     * Get all reasons unsuitability for counter with type {@code counterTypeId}
     * @return  all unsuitability reasons of counter
     */
    @RequestMapping(value = "reasons_unsuitability/{counterTypeId}", method = RequestMethod.GET)
    public List<UnsuitabilityReasonDTO> isCertificateSigned(@PathVariable Long counterTypeId) {
        return counterTypeService.findById(counterTypeId).getDevice().getUnsuitabilitySet().stream()
                .map(reason -> new UnsuitabilityReasonDTO(reason.getId(), reason.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Get all reasons unsuitability for counter by counterId
     * @return all unsuitability reasons of counter
     */
    @RequestMapping(value = "getAllUnsuitabilityReasons/{counterId}", method = RequestMethod.GET)
    public List<UnsuitabilityReasonDTO> getAllUnsuitabilityReasons(@PathVariable Long counterId) {
        return counterRepository.findOne(counterId).getCounterType().getDevice().getUnsuitabilitySet().stream()
                .map(reason -> new UnsuitabilityReasonDTO(reason.getId(), reason.getName()))
                .collect(Collectors.toList());
    }
}