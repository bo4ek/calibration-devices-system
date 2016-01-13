package com.softserve.edu.repository;

import com.softserve.edu.entity.device.CounterType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CounterTypeRepository extends CrudRepository<CounterType, Long> {

    List<CounterType> findByDeviceId(Long deviceId);

    List<CounterType> findBySymbolAndDeviceId(String symbol, Long deviceId);

    List<CounterType> findAll();

    List<CounterType> findBySymbol(String symbol);

    CounterType findOneBySymbolAndStandardSize(String symbol, String standardSize);

    CounterType findOneBySymbolAndStandardSizeAndDeviceId(String symbol, String standardSize, Long deviceId);

    @Query(value = "SELECT ct.symbol FROM COUNTER_TYPE ct INNER JOIN DEVICE d ON ct.deviceId = d.id " +
                    "WHERE  d.deviceType = ?1", nativeQuery = true)
    Set<String> findSymbolsByDeviceType (String deviceType);

    @Query(value = "SELECT ct.standardSize FROM COUNTER_TYPE ct INNER JOIN DEVICE d ON ct.deviceId = d.id " +
                    "WHERE ct.symbol = ?1 AND d.deviceType = ?2", nativeQuery = true)
    Set<String> findStandardSizesBySymbolAndDeviceType(String symbol, String deviceType);

    @Query(value = "SELECT ct.manufacturer FROM COUNTER_TYPE ct " +
            "WHERE ct.symbol = ?1", nativeQuery = true)
    Set<String> findManufacturerBySymbol (String symbol);

    @Query(value = "SELECT ct.symbol FROM COUNTER_TYPE ct INNER JOIN DEVICE d ON ct.deviceId = d.id " +
            "WHERE ct.standardSize = ?1 AND d.deviceType = ?2", nativeQuery = true)
        Set<String> findSymbolByStandardSizeAndDeviceType (String standardSize, String deviceType);
}
