package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.service.admin.CounterTypeService;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.calibrator.BBIFileServiceFacade;
import com.softserve.edu.service.calibrator.BbiFileService;
import com.softserve.edu.service.calibrator.CalibratorService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.service.tool.DeviceService;
import com.softserve.edu.service.utils.BBIOutcomeDTO;
import com.softserve.edu.service.verification.VerificationService;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.metal.MetalLookAndFeel;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

@Service
public class BBIFileServiceFacadeImpl implements BBIFileServiceFacade {
    private static final String[] bbiExtensions = {"bbi", "BBI"};
    private static final String[] dbfExtensions = {"db", "dbf", "DB", "DBF"};
    private static InputStream inStream;
    private static BufferedInputStream bufferedInputStream;

    private final Logger logger = Logger.getLogger(BBIFileServiceFacadeImpl.class);


    @Autowired
    private BbiFileService bbiFileService;

    @Autowired
    private CalibratorService calibratorService;

    @Autowired
    private CalibrationTestService calibrationTestService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private CounterTypeService counterTypeService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private OrganizationService organizationService;

    @Transactional
    public DeviceTestData parseBBIFileWithoutSaving(File BBIFile, String originalFileName) {
        DeviceTestData deviceTestData = null;
        try {
            inStream = FileUtils.openInputStream(BBIFile);
            bufferedInputStream = new BufferedInputStream(inStream);
            bufferedInputStream.mark(inStream.available());
            deviceTestData = bbiFileService.parseBbiFile(bufferedInputStream, originalFileName);
            bufferedInputStream.reset();
        } catch (Exception e) {
            logger.info(e);
        }
        return deviceTestData;
    }

    public void saveBBIFile(DeviceTestData deviceTestData, String verificationID, String originalFileName) throws IOException {
        calibratorService.uploadBbi(bufferedInputStream, verificationID, originalFileName);
        calibrationTestService.createNewTest(deviceTestData, verificationID);
    }

    @Override
    public DeviceTestData parseAndSaveBBIFile(File BBIfile, String verificationID, String originalFileName)
            throws IOException, DecoderException {
        DeviceTestData deviceTestData;
        try (InputStream inputStream = FileUtils.openInputStream(BBIfile)) {
            deviceTestData = parseAndSaveBBIFile(inputStream, verificationID, originalFileName);
            calibrationTestService.createNewTest(deviceTestData, verificationID);
        } catch (DecoderException e) {
            throw e;
        }
        return deviceTestData;
    }


    public DeviceTestData parseAndSaveBBIFile(MultipartFile BBIfile, String verificationID, String originalFileName)
            throws IOException, NoSuchElementException, DecoderException {
        DeviceTestData deviceTestData = parseAndSaveBBIFile(BBIfile.getInputStream(), verificationID, originalFileName);
        return deviceTestData;
    }

    @Transactional
    public DeviceTestData parseAndSaveBBIFile(InputStream inputStream, String verificationID, String originalFileName)
            throws IOException, DecoderException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.mark(inputStream.available());
        DeviceTestData deviceTestData = bbiFileService.parseBbiFile(bufferedInputStream, originalFileName);
        bufferedInputStream.reset();
        calibratorService.uploadBbi(bufferedInputStream, verificationID, originalFileName);
        return deviceTestData;
    }

    public List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfiles(File archive, String originalFileName,
                                                             User calibratorEmployee)
            throws IOException, ZipException, SQLException, ClassNotFoundException, ParseException {
        try (InputStream inputStream = FileUtils.openInputStream(archive)) {
            return parseAndSaveArchiveOfBBIfiles(inputStream, originalFileName, calibratorEmployee);
        }
    }


    public List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfiles(MultipartFile archiveFile, String originalFileName,
                                                             User calibratorEmployee) throws IOException, ZipException,
            SQLException, ClassNotFoundException, ParseException {
        List<BBIOutcomeDTO> resultsOfBBIProcessing = parseAndSaveArchiveOfBBIfiles(archiveFile.getInputStream(),
                originalFileName, calibratorEmployee);
        return resultsOfBBIProcessing;
    }

    @Transactional
    public List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfiles(InputStream archiveStream, String originalFileName,
                                                             User calibratorEmployee) throws IOException,
            ZipException, SQLException, ClassNotFoundException, ParseException {
        File directoryWithUnpackedFiles = unpackArchive(archiveStream, originalFileName);
        Map<String, Map<String, String>> bbiFileNamesToVerificationMap = getVerificationMapFromUnpackedFiles(
                directoryWithUnpackedFiles);
        List<File> listOfBBIfiles = new ArrayList<>(FileUtils.listFiles(directoryWithUnpackedFiles, bbiExtensions, true));
        List<BBIOutcomeDTO> resultsOfBBIProcessing = processListOfBBIFiles(bbiFileNamesToVerificationMap, listOfBBIfiles,
                calibratorEmployee);
        FileUtils.forceDelete(directoryWithUnpackedFiles);
        return resultsOfBBIProcessing;
    }

    /**
     * @param verificationMapFromUnpackedFiles Map of BBI files names to their corresponding verifications
     * @param listOfBBIfiles                   List with BBI files extracted from the archive
     * @return List of DTOs containing BBI filename, verification id, outcome of parsing (true/false), and reason of
     * rejection (if the bbi file was rejected)
     */
    private List<BBIOutcomeDTO> processListOfBBIFiles(Map<String, Map<String, String>> verificationMapFromUnpackedFiles,
                                                      List<File> listOfBBIfiles, User calibratorEmployee) throws
            ParseException {
        List<BBIOutcomeDTO> resultsOfBBIProcessing = new ArrayList<>();

        for (File bbiFile : listOfBBIfiles) {
            Map<String, String> correspondingVerificationMap = verificationMapFromUnpackedFiles.get(bbiFile.getName());
            String correspondingVerification = correspondingVerificationMap.get(Constants.VERIFICATION_ID);
            BBIOutcomeDTO.ReasonOfRejection reasonOfRejection = null;
            DeviceTestData deviceTestData = parseBBIFileWithoutSaving(bbiFile, bbiFile.getName());
            if (correspondingVerification == null) {
                try {
                    correspondingVerification = createNewVerificationFromMap(correspondingVerificationMap,
                            calibratorEmployee, deviceTestData);
                    saveBBIFile(deviceTestData, correspondingVerification, bbiFile.getName());
                    Verification verification = verificationService.findById(correspondingVerification);
                    verification.setStatus(Status.CREATED_BY_CALIBRATOR);
                    verificationService.saveVerification(verification);
                } catch (Exception e) {
                    reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.BBI_IS_NOT_VALID;
                    logger.info(e);
                }
            } else {
                try {
                    updateVerificationFromMap(correspondingVerificationMap, correspondingVerification, deviceTestData);
                    saveBBIFile(deviceTestData, correspondingVerification, bbiFile.getName());
                } catch (IOException e) {
                    reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.BBI_IS_NOT_VALID;
                    logger.info(e);
                } catch (Exception e) {
                    reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INVALID_VERIFICATION_CODE;
                    logger.info(e);
                }
            }
            if (reasonOfRejection == null) {
                resultsOfBBIProcessing.add(BBIOutcomeDTO.accept(bbiFile.getName(), correspondingVerification));
            } else {
                resultsOfBBIProcessing.add(BBIOutcomeDTO.reject(bbiFile.getName(), correspondingVerification,
                        reasonOfRejection));
            }
        }
        return resultsOfBBIProcessing;
    }

    /**
     * Unpacks file into temporary directory
     *
     * @param inputStream      InputStream representing archive file
     * @param originalFileName Name of the archive
     * @return Directory to which the archive was unpacked
     * @throws IOException
     * @throws ZipException
     */

    private File unpackArchive(InputStream inputStream, String originalFileName) throws IOException, ZipException {
        String randomDirectoryName = RandomStringUtils.randomAlphanumeric(8);
        File directoryForUnpacking = FileUtils.getFile(FileUtils.getTempDirectoryPath(), randomDirectoryName);
        FileUtils.forceMkdir(directoryForUnpacking);
        File zipFileDownloaded = FileUtils.getFile(FileUtils.getTempDirectoryPath(), originalFileName);

        try (OutputStream os = new FileOutputStream(zipFileDownloaded)) {
            IOUtils.copy(inputStream, os);
        }

        ZipFile zipFile = new ZipFile(zipFileDownloaded);
        zipFile.extractAll(directoryForUnpacking.toString());
        FileUtils.forceDelete(zipFileDownloaded);
        return directoryForUnpacking;
    }

    /**
     * @param directoryWithUnpackedFiles Directory with unpacked files (should include BBIs and DBF)
     * @return Map of BBI files names to their corresponding verifications
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     * @implNote Uses sqlite to open DBF
     */
    private Map<String, Map<String, String>> getVerificationMapFromUnpackedFiles(File directoryWithUnpackedFiles)
            throws SQLException, ClassNotFoundException, FileNotFoundException {

        Map<String, Map<String, String>> bbiFilesToVerification = new LinkedHashMap<>();
        Map<String, String> verificationMap;
        Optional<File> foundDBFile = FileUtils.listFiles(directoryWithUnpackedFiles,
                dbfExtensions, true).stream().findFirst();
        File dbFile = foundDBFile.orElseThrow(() -> new FileNotFoundException("DBF not found"));
        Class.forName("org.sqlite.JDBC");

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile)) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Results");
            while (rs.next()) {
                verificationMap = new LinkedHashMap<>();
                verificationMap.put(Constants.VERIFICATION_ID, rs.getString("Id_pc"));
                verificationMap.put(Constants.PROVIDER, rs.getString("Customer"));
                verificationMap.put(Constants.DATE, rs.getString("Date"));
                verificationMap.put(Constants.COUNTER_NUMBER, rs.getString("CounterNumber"));
                verificationMap.put(Constants.COUNTER_SIZE_AND_SYMBOL, rs.getString("Type"));
                verificationMap.put(Constants.YEAR, rs.getString("Year"));
                verificationMap.put(Constants.STAMP, rs.getString("Account"));
                verificationMap.put(Constants.LAST_NAME, rs.getString("Surname"));
                verificationMap.put(Constants.FIRST_NAME, rs.getString("Name"));
                verificationMap.put(Constants.MIDDLE_NAME, rs.getString("Middlename"));
                verificationMap.put(Constants.PHONE_NUMBER, rs.getString("TelNumber"));
                verificationMap.put(Constants.REGION, rs.getString("District"));
                verificationMap.put(Constants.CITY, rs.getString("City"));
                verificationMap.put(Constants.STREET, rs.getString("Street"));
                verificationMap.put(Constants.BUILDING, rs.getString("Building"));
                verificationMap.put(Constants.FLAT, rs.getString("Apartment"));

                try {
                    verificationMap.put(Constants.CITY_ID, rs.getString("CityID"));
                    verificationMap.put(Constants.DISTRICT_ID, rs.getString("DistrictID"));
                    verificationMap.put(Constants.STREET_ID, rs.getString("StreetID"));
                    verificationMap.put(Constants.CUSTOMER_ID, rs.getString("CustomerID"));
                }catch (SQLException e){
                    logger.info("User was trying to upload old archive format", e);
                }

                bbiFilesToVerification.put(rs.getString("FileNumber"), verificationMap);
            }
        }
        return bbiFilesToVerification;
    }

    private String createNewVerificationFromMap(Map<String, String> verificationData, User calibratorEmployee, DeviceTestData deviceTestData)
            throws ParseException {

        Address address = new Address(verificationData.get(Constants.REGION), verificationData.get(Constants.CITY),
                verificationData.get(Constants.STREET), verificationData.get(Constants.BUILDING),
                verificationData.get(Constants.FLAT));
        ClientData clientData = new ClientData(verificationData.get(Constants.FIRST_NAME),
                verificationData.get(Constants.LAST_NAME), verificationData.get(Constants.MIDDLE_NAME),
                verificationData.get(Constants.PHONE_NUMBER), address);

        Long calibratorOrganisationId = calibratorEmployee.getOrganization().getId();
        Organization calibrator = organizationService.getOrganizationById(calibratorOrganisationId);
        Counter counter = getCounterFromVerificationData(verificationData, deviceTestData);
        Date date = new SimpleDateFormat(Constants.FULL_DATE).parse(verificationData.get(Constants.DATE));
        String verId = verificationService.getNewVerificationDailyIdByDeviceType(date,
                counter.getCounterType().getDevice().getDeviceType());
        Verification verification = new Verification(date, clientData,
                Status.CREATED_BY_CALIBRATOR, calibrator, calibratorEmployee,
                counter, verId);
        String verificationId = verification.getId();
        verificationService.saveVerification(verification);
        return verificationId;
    }

    private void updateVerificationFromMap(Map<String, String> verificationData, String verificationId, DeviceTestData deviceTestData)
            throws Exception {

        Verification verification = verificationService.findById(verificationId);
        Counter counter = getCounterFromVerificationData(verificationData, deviceTestData);
        verification.setCounter(counter);

    }

    private Counter getCounterFromVerificationData(Map<String, String> verificationData, DeviceTestData deviceTestData) {

        String sizeAndSymbol = verificationData.get(Constants.COUNTER_SIZE_AND_SYMBOL);
        String[] parts = sizeAndSymbol.split(" ");
        String standardSize = parts[0] + " " + parts[1];
        String symbol = parts[2];
        if (parts.length > Constants.MIN_LENGTH) {
            for (int i = Constants.MIN_LENGTH; i < parts.length; i++) {
                symbol += " " + parts[i];
            }
        }
        CounterType counterType = counterTypeService.findOneBySymbolAndStandardSize(symbol, standardSize);

//        If there is no such symbol and standartSize of Counter in DB - create new counterType
//        with default deviceType "Лічильник холодної води" (deviceId 65466) if deviceTypeId in bbi is "1"
//        or "Лічильник гарячої води" (deviceId 65466) if deviceTypeId in bbi is "2"
//        which is already in DB
        if (counterType == null) {
            long deviceId = (long) deviceTestData.getDeviceTypeId();
            if (deviceId == 1) {
                deviceId = Constants.DEFAULT_WATER_DEVICE_ID;
            }
            if (deviceId == 2) {
                deviceId = Constants.DEFAULT_THERMAL_DEVICE_ID;
            }
            String deviceName = deviceService.getById(deviceId).getDeviceName();
            counterTypeService.addCounterType(deviceName, symbol, standardSize, null, null, null, null, deviceId);
            counterType = counterTypeService.findOneBySymbolAndStandardSize(symbol, standardSize);
        }
        Counter counter = new Counter(verificationData.get(Constants.YEAR),
                verificationData.get(Constants.COUNTER_NUMBER), counterType, verificationData.get(Constants.STAMP));
        return counter;
    }
}