package com.softserve.edu.service.verification;

import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.tool.impl.MailServiceImpl;
import com.softserve.edu.service.verification.impl.VerificationProviderEmployeeServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Veronika Herasymenko
 */
@RunWith(MockitoJUnitRunner.class)
public class VerificationProviderEmployeeServiceImplTest {

    @Mock
    VerificationRepository verificationRepository;

    @Mock
    UserRepository userRepository;

    @Spy
    Verification verification;

    @Mock
    MailServiceImpl mailServiceImpl;

    @InjectMocks
    VerificationProviderEmployeeService verificationProviderEmployeeService = new VerificationProviderEmployeeServiceImpl();

    @Test
    public void testAssignProviderEmployeeUserIsNotNull() {
        String verificationID = " ";
        String mail = "verification@mail.com";
        User mockUser = mock(User.class);
        ClientData mockClientData = mock(ClientData.class);
        Device mockDevice = mock(Device.class);
        Device.DeviceType type = Device.DeviceType.THERMAL;
        String device = type.name();

        when(verificationRepository.findOne(anyString())).thenReturn(verification);

        when(verification.getClientData()).thenReturn(mockClientData);
        when(mockClientData.getEmail()).thenReturn(mail);
        when(verification.getDevice()).thenReturn(mockDevice);
        when(mockDevice.getDeviceType()).thenReturn(type);

        verificationProviderEmployeeService.assignProviderEmployee(verificationID, mockUser);

        verify(verification).setProviderEmployee(mockUser);
        verify(verification).setStatus(Status.ACCEPTED);
        verify(mailServiceImpl).sendAcceptMail(mail, verificationID, device);

        verify(verification).setReadStatus(Verification.ReadStatus.READ);
        verify(verification).setExpirationDate(any(Date.class));
        verify(verificationRepository).save(verification);
    }

    @Test
    public void testAssignProviderEmployeeUserIsNull() {
        String verificationID = " ";
        User user = null;

        when(verificationRepository.findOne(anyString())).thenReturn(verification);

        verificationProviderEmployeeService.assignProviderEmployee(verificationID, null);

        verify(verification).setProviderEmployee(null);
        verify(verification).setStatus(Status.SENT);
        verify(verification).setReadStatus(Verification.ReadStatus.READ);
        verify(verification).setExpirationDate(any(Date.class));
        verify(verificationRepository).save(verification);
    }

    @Test
    public void testGetProviderEmployeeById() {
        User expectedUser = mock(User.class);
        when(verificationRepository.getProviderEmployeeById(anyString())).thenReturn(expectedUser);

        Assert.assertEquals(expectedUser,verificationProviderEmployeeService.getProviderEmployeeById(anyString()));

    }

    @Test
    public void testGetVerificationListbyProviderEmployee() {
        List<Verification> expected = Collections.singletonList(verification);
        String username = "username";
        when(verificationRepository.findByProviderEmployeeUsernameAndStatus(username, Status.IN_PROGRESS)).thenReturn(expected);

        Assert.assertEquals(expected, verificationProviderEmployeeService.getVerificationListByProviderEmployee(username));
    }

    @Test
    public void testGetVerificationListbyCalibratormployee() {
        List<Verification> expected = Collections.singletonList(verification);
        String username = "username";
        when(verificationRepository.findByCalibratorEmployeeUsernameAndStatus(username, Status.IN_PROGRESS)).thenReturn(expected);

        Assert.assertEquals(expected, verificationProviderEmployeeService.getVerificationListByCalibratorEmployee(username));

    }

    @Test
    public void testCountByProviderEmployeeTasks() {
        Long expected = 1L;
        String username = "username";
        when(verificationRepository.countByProviderEmployeeUsernameAndStatus(username, Status.IN_PROGRESS)).thenReturn(expected);

        Assert.assertEquals(expected, verificationProviderEmployeeService.countByProviderEmployeeTasks(username));

    }

    @Test
    public void testCountByCalibratorEmployeeTasks() {
        String username = "username";
        Long expected = 1L;
        when(verificationRepository.countByCalibratorEmployeeUsernameAndStatus(username, Status.IN_PROGRESS)).thenReturn(expected);

        Assert.assertEquals(expected, verificationProviderEmployeeService.countByCalibratorEmployeeTasks(username));
    }

    @Test
    public void testOneProviderEmployee() {
        User expectedUser = mock(User.class);
        when(userRepository.findOne(anyString())).thenReturn(expectedUser);

        Assert.assertEquals(expectedUser, verificationProviderEmployeeService.oneProviderEmployee(anyString()));
    }
}