package com.softserve.edu.service.tool.impl;


import com.softserve.edu.entity.device.Device;
import com.softserve.edu.repository.DeviceRepository;
import com.softserve.edu.service.tool.DeviceService;
import com.softserve.edu.service.utils.ArchivalDevicesCategoryQueryConstructorAdmin;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


@Service
public class DeviceServiceImpl implements DeviceService {
	
	@Autowired
	private DeviceRepository deviceRepository;

	@PersistenceContext
	private EntityManager entityManager;

    @Override
	@Transactional
	public boolean existsWithDeviceId(Long id) {
		return deviceRepository.findOne(id) != null;
	}

    @Override
	@Transactional
	public Device getById(Long id) {
		return deviceRepository.findOne(id);
	}

    @Override
	@Transactional
	public List<Device> getAll() {
		return (List<Device>) deviceRepository.findAll(); 
	}

    @Override
	@Transactional
	  public Page<Device> getDevicesBySearchAndPagination(int pageNumber, int itemsPerPage, String search) {
	  PageRequest pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
	  return search == null ? deviceRepository.findAll(pageRequest) : deviceRepository.findByNumberLikeIgnoreCase("%" + search + "%", pageRequest);
	 }

	@Override
	@Transactional
	public ListToPageTransformer<Device> getCategoryDevicesBySearchAndPagination(int pageNumber, int itemsPerPage, Long id, String deviceType, String deviceName, String sortCriteria, String sortOrder) {
		CriteriaQuery<Device> criteriaQuery = ArchivalDevicesCategoryQueryConstructorAdmin
				.buildSearchQuery(id, deviceType, deviceName, sortCriteria, sortOrder, entityManager);

		Long count = entityManager.createQuery(ArchivalDevicesCategoryQueryConstructorAdmin
				.buildCountQuery(id, deviceType, deviceName, entityManager)).getSingleResult();

		TypedQuery<Device> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
		typedQuery.setMaxResults(itemsPerPage);
		List<Device> devicesCategoryList = typedQuery.getResultList();

		ListToPageTransformer<Device> result = new ListToPageTransformer<Device>();
		result.setContent(devicesCategoryList);
		result.setTotalItems(count);
		return result;
	}

	@Override
	@Transactional
	public List<Device> getAllByDeviceName(String device) {
		return deviceRepository.findByDeviceName(device);
	}

    @Override
    public Device getByDeviceTypeAndDefaultDevice(String deviceType, Boolean defaultDevice) {
        return deviceRepository.findByDeviceTypeAndDefaultDevice(Device.DeviceType.valueOf(deviceType), defaultDevice);
    }

    @Override
	@Transactional
	public void addDeviceCategory(String deviceType, String deviceName) {
		Device deviceCategory = new Device(Device.DeviceType.valueOf(deviceType), deviceName);
		deviceRepository.save(deviceCategory);
	}

	@Override
	@Transactional
	public void editDeviceCategory(Long id, String deviceType, String deviceName) {
		Device device = deviceRepository.findOne(id);

		device.setDeviceType(Device.DeviceType.valueOf(deviceType));
		device.setDeviceName(deviceName);

		deviceRepository.save(device);
	}

	@Override
	@Transactional
	public void removeDeviceCategory(Long id) {
		deviceRepository.delete(id);
	}

    @Override
    public Device.DeviceType getDeviceTypeById(Long id)
    {
        return deviceRepository.getDeviceTypeById(id);
    }
}
