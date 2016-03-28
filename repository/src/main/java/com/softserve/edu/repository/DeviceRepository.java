package com.softserve.edu.repository;

import com.softserve.edu.entity.device.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends CrudRepository<Device, Long> {

	public Page<Device> findAll(Pageable pageable);

	Page<Device> findByNumberLikeIgnoreCase(String number, Pageable pageable);

	public List<Device> findByDeviceName(String deviceName );

    public Device findByDeviceTypeAndDefaultDevice(Device.DeviceType deviceType, Boolean defaultDevice);

    @Query("SELECT d.deviceType FROM Device d WHERE d.id = :id")
    Device.DeviceType getDeviceTypeById(@Param("id")Long id);

}
