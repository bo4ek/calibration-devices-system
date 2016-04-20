package com.softserve.edu.service.admin.impl;

import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.repository.CounterRepository;
import com.softserve.edu.repository.CounterTypeRepository;
import com.softserve.edu.service.admin.CounterTypeService;
import com.softserve.edu.service.tool.DeviceService;
import com.softserve.edu.service.utils.ArchivalCounterTypeQueryConstructorAdmin;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Service
public class CounterTypeServiceImpl implements CounterTypeService{

    @Autowired
    private CounterTypeRepository counterTypeRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private DeviceService deviceService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Save counter type with params
     * @param name
     * @param symbol
     * @param standardSize
     * @param manufacturer
     * @param calibrationInterval
     * @param yearIntroduction
     * @param gost
     * @param deviceId
     */
    @Override
    @Transactional
    public void addCounterType(String name, String symbol, String standardSize, String manufacturer,
                               Integer calibrationInterval, Integer yearIntroduction, String gost, Long deviceId) {
        CounterType counterType = new CounterType(name, symbol, standardSize,
                manufacturer, calibrationInterval, yearIntroduction,
                gost, deviceService.getById(deviceId));
        counterTypeRepository.save(counterType);
    }

    /**
     * Edit counter type with params
     * @param id
     * @param name
     * @param symbol
     * @param standardSize
     * @param manufacturer
     * @param calibrationInterval
     * @param yearIntroduction
     * @param gost
     * @param deviceId
     */
    @Override
    @Transactional
    public void editCounterType(Long id, String name, String symbol, String standardSize, String manufacturer,
                                Integer calibrationInterval, Integer yearIntroduction, String gost, Long deviceId) {
        CounterType counterType = counterTypeRepository.findOne(id);

        counterType.setName(name);
        counterType.setSymbol(symbol);
        counterType.setStandardSize(standardSize);
        counterType.setManufacturer(manufacturer);
        counterType.setCalibrationInterval(calibrationInterval);
        counterType.setYearIntroduction(yearIntroduction);
        counterType.setGost(gost);
        counterType.setDevice(deviceService.getById(deviceId));

        counterTypeRepository.save(counterType);
    }

    /**
     * Delete counter type by his id
     * @param id
     */
    @Override
    @Transactional
    public void removeCounterType(Long id) {
        counterTypeRepository.delete(id);
    }

    /**
     * Find counter type by id
     * @param id
     * @return
     */
    @Override
    @Transactional
    public CounterType findById(Long id) {
        return counterTypeRepository.findOne(id);
    }


    /**
     * Service for building page by SortCriteria, SortOrder and Searching data
     * @param pageNumber
     * @param itemsPerPage
     * @param name
     * @param symbol
     * @param standardSize
     * @param manufacturer
     * @param calibrationInterval
     * @param yearIntroduction
     * @param gost
     * @param sortCriteria
     * @param sortOrder
     * @return
     */
    @Override
    @Transactional
    public ListToPageTransformer<CounterType> getCounterTypeBySearchAndPagination(int pageNumber, int itemsPerPage,
                                                                                  String name, String symbol, String standardSize,
                                                                                  String manufacturer, Integer calibrationInterval,
                                                                                  Integer yearIntroduction, String gost,
                                                                                  String sortCriteria, String sortOrder) {
        CriteriaQuery<CounterType> criteriaQuery = ArchivalCounterTypeQueryConstructorAdmin
                .buildSearchQuery(name, symbol, standardSize, manufacturer, calibrationInterval, yearIntroduction,
                        gost, sortCriteria, sortOrder, entityManager);

        Long count = entityManager.createQuery(ArchivalCounterTypeQueryConstructorAdmin
                .buildCountQuery(name, symbol, standardSize, manufacturer, calibrationInterval, yearIntroduction,
                        gost, entityManager)).getSingleResult();

        TypedQuery<CounterType> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<CounterType> CounterTypeList = typedQuery.getResultList();

        ListToPageTransformer<CounterType> result = new ListToPageTransformer<>();
        result.setContent(CounterTypeList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    public CounterType findOneBySymbolAndStandardSizeAndDeviceId(String symbol, String standardSize, Long deviceId) {
        return counterTypeRepository.findOneBySymbolAndStandardSizeAndDeviceId(symbol, standardSize, deviceId);
    }

    @Override
    public List<CounterType> findBySymbolAndStandardSize(String symbol, String standardSize) {
        return counterTypeRepository.findBySymbolAndStandardSize(symbol, standardSize);
    }
}
