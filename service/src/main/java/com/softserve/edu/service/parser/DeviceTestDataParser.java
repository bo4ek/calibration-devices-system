package com.softserve.edu.service.parser;

import java.io.IOException;
import java.io.InputStream;
import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.service.exceptions.InvalidImageInBbiException;
import org.apache.commons.codec.DecoderException;

/**
 * Created by Taras on 15.09.2015.
 */
public interface DeviceTestDataParser {
    DeviceTestData parse(InputStream deviceTestData) throws IOException, DecoderException, InvalidImageInBbiException;

}
