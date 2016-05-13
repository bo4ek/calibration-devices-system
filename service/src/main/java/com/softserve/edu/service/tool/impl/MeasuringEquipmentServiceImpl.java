package com.softserve.edu.service.tool.impl;

import com.softserve.edu.entity.device.MeasuringEquipment;
import com.softserve.edu.repository.MeasuringEquipmentRepository;
import com.softserve.edu.service.tool.MeasureEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeasuringEquipmentServiceImpl implements MeasureEquipmentService {

    @Autowired
    private MeasuringEquipmentRepository measuringEquipmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MeasuringEquipment> getAll() {
        return (List<MeasuringEquipment>) measuringEquipmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MeasuringEquipment> getMeasuringEquipmentsBySearchAndPagination(int pageNumber,
                                                                                int itemsPerPage, String search) {
        PageRequest pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
        return search == null ? measuringEquipmentRepository.findAll(pageRequest) :
                measuringEquipmentRepository.findByNameLikeIgnoreCase("%" + search + "%", pageRequest);
    }

    @Override
    @Transactional
    public void addMeasuringEquipment(MeasuringEquipment measuringEquipment) {
        measuringEquipmentRepository.save(measuringEquipment);
    }

    @Override
    @Transactional(readOnly = true)
    public MeasuringEquipment getMeasuringEquipmentById(Long equipmentId) {
        return measuringEquipmentRepository.findOne(equipmentId);
    }

    @Override
    @Transactional
    public void editMeasuringEquipment(Long equipmentId, String name, String deviceType, String manufacturer,
                                       String verificationInterval) {
        MeasuringEquipment measuringEquipment = measuringEquipmentRepository.findOne(equipmentId);
        measuringEquipment.setName(name);
        measuringEquipment.setDeviceType(deviceType);
        measuringEquipment.setManufacturer(manufacturer);
        measuringEquipment.setVerificationInterval(verificationInterval);
        measuringEquipmentRepository.save(measuringEquipment);
    }

    @Override
    @Transactional
    public void deleteMeasuringEquipment(Long equipmentId) {
        measuringEquipmentRepository.delete(equipmentId);
    }

}
