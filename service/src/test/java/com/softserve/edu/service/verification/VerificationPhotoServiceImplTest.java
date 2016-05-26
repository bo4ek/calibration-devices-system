package com.softserve.edu.service.verification;

import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.service.storage.FileOperations;
import com.softserve.edu.service.verification.impl.VerificationPhotoServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.nio.file.FileSystems;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VerificationPhotoServiceImplTest {

    // mocking fields of the tested class
    @Mock private CalibrationTestService calibrationTestService;
    @Mock private FileOperations fileOperations;
    private final String separator = FileSystems.getDefault().getSeparator();

    // mocking putResource() arguments
    @Mock private InputStream inputStream;
    private final long TEST_ID = 1234L;
    private final String FILETYPE = ".jpeg";

    // mocking local variables of purResource()
    @Mock private CalibrationTest calibrationTest;
    @Mock private Verification verification;
    private final String FILEPATH = "folder/image.jpeg";
    private final String VER_ID = "1a2b";
    private final String relfolder = VER_ID + separator + TEST_ID + separator;

    @InjectMocks
    private VerificationPhotoServiceImpl verificationPhotoServiceImpl;

    @Before
    public void setUp() {
        when(calibrationTestService.findTestById(TEST_ID)).thenReturn(calibrationTest);
        when(calibrationTest.getVerification()).thenReturn(verification);
        when(verification.getId()).thenReturn(VER_ID);
        when(fileOperations.putResourse(inputStream, relfolder, FILETYPE)).thenReturn(FILEPATH);
    }

    @Test
    public void testPutResource() {
        boolean actual = verificationPhotoServiceImpl.putResource(TEST_ID, inputStream, FILETYPE);
        verify(calibrationTestService).findTestById(TEST_ID);
        verify(calibrationTest).getVerification();
        verify(verification).getId();
        verify(fileOperations).putResourse(inputStream, relfolder, FILETYPE);
        verify(calibrationTest).setPhotoPath(FILEPATH);
        assertTrue("the method returns false instead of true", actual);
    }
}