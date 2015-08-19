package com.softserve.edu.service;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import com.softserve.edu.entity.Device;
import com.softserve.edu.repository.DeviceRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.io.FileNotFoundException;
import java.util.List;

public class DeviceServiceTest {

	private static final String deviceName = "Test";

	private static final Long testId = 123L;

	private static final Long nullId = null;

	@InjectMocks
	private DeviceService deviceService;

	@Mock
	private DeviceRepository deviceRepository;

	@Mock
	private List<Device> devices;

	@Mock
	private Device device;

	@Mock
	private PageRequest pageRequest;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExistsWithDeviceid() throws FileNotFoundException {
		when(deviceRepository.findOne(testId)).thenReturn(null);
		Assert.assertFalse(deviceService.existsWithDeviceid(testId));

	}

	@Test(expected  = NullPointerException.class)
	public void testExceptionExistsWithDeviceid() throws FileNotFoundException {
		DeviceService d = new DeviceService();
		d.existsWithDeviceid(nullId);
	}


	@Test(expected  = NullPointerException.class)
	public void testExceptionGetById() throws NullPointerException {
		DeviceService deviceService = new DeviceService();
		deviceService.existsWithDeviceid(testId);
	}

	@Test
	public void testGetById() throws Exception {
		when(deviceRepository.findOne(testId )).thenReturn(device);
		deviceService.getById(testId);
		verify(deviceRepository).findOne(testId);
		Assert.assertEquals(device, deviceService.getById(testId));
	}

	/**
	 * Test for null returning if no device.
	 */
	@Test
	public void testGetAll() {
		when(deviceRepository.findAll()).thenReturn(null);
		Assert.assertNull(deviceService.getAll());
	}

	@Test
	public void testSecondGetAll() {
		when(deviceRepository.findAll()).thenReturn(devices);
		Assert.assertEquals(deviceRepository.findAll(), deviceService.getAll());
	}

	@Test
	public void testGetDevicesBySearchAndPagination() {
		deviceService.getDevicesBySearchAndPagination(2, 5, null);
		verify(deviceRepository).findAll(any(PageRequest.class));

	}

	@Test
	public void testSecondGetDevicesBySearchAndPagination() {
		String search = "search";
		deviceService.getDevicesBySearchAndPagination(2, 5, search);

		verify(deviceRepository).findByNumberLikeIgnoreCase(eq("%" + search + "%"), any(PageRequest.class));

	}

	@Test
	public void testGetAllByType() {
		when(deviceRepository.findByDeviceName(deviceName)).thenReturn(null);
		Assert.assertNull(deviceService.getAllByType(deviceName));
	}

	@Test
	public void testSecondGetAllByType() {
		when(deviceRepository.findByDeviceName(deviceName)).thenReturn(devices);
		Assert.assertEquals(deviceRepository.findByDeviceName(deviceName), deviceService.getAllByType(deviceName));
	}

}
