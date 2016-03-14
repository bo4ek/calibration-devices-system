package com.softserve.edu.service.testVerification;


import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.repository.CalibrationTestRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.exceptions.NotAvailableException;
import com.softserve.edu.service.verification.VerificationService;
import com.softserve.edu.service.verification.impl.VerificationServiceImpl;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class VerificationServiceImplTest {
	
	@InjectMocks
	private static VerificationService verificationService = new VerificationServiceImpl();
	
	@Mock
	private Logger mockLogger;
	
	@Mock
	private VerificationRepository mockVerificationRepository;
	
	@Mock
	private CalibrationTestRepository mockCalibrationTestRepository;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private Verification mockVerification = mock(Verification.class);
	
	@Mock
	private EntityManager mockEntityManager;

	@BeforeClass
	public static void testCreateVerificationProviderEmployeeService() {
		verificationService = new VerificationServiceImpl();
	}

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testSaveVerification() {
		verificationService.saveVerification(mockVerification);
		verify(mockVerificationRepository, times(1)).save(mockVerification);
	}
	
	@Test
	public void testFindById() {
		String code = "code";
		when(mockVerificationRepository.findOne(code)).thenReturn(mockVerification);
		Verification expected = verificationService.findById(code);
		Assert.assertEquals(expected, mockVerification);
	}
	
	@Test
	public void testFindPageOfAllVerificationsByProviderId() {
		long providerId = 1;
		PageRequest pageRequest = mock(PageRequest.class);
		Pageable pageable = pageRequest;
		List<Verification> list = new ArrayList<Verification>();
		Page<Verification> page = (Page<Verification>) new PageImpl(list);
		when(mockVerificationRepository.findByProviderId(providerId, pageable)).thenReturn(page);
		Assert.assertNull(verificationService.findPageOfAllVerificationsByProviderId(providerId, 1, 1));
	}
	
	@Test
	public void testFindPageOfAllVerificationsByCalibratorId() {
		long providerId = 1;
		PageRequest pageRequest = mock(PageRequest.class);
		Pageable pageable = pageRequest;
		List<Verification> list = new ArrayList<Verification>();
		Page<Verification> page = (Page<Verification>) new PageImpl(list);
		when(mockVerificationRepository.findByCalibratorId(providerId, pageable)).thenReturn(page);
		Assert.assertNull(verificationService.findPageOfAllVerificationsByCalibratorId(providerId, 1, 1));
	}
	
	@Test
	public void testFindPageOfAllVerificationsByStateVerificatorId() {
		long providerId = 1;
		PageRequest pageRequest = mock(PageRequest.class);
		Pageable pageable = pageRequest;
		List<Verification> list = new ArrayList<Verification>();
		Page<Verification> page = (Page<Verification>) new PageImpl(list);
		when(mockVerificationRepository.findByStateVerificatorId(providerId, pageable)).thenReturn(page);
		Assert.assertNull(verificationService.findPageOfAllVerificationsByStateVerificatorId(providerId, 1, 1));
	}
	
	@Test
	public void testFindCountOfNewVerificationsByCalibratorId() {
		long calibratorId = 1;
		when(mockVerificationRepository
						.countByCalibratorIdAndStatusAndCalibratorEmployeeUsername(calibratorId,
								Status.IN_PROGRESS, null))
				.thenReturn(null);
		Assert.assertNull(verificationService.findCountOfNewVerificationsByCalibratorId(calibratorId));
	}
	
	@Test
	public void testFindCountOfNewVerificationsByProviderId() {
		long providerId = 1;
		when(mockVerificationRepository
						.countByProviderIdAndStatusAndReadStatus(providerId,
								Status.SENT, Verification.ReadStatus.UNREAD))
				.thenReturn(null);
		Assert.assertNull(verificationService.findCountOfNewVerificationsByProviderId(providerId));
	}
	
	@Test
	public void testFindCountOfNewVerificationsByStateVerificatorId() {
		long providerId = 1;
		when(
				mockVerificationRepository
						.countByStateVerificatorIdAndStatusAndReadStatus(providerId,
								Status.SENT_TO_VERIFICATOR, Verification.ReadStatus.UNREAD))
				.thenReturn(null);
		Assert.assertNull(verificationService.findCountOfNewVerificationsByStateVerificatorId(providerId));
	}
	
	@Test
	public void testFindPageOfSentVerificationsByProviderId() {
		long providerId = 1;
		PageRequest pageRequest = mock(PageRequest.class);
		Pageable pageable = pageRequest;
		List<Verification> list = new ArrayList<Verification>();
		Page<Verification> page = (Page<Verification>) new PageImpl(list);
		when(mockVerificationRepository
						.findByProviderIdAndStatusOrderByInitialDateDesc(
								providerId, Status.SENT, pageable)).thenReturn(page);
		Assert.assertNull(verificationService
                .findPageOfSentVerificationsByProviderId(providerId, 1, 1));
	}
	
	@Test
	public void testFindPageOfSentVerificationsByCalibratorId() {
		long providerId = 1;
		PageRequest pageRequest = mock(PageRequest.class);
		Pageable pageable = pageRequest;
		List<Verification> list = new ArrayList<Verification>();
		Page<Verification> page = (Page<Verification>) new PageImpl(list);
		when(mockVerificationRepository
						.findByCalibratorIdAndStatusOrderByInitialDateDesc(
								providerId, Status.IN_PROGRESS, pageable)).thenReturn(page);
		Assert.assertNull(verificationService
                .findPageOfSentVerificationsByCalibratorId(providerId, 1, 1));
	}
	
	
	@Test
	public void testFindPageOfSentVerificationsByStateVerificatorId() {
		long providerId = 1;
		PageRequest pageRequest = mock(PageRequest.class);
		Pageable pageable = pageRequest;
		List<Verification> list = new ArrayList<Verification>();
		Page<Verification> page = (Page<Verification>) new PageImpl(list);
		when(mockVerificationRepository
						.findByStateVerificatorIdAndStatus(
								providerId, Status.SENT_TO_VERIFICATOR, pageable)).thenReturn(page);
		Assert.assertNull(verificationService
                .findPageOfSentVerificationsByStateVerificatorId(providerId, 1, 1));
	}
	
	//// dobavutu testy
	
	@Test
	public void testFindByIdAndProviderId() {
		String id = "id";
		long providerId = 1;
		when(mockVerificationRepository.findByIdAndProviderId(id, providerId)).thenReturn(null);
		exception.expect(AccessDeniedException.class);
		exception.expectMessage("You have not permission to get this data.");
		Verification actual = verificationService.findByIdAndProviderId(id, providerId);
	}
	
	@Test
	public void testFindByIdAndProviderIdSecondBranch() {
		String id = "id";
		long providerId = 1;
		when(mockVerificationRepository.findByIdAndProviderId(id, providerId)).thenReturn(mockVerification);
		Verification actual = verificationService.findByIdAndProviderId(id, providerId);
		Assert.assertEquals(actual, mockVerification);
	}
	
	@Test
	public void testFindByIdAndCalibratorId() {
		String id = "id";
		long calibratorId = 1;
		when(mockVerificationRepository.findByIdAndCalibratorId(id, calibratorId)).thenReturn(null);
		exception.expect(AccessDeniedException.class);
		exception.expectMessage("You have not permission to get this data.");
		Verification actual = verificationService.findByIdAndCalibratorId(id, calibratorId);
	}
	
	@Test
	public void testFindByIdAndCalibratorSecondBranch() {
		String id = "id";
		long calibratorId = 1;
		when(mockVerificationRepository.findByIdAndCalibratorId(id, calibratorId)).thenReturn(mockVerification);
		Verification actual = verificationService.findByIdAndCalibratorId(id, calibratorId);
		Assert.assertEquals(actual, mockVerification);
	}
	
	@Test
	public void testFindByIdAndStateVerificatorId() {
		String id = "id";
		long stateVerificatorId = 1;
		when(mockVerificationRepository.findByIdAndStateVerificatorId(id, stateVerificatorId)).thenReturn(null);
		exception.expect(AccessDeniedException.class);
		exception.expectMessage("You have not permission to get this data.");
		Verification actual = verificationService.findByIdAndStateVerificatorId(id, stateVerificatorId);
	}
	
	@Test
	public void testFindByIdAndStateVerificatorIdSecondBranch() {
		String id = "id";
		long stateVerificatorId = 1;
		when(mockVerificationRepository.findByIdAndStateVerificatorId(id, stateVerificatorId)).thenReturn(mockVerification);
		Verification actual = verificationService.findByIdAndStateVerificatorId(id, stateVerificatorId);
		Assert.assertEquals(actual, mockVerification);
	}
	
	@Test
	public void testUpdateVerificationReadStatus() {
		String verificationId = "id";
		String readStatus = "status";
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(null);
		verificationService.updateVerificationReadStatus(verificationId, readStatus);
		verify(mockLogger, times(1)).error(anyString());
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(mockVerification);
		verificationService.updateVerificationReadStatus(verificationId, readStatus);
		verify(mockVerification, times(1)).setReadStatus(Verification.ReadStatus.READ);
		verify(mockVerificationRepository, times(1)).save(mockVerification);
	}
	
	@Test
	public void testUpdateVerificationStatus() {
		String verificationId = "id";
		Status status = Status.ACCEPTED;
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(null);
		verificationService.updateVerificationStatus(verificationId, status);
		verify(mockLogger, times(1)).error(anyString());
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(mockVerification);
		verificationService.updateVerificationStatus(verificationId, status);
		verify(mockVerification, times(1)).setStatus(status);
		verify(mockVerification, times(1)).setReadStatus(Verification.ReadStatus.READ);
		verify(mockVerification, times(1)).setExpirationDate(any());
		verify(mockVerificationRepository, times(1)).save(mockVerification);
	}
	
	@Test
	public void testSendVerificationTo() {
		String verificationId = "id";
		Organization organization = mock(Organization.class);
		Status status = Status.ACCEPTED;
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(null);
		verificationService.sendVerificationTo(verificationId, organization, status);
		verify(mockLogger, times(1)).error(anyString());
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(mockVerification);
		status = Status.IN_PROGRESS;
		verificationService.sendVerificationTo(verificationId, organization, status);
		verify(mockVerification, times(1)).setCalibrator(organization);
		verify(mockVerification, times(1)).setSentToCalibratorDate(any());
		status = Status.SENT_TO_VERIFICATOR;
		verificationService.sendVerificationTo(verificationId, organization, status);
		verify(mockVerification, times(1)).setStateVerificator(organization);
		status = Status.TEST_OK;
		verificationService.sendVerificationTo(verificationId, organization, status);
		verify(mockVerification, times(1)).setProvider(organization);
		status = Status.TEST_NOK;
		verificationService.sendVerificationTo(verificationId, organization, status);
		verify(mockVerification, times(2)).setProvider(organization);
		status = Status.REJECTED;
		verificationService.sendVerificationTo(verificationId, organization, status);
		verify(mockVerification, times(1)).setStatus(status);
		verify(mockVerification, times(5)).setReadStatus(Verification.ReadStatus.UNREAD);
		verify(mockVerification, times(5)).setExpirationDate(any());
		verify(mockVerificationRepository, times(5)).save(mockVerification);
	}
	
	@Test
	public void testUpdateVerification(){
		Organization mockStateVerificator = mock(Organization.class);
		String verificationId = "id";
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(null);
		verificationService.updateVerification(verificationId, mockStateVerificator);
		verify(mockLogger, times(1)).error(anyString());
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(mockVerification);
		verificationService.updateVerification(verificationId, mockStateVerificator);
		verify(mockVerification, times(1)).setStatus(Status.TEST_OK);
		verify(mockVerification, times(1)).setStateVerificator(mockStateVerificator);
		verify(mockVerificationRepository, times(1)).save(mockVerification);
	}
	
	@Test
	public void testUpdateVerificationData(){
		Organization mockProvider = mock(Organization.class);
		String id = "id";
		ClientData mockClientData = mock(ClientData.class);
		when(mockVerificationRepository.findOne(id)).thenReturn(mockVerification);
		verificationService.updateVerificationData(id, mockClientData, mockProvider);
		verify(mockVerification, times(1)).setInitialDate(any());
		verify(mockVerification, times(1)).setClientData(mockClientData);
		verify(mockVerification, times(1)).setProvider(mockProvider);
		verify(mockVerification, times(1)).setStatus(Status.SENT);
		verify(mockVerification, times(1)).setReadStatus(Verification.ReadStatus.UNREAD);
		verify(mockVerificationRepository, times(1)).save(mockVerification);
	}
	
	@Test
	public void testCreateCalibrationTestException(){
		String verificationId = "id";
		CalibrationTest mockCalibrationTest = mock(CalibrationTest.class);
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(null);
		exception.expect(NotAvailableException.class);
		exception.expectMessage("Повірки з таким ID не існує");
		mockCalibrationTest = verificationService.createCalibrationTest(verificationId, mockCalibrationTest);
	}
	
	@Test
	public void testCreateCalibrationTest(){
		String verificationId = "id";
		CalibrationTest mockCalibrationTest = mock(CalibrationTest.class);
		CalibrationTest spyCalibrationTest = spy(new CalibrationTest());
		when(mockVerificationRepository.findOne(verificationId)).thenReturn(mockVerification);
		when(mockCalibrationTestRepository.save(mockCalibrationTest)).thenReturn(spyCalibrationTest);
		CalibrationTest calibrationTest = verificationService.createCalibrationTest(verificationId, mockCalibrationTest);
		verify(calibrationTest, times(1)).setVerification(mockVerification);
	}
	
	@Test
	public void testFindByCalibrationTestIdException(){
		long id = 1;
		exception.expect(AccessDeniedException.class);
		exception.expectMessage("You have not permission to get this data");
		verificationService.findByCalibrationTestId(id);
	}
	
	@Test
	public void testFindByCalibrationTestId(){
		long id = 1;
		CalibrationTest expected = mock(CalibrationTest.class);
		when(mockCalibrationTestRepository.findById(id)).thenReturn(expected);
		CalibrationTest actual = verificationService.findByCalibrationTestId(id);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFindCountOfAllSentVerifications(){
		int expected = 0;
		Organization mockOrganization = mock(Organization.class);
		when(mockVerificationRepository.getCountOfAllSentVerifications(mockOrganization)).thenReturn(expected);
		int actual = verificationService.findCountOfAllSentVerifications(mockOrganization);
		Assert.assertEquals(expected, actual);
	}
	

	@Test
	public void testFindCountOfAllAcceptedVerification(){
		int expected = 0;
		Organization mockOrganization = mock(Organization.class);
		when(mockVerificationRepository.getCountOfAllAcceptedVerifications(mockOrganization)).thenReturn(expected);
		int actual = verificationService.findCountOfAllAcceptedVerification(mockOrganization);
		Assert.assertEquals(expected, actual);
	}
	
}

