package com.softserve.edu.service.catalogue;


import com.softserve.edu.entity.verification.calibration.RejectedInfo;

import java.util.List;

public interface RejectedInfoService {

    List<RejectedInfo> getAllReasons();

    RejectedInfo findOneById(Long id);
}
