package com.softserve.edu.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.softserve.edu.entity.device.Device;

@Repository
public interface DeviceRepository extends CrudRepository<Device, Long> {

	public Page<Device> findAll(Pageable pageable);

	Page<Device> findByNumberLikeIgnoreCase(String number, Pageable pageable);

	public List<Device> findByDeviceName(String deviceName );

    public List<Device> findByDeviceType(Device.DeviceType deviceType);


}
