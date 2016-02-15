package com.softserve.edu.service.calibrator;

import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.entity.verification.BbiProtocol;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.service.exceptions.InvalidImageInBbiException;
import org.apache.commons.codec.DecoderException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This service provides functionality of getting Bbi file and its content.
 */
public interface BbiFileService {

    File findBbiFileByFileName(String fileName);

    boolean findByFileNameAndDate(String fileName, String date);

    DeviceTestData parseBbiFile(InputStream fileStream, String fileName) throws IOException, DecoderException, InvalidImageInBbiException;
}
