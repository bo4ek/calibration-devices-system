package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.repository.UploadBbiRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.calibrator.BbiFileService;
import com.softserve.edu.service.parser.DeviceTestDataParser;
import com.softserve.edu.service.parser.DeviceTestDataParserFactory;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


@Service
public class BbiFileServiceImpl implements BbiFileService {

    @Autowired
    private UploadBbiRepository uploadBbiRepository;

    @Value("${bbi.storage.local}")
    private String bbiLocalStorage;

    private DeviceTestDataParserFactory testDataParserFactory = new DeviceTestDataParserFactory();

    @Override
    @Transactional
    public File findBbiFileByFileName(String fileName) {
        String verificationId = uploadBbiRepository.findVerificationIdByFileName(fileName);
        String absolutePath = bbiLocalStorage + verificationId + "/" + fileName;
        File file = new File(absolutePath);
        return file;
    }

    @Override
    public String findBBIByFileName(String fileName){
        String bbiName = uploadBbiRepository.findBBIProtocolByFileName(fileName);
        return bbiName;
    }


    @Override
    public DeviceTestData parseBbiFile(InputStream fileStream, String fileName) throws IOException, DecoderException {
        DeviceTestDataParser parser = testDataParserFactory.getParser(fileName);
        DeviceTestData parsedData = parser.parse(fileStream);
        return parsedData;
    }

}
