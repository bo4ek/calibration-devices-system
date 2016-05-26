package com.softserve.edu.service.utils;

import com.softserve.edu.service.verification.VerificationService;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ProcessTimeChecker.class)
public class ProcessTimeCheckerTest extends TestCase {

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    ProcessTimeChecker processTimeChecker;

    @Before
    public void setUp() throws Exception {
        processTimeChecker = new ProcessTimeChecker();

        MockitoAnnotations.initMocks(this);
    }

    public void tearDown() throws Exception {
        processTimeChecker = null;
    }

    @Test
    public void testRunProcessTimeCheck() throws Exception {
        List<Object[]> providerList = mock(List.class);
        List<Object[]> calibratorList = mock(List.class);
        List<Object[]> verificatorList = mock(List.class);
        List<Object[]> anyList = new ArrayList<Object[]>();

        processTimeChecker.runProcessTimeCheck();
        when(verificationService.getProcessTimeProvider()).thenReturn(providerList);
        verify(verificationService).getProcessTimeProvider();
        when(verificationService.getProcessTimeCalibrator()).thenReturn(calibratorList);
        verify(verificationService).getProcessTimeCalibrator();
        when(verificationService.getProcessTimeVerificator()).thenReturn(verificatorList);
        verify(verificationService).getProcessTimeVerificator();
        verifyPrivate(processTimeChecker, times(3)).invoke("processTime", anyList);
    }

}