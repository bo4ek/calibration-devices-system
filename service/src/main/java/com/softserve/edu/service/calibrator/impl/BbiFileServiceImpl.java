package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.entity.verification.BbiProtocol;
import com.softserve.edu.repository.UploadBbiRepository;
import com.softserve.edu.service.calibrator.BbiFileService;
import com.softserve.edu.service.exceptions.InvalidImageInBbiException;
import com.softserve.edu.service.parser.DeviceTestDataParser;
import com.softserve.edu.service.parser.DeviceTestDataParserFactory;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


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
        return new File(absolutePath);
    }

    @Override
    public boolean findByFileNameAndDate(String fileName, String date, String moduleNumber){
        List<BbiProtocol> protocolList = uploadBbiRepository.findBBIProtocolByFileNameAndDateAndModuleNumber(fileName, date, moduleNumber);
        System.out.println(protocolList.size());
        for (BbiProtocol protocol : protocolList) {
            System.out.println(protocol.toString());

        }
        return protocolList.size() > 0;
    }

    @Override
    public DeviceTestData parseBbiFile(InputStream fileStream, String fileName, boolean taskForStation) throws IOException, DecoderException,InvalidImageInBbiException {
        DeviceTestDataParser parser = testDataParserFactory.getParser(fileName);
        return parser.parse(fileStream, taskForStation);
    }

}
