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

    @Query(value = "SELECT * FROM COUNTER_TYPE ct INNER JOIN DEVICE d ON ct.deviceId = d.id " +
            "WHERE ct.standardSize = ?1 AND d.deviceType = ?2 AND ct.symbol = ?3", nativeQuery = true)
    List<CounterType> findByStandardSizeAndDeviceTypeAndSymbol(String standardSize, String deviceType, String symbol);

    CounterType findOneBySymbolAndStandardSize(String symbol, String standardSize);

    CounterType findOneBySymbolAndStandardSizeAndDeviceId(String symbol, String standardSize, Long deviceId);

    @Query(value = "SELECT ct.symbol FROM COUNTER_TYPE ct INNER JOIN DEVICE d ON ct.deviceId = d.id " +
                    "WHERE  d.deviceType = ?1", nativeQuery = true)
    Set<String> findSymbolsByDeviceType (String deviceType);

    @Query(value = "SELECT ct.standardSize FROM COUNTER_TYPE ct INNER JOIN DEVICE d ON ct.deviceId = d.id " +
                    "WHERE ct.symbol = ?1 AND d.deviceType = ?2", nativeQuery = true)
    Set<String> findStandardSizesBySymbolAndDeviceType(String symbol, String deviceType);

    @Query(value = "SELECT ct.symbol FROM COUNTER_TYPE ct INNER JOIN DEVICE d ON ct.deviceId = d.id " +
            "WHERE ct.standardSize = ?1 AND d.deviceType = ?2", nativeQuery = true)
        Set<String> findSymbolByStandardSizeAndDeviceType (String standardSize, String deviceType);

    @Query(value = "SELECT ct.standardSize FROM COUNTER_TYPE ct ", nativeQuery = true)
    Set<String> findAllStandardSizes();


    /**
     * Find maximum yearIntroduction by criteria to calculate right calibration interval
     * @param standardSize
     * @param symbol
     * @param manufacturer
     * @param deviceType
     * @param yearIntroduction
     * @return
     */
    @Query(value = "select max(ct.yearIntroduction)" +
            "from counter_type ct INNER JOIN device d ON ct.deviceId = d.id " +
            "where ct.standardSize = ?1 AND ct.symbol = ?2 AND ct.manufacturer = ?3 AND d.deviceType = ?4 " +
            "AND ct.yearIntroduction <= ?5", nativeQuery = true)
    Integer findMaximumYearIntroduction(String standardSize, String symbol, String manufacturer, String deviceType, Integer yearIntroduction);

    @Query(value = "select result.calibrationInterval from (select ct.calibrationInterval,ct.yearIntroduction " +
            "from counter_type ct INNER JOIN device d ON ct.deviceId = d.id " +
            "where ct.standardSize = ?1 AND ct.symbol = ?2 AND ct.manufacturer = ?3 AND d.deviceType = ?4 " +
            "AND ct.yearIntroduction <= ?5) as result where result.yearIntroduction = ?6", nativeQuery = true)
    Integer findCalibrationInterval(String standardSize, String symbol,
                                    String manufacturer, String deviceType, Integer yearIntroduction, Integer maxOfYearIntroduction);



}
