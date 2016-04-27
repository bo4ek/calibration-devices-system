package com.softserve.edu.service.catalogue.impl;


import com.softserve.edu.entity.verification.calibration.RejectedInfo;
import com.softserve.edu.repository.RejectedInfoRepository;
import com.softserve.edu.service.catalogue.RejectedInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RejectedInfoServiceImpl implements RejectedInfoService {

    @Autowired
    RejectedInfoRepository rejectedInfoRepository;

    @Override
    public List<RejectedInfo> getAllReasons() {
        return (List<RejectedInfo>) rejectedInfoRepository.findAll();
    }

    @Override
    public RejectedInfo findOneById(Long id) {
        return rejectedInfoRepository.findOne(id);
    }
}
