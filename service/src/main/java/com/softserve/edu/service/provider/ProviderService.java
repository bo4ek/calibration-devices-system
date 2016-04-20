package com.softserve.edu.service.provider;

import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.organization.Organization;

import java.util.List;
import java.util.Set;

public interface ProviderService {

     Set<String> getTypesById(Long id);

     Organization findById(Long id);

     CounterType findOneBySymbolAndStandardSizeAndDeviceId(String symbol, String standardSize, Long deviceId);

  //   Set<String> findStandardSizesBySymbolAndDeviceId(String symbol, Long deviceId);
}
