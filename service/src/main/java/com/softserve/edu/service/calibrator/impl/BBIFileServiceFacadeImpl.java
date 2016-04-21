package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.catalogue.Street;
import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.repository.CalibrationModuleRepository;
import com.softserve.edu.service.admin.CounterTypeService;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.calibrator.BBIFileServiceFacade;
import com.softserve.edu.service.calibrator.BbiFileService;
import com.softserve.edu.service.calibrator.CalibratorService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.service.catalogue.DistrictService;
import com.softserve.edu.service.catalogue.LocalityService;
import com.softserve.edu.service.catalogue.RegionService;
import com.softserve.edu.service.catalogue.StreetService;
import com.softserve.edu.service.exceptions.*;
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

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.sql.*;
import java.text.DateFormat;
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

    private final Logger logger = Logger.getLogger(BBIFileServiceFacadeImpl.class.getSimpleName());

    @Autowired
    private BbiFileService bbiFileService;

    @Autowired
    private CalibratorService calibratorService;

    @Autowired
    private CalibrationTestService calibrationTestService;

    @Autowired
    private CalibrationModuleRepository calibrationModuleRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private CounterTypeService counterTypeService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private StreetService streetService;

    @Autowired
    private RegionService regionService;

    @Transactional
    public DeviceTestData parseBBIFile(File BBIFile, String originalFileName) throws IOException, DecoderException, InvalidImageInBbiException {
        inStream = FileUtils.openInputStream(BBIFile);
        bufferedInputStream = new BufferedInputStream(inStream);
        bufferedInputStream.mark(inStream.available());
        DeviceTestData deviceTestData = bbiFileService.parseBbiFile(bufferedInputStream, originalFileName);
        bufferedInputStream.reset();
        return deviceTestData;
    }

    public void saveBBIFile(DeviceTestData deviceTestData, String verificationID, String originalFileName) throws IOException, InvalidModuleIdException, DuplicateCalibrationTestException {
        calibratorService.uploadBbi(bufferedInputStream, verificationID, originalFileName);
        calibrationTestService.createNewTest(deviceTestData, verificationID);
        bufferedInputStream.close();
        inStream.close();
    }

    @Override
    public DeviceTestData parseAndSaveBBIFile(File BBIfile, String verificationID, String originalFileName)
            throws IOException, DecoderException, InvalidImageInBbiException, InvalidModuleIdException, DuplicateCalibrationTestException {
        DeviceTestData deviceTestData;
        try (InputStream inputStream = FileUtils.openInputStream(BBIfile)) {
            deviceTestData = parseAndSaveBBIFile(inputStream, verificationID, originalFileName);
            calibrationTestService.createNewTest(deviceTestData, verificationID);
        } catch (DecoderException e) {
            logger.error("error ", e);
        }
        return null;
    }

    public DeviceTestData parseAndSaveBBIFile(MultipartFile BBIfile, String verificationID, String originalFileName)
            throws IOException, NoSuchElementException, DecoderException {
        try {
            return parseAndSaveBBIFile(BBIfile.getInputStream(), verificationID, originalFileName);
        } catch (Exception e) {
            logger.error("Error " + e);
        }
        return null;
    }

    @Transactional
    public DeviceTestData parseAndSaveBBIFile(InputStream inputStream, String verificationID, String originalFileName)
            throws IOException, DecoderException, InvalidImageInBbiException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.mark(inputStream.available());
        DeviceTestData deviceTestData = bbiFileService.parseBbiFile(bufferedInputStream, originalFileName);
        bufferedInputStream.reset();
        calibratorService.uploadBbi(bufferedInputStream, verificationID, originalFileName);
        return deviceTestData;
    }

    public List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfiles(File archive, String originalFileName,
                                                             User calibratorEmployee)
            throws IOException, ZipException, SQLException, ClassNotFoundException, ParseException, InvalidImageInBbiException {
        try (InputStream inputStream = FileUtils.openInputStream(archive)) {
            return parseAndSaveArchiveOfBBIfiles(inputStream, originalFileName, calibratorEmployee);
        }
    }
    public List<BBIOutcomeDTO>  parseAndSaveArchiveOfBBIfilesForStation(MultipartFile archiveFile, String originalFileName,
                                                              User calibratorEmployee) throws IOException, ZipException,
            SQLException, ClassNotFoundException, ParseException, InvalidImageInBbiException {
        return parseAndSaveArchiveOfBBIfilesForStation(archiveFile.getInputStream(), originalFileName, calibratorEmployee);
    }


    public List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfiles(MultipartFile archiveFile, String originalFileName,
                                                             User calibratorEmployee) throws IOException, ZipException,
            SQLException, ClassNotFoundException, ParseException, InvalidImageInBbiException {
        return parseAndSaveArchiveOfBBIfiles(archiveFile.getInputStream(), originalFileName, calibratorEmployee);
    }

    @Transactional
    public List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfilesForStation(InputStream archiveStream, String originalFileName,
                                                             User calibratorEmployee) throws IOException,
            ZipException, SQLException, ClassNotFoundException, ParseException, InvalidImageInBbiException {
        File directoryWithUnpackedFiles = unpackArchive(archiveStream, originalFileName);
        List<File> listOfBBIfiles = new ArrayList<>(FileUtils.listFiles(directoryWithUnpackedFiles, bbiExtensions, true));
        List<BBIOutcomeDTO> resultsOfBBIProcessing = processListOfBBIFilesForStation(listOfBBIfiles,
                calibratorEmployee);
        FileUtils.forceDelete(directoryWithUnpackedFiles);
        return resultsOfBBIProcessing;
    }

    @Transactional
    public List<BBIOutcomeDTO> parseAndSaveArchiveOfBBIfiles(InputStream archiveStream, String originalFileName,
                                                             User calibratorEmployee) throws IOException,
            ZipException, SQLException, ClassNotFoundException, ParseException, InvalidImageInBbiException {
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
            ParseException, IOException, InvalidImageInBbiException {
        List<BBIOutcomeDTO> resultsOfBBIProcessing = new ArrayList<>();

        String archiveBbi = "../archiveBbi";
        File dir = new File(archiveBbi);
        if (!dir.exists()) dir.mkdir();

        for (File bbiFile : listOfBBIfiles) {
            Map<String, String> correspondingVerificationMap = verificationMapFromUnpackedFiles.get(bbiFile.getName());
            String correspondingVerification = null;
            BBIOutcomeDTO.ReasonOfRejection reasonOfRejection = null;
            DeviceTestData deviceTestData;
            String calibrationModuleNumber;

            try {
                if (correspondingVerificationMap == null) {
                    throw new MismatchBbiFilesNamesException();
                }
                deviceTestData = parseBBIFile(bbiFile, bbiFile.getName());
                calibrationModuleNumber = deviceTestData.getInstallmentNumber();

                String moduleDirectory = archiveBbi + "/" + calibrationModuleNumber;
                File theDir = new File(moduleDirectory);
                if (!theDir.exists()) theDir.mkdir();

                File fileDir = new File(moduleDirectory + "/" + bbiFile.getName());
                try {
                    FileUtils.copyFile(bbiFile, fileDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CalibrationModule calibrationModule = calibrationModuleRepository.findByModuleNumber(calibrationModuleNumber);
                if (calibrationModule == null) {
                    throw new InvalidModuleIdException();
                }
                if (bbiFileService.findByFileNameAndDate(bbiFile.getName(), correspondingVerificationMap.get(Constants.DATE), calibrationModule.getModuleNumber())) {
                    throw new FileAlreadyExistsException(bbiFile.getName());
                }
                correspondingVerification = correspondingVerificationMap.get(Constants.VERIFICATION_ID);
                if (correspondingVerification == null) {
                    correspondingVerification = createNewVerificationFromMap(correspondingVerificationMap,
                            calibratorEmployee, deviceTestData);

                    saveBBIFile(deviceTestData, correspondingVerification, bbiFile.getName());
                    Verification verification = verificationService.findById(correspondingVerification);
                    verification.setCalibrationModule(calibrationModule);
                    verification.setStatus(Status.CREATED_BY_CALIBRATOR);
                    verificationService.saveVerification(verification);
                } else {
                    Verification verification = verificationService.findById(correspondingVerification);
                    if (verification == null) {
                        throw new InvalidVerificationCodeException();
                    }
                    if (!verification.getCalibrator().getId().equals(calibratorEmployee.getOrganization().getId())) {
                        throw new IncorrectOrganizationException();
                    }
                    updateVerificationFromMap(correspondingVerificationMap, verification, deviceTestData);
                    saveBBIFile(deviceTestData, correspondingVerification, bbiFile.getName());
                }
            } catch (MismatchBbiFilesNamesException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.NAME_OF_BBI_FILE_DOES_NOT_MATCH;
                logger.error("Mismatch of bbi file names");
            } catch (FileAlreadyExistsException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.BBI_FILE_IS_ALREADY_IN_DATABASE;
                logger.error("BBI file is already in database");
            } catch (IOException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.BBI_IS_NOT_VALID;
                logger.error("BBI is not valid");
            } catch (InvalidImageInBbiException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INVALID_IMAGE_IN_BBI;
                logger.error("Wrong image in BBI file");
            } catch (InvalidModuleIdException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INVALID_MODULE_ID;
                logger.error("Wrong module serial number in BBI file");
            } catch (InvalidDeviceTypeIdException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INVALID_DEVICE_TYPE_ID;
                logger.error("Wrong device type id in BBI file");
            } catch (InvalidVerificationCodeException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INVALID_VERIFICATION_CODE;
                logger.error("Invalid verification code");
            } catch (IncorrectCityIdException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INCORRECT_CITY_ID;
                logger.error("Incorrect city id");
            } catch (IncorrectStreetIdException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INCORRECT_STREET_ID;
                logger.error("Incorrect street id");
            } catch (IncorrectOrganizationException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INCORRECT_ORGANIZATION;
                logger.error("Incorrect street id");
            } catch (InvalidSymbolAndStandardSizeException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INCORRECT_SYMBOL_AND_STANDARD_SIZE;
                logger.error("Incorrect symbol and standard size");
            } catch (DuplicateCalibrationTestException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.DUPLICATE_CALIBRATION_TEST;
                logger.error("Duplicate calibration test");
            } catch (Exception e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.UNKNOWN_REASON_OF_REJECTION;
                logger.error("Unknown reason of rejection");
            } finally {
                if (inStream != null) {
                    bufferedInputStream.close();
                    inStream.close();
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


    private Verification findVerificationByDeviceTestData(DeviceTestData deviceTestData, User calibratorEmployee) throws InvalidVerificationCodeException {
        List<Verification> verifications = verificationService.findByCounterNumberAndCalibratorId(deviceTestData.getCurrentCounterNumber(), calibratorEmployee.getOrganization().getId());
        if(verifications.size() == 0) {
            throw new InvalidVerificationCodeException();
        }
        return verifications.get(0);
    }

    private List<BBIOutcomeDTO> processListOfBBIFilesForStation(List<File> listOfBBIfiles, User calibratorEmployee) throws
            ParseException, IOException, InvalidImageInBbiException {
        List<BBIOutcomeDTO> resultsOfBBIProcessing = new ArrayList<>();

        for (File bbiFile : listOfBBIfiles) {
            String correspondingVerification = null;
            BBIOutcomeDTO.ReasonOfRejection reasonOfRejection = null;
            DeviceTestData deviceTestData;
            try {
                deviceTestData = parseBBIFile(bbiFile, bbiFile.getName());
                Date date = new Date(deviceTestData.getYear(), deviceTestData.getMonth(), deviceTestData.getDay(), deviceTestData.getHour(),
                        deviceTestData.getMinute(), deviceTestData.getSecond());
                DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy HH:mm:ss");
                String formattedDate = dateFormat.format(date);

                CalibrationModule calibrationModule = calibrationModuleRepository.findByModuleNumber(deviceTestData.getInstallmentNumber());
                if (calibrationModule == null) {
                    throw new InvalidModuleIdException();
                }
                if (bbiFileService.findByFileNameAndDate(bbiFile.getName(), formattedDate, calibrationModule.getModuleNumber())) {
                    throw new FileAlreadyExistsException(bbiFile.getName());
                }
                Verification verification = findVerificationByDeviceTestData(deviceTestData, calibratorEmployee);
                if (verification == null) {
                    throw new InvalidVerificationCodeException();
                }
                if (!verification.getCalibrator().getId().equals(calibratorEmployee.getOrganization().getId())) {
                    throw new IncorrectOrganizationException();
                }
                correspondingVerification = verification.getId();
                updateVerificationFromMapForStation(date, formattedDate, verification, deviceTestData);
                saveBBIFile(deviceTestData, correspondingVerification, bbiFile.getName());
            }  catch (FileAlreadyExistsException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.BBI_FILE_IS_ALREADY_IN_DATABASE;
                logger.error("BBI file is already in database");
            } catch (IOException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.BBI_IS_NOT_VALID;
                logger.error("BBI is not valid");
            } catch (InvalidImageInBbiException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INVALID_IMAGE_IN_BBI;
                logger.error("Wrong image in BBI file");
            } catch (InvalidModuleIdException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INVALID_MODULE_ID;
                logger.error("Wrong module serial number in BBI file");
            }  catch (InvalidVerificationCodeException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INVALID_VERIFICATION_CODE;
                logger.error("Invalid verification code");
            } catch (DuplicateCalibrationTestException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.DUPLICATE_CALIBRATION_TEST;
                logger.error("Duplicate calibration test");
            } catch (IncorrectOrganizationException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INCORRECT_ORGANIZATION;
                logger.error("Incorrect street id");
            } catch (InvalidSymbolAndStandardSizeException e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.INCORRECT_SYMBOL_AND_STANDARD_SIZE;
                logger.error("Incorrect symbol and standard size");
            } catch (Exception e) {
                reasonOfRejection = BBIOutcomeDTO.ReasonOfRejection.UNKNOWN_REASON_OF_REJECTION;
                logger.error("Unknown reason of rejection");
            } finally {
                if (inStream != null) {
                    bufferedInputStream.close();
                    inStream.close();
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Results");
            while (resultSet.next()) {
                verificationMap = new LinkedHashMap<>();
                verificationMap.put(Constants.VERIFICATION_ID, resultSet.getString("Id_pc"));
                verificationMap.put(Constants.LAST_NAME, resultSet.getString("Surname"));
                verificationMap.put(Constants.FIRST_NAME, resultSet.getString("Name"));
                verificationMap.put(Constants.MIDDLE_NAME, resultSet.getString("Middlename"));
                verificationMap.put(Constants.CITY, resultSet.getString("City"));
                verificationMap.put(Constants.REGION, resultSet.getString("District"));
                verificationMap.put(Constants.STREET, resultSet.getString("Street"));
                verificationMap.put(Constants.BUILDING, resultSet.getString("Building"));
                verificationMap.put(Constants.FLAT, resultSet.getString("Apartment"));
                verificationMap.put(Constants.STAMP, resultSet.getString("Account"));
                verificationMap.put(Constants.COUNTER_NUMBER, resultSet.getString("CounterNumber"));
                verificationMap.put(Constants.COUNTER_SIZE_AND_SYMBOL, resultSet.getString("Type"));
                verificationMap.put(Constants.YEAR, resultSet.getString("Year"));
                verificationMap.put(Constants.PHONE_NUMBER, resultSet.getString("TelNumber"));
                verificationMap.put(Constants.PROVIDER, resultSet.getString("Customer"));
                verificationMap.put(Constants.DATE, resultSet.getString("Date"));

                try {
                    verificationMap.put(Constants.CITY_ID, resultSet.getString("CityID"));
                    verificationMap.put(Constants.DISTRICT_ID, resultSet.getString("DistrictID"));
                    verificationMap.put(Constants.STREET_ID, resultSet.getString("StreetID"));
                    verificationMap.put(Constants.CUSTOMER_ID, resultSet.getString("CustomerID"));

                    verificationMap.put(Constants.COMMENT, resultSet.getString("Note"));
                } catch (SQLException e) {
                    logger.warn("User was trying to upload old archive format ");
                }

                bbiFilesToVerification.put(resultSet.getString("FileNumber"), verificationMap);
            }
        }
        return bbiFilesToVerification;
    }

    private String createNewVerificationFromMap(Map<String, String> verificationData, User calibratorEmployee, DeviceTestData deviceTestData)
            throws ParseException, InvalidDeviceTypeIdException, IncorrectCityIdException, IncorrectStreetIdException, InvalidSymbolAndStandardSizeException {

        String regionName = null;
        String districtName = null;
        String cityName = null;
        String streetName = null;
        ClientData clientData;

        try {
            /*Long districtIdLong = Long.parseLong(verificationData.get(Constants.DISTRICT_ID));

            Long cityIdLong = Long.parseLong(verificationData.get(Constants.CITY_ID));
            boolean isExistLocality = localityService.existByIdAndDistrictId(cityIdLong, districtIdLong);
            if(!isExistLocality) {
                throw new IncorrectCityIdException();
            }*/
            Long streetIdLong = Long.parseLong(verificationData.get(Constants.STREET_ID));
            Street street = streetService.findStreetById(streetIdLong);

            if (street == null) {
                throw new IncorrectStreetIdException();
            }
            regionName = street.getLocality().getDistrict().getRegion().getDesignation();
            districtName = street.getLocality().getDistrict().getDesignation();
            cityName = street.getLocality().getDesignation();
            streetName = street.getDesignation();
        } catch (NumberFormatException e) {
            logger.info("Old *.db format", e);
        }

        if (cityName != null && regionName != null && streetName != null) {
            Address address = new Address(regionName, districtName, cityName,
                    streetName, verificationData.get(Constants.BUILDING), verificationData.get(Constants.FLAT));
            clientData = new ClientData(verificationData.get(Constants.FIRST_NAME),
                    verificationData.get(Constants.LAST_NAME), verificationData.get(Constants.MIDDLE_NAME),
                    verificationData.get(Constants.PHONE_NUMBER), address);
        } else {
            Address address = new Address(verificationData.get(Constants.REGION), verificationData.get(Constants.CITY),
                    verificationData.get(Constants.STREET), verificationData.get(Constants.BUILDING),
                    verificationData.get(Constants.FLAT));
            clientData = new ClientData(verificationData.get(Constants.FIRST_NAME),
                    verificationData.get(Constants.LAST_NAME), verificationData.get(Constants.MIDDLE_NAME),
                    verificationData.get(Constants.PHONE_NUMBER), address);
        }

        Long calibratorOrganisationId = calibratorEmployee.getOrganization().getId();
        Organization calibrator = organizationService.getOrganizationById(calibratorOrganisationId);
        Counter counter = getCounterFromVerificationData(verificationData, deviceTestData);
        Date date = new SimpleDateFormat(Constants.FULL_DATE).parse(verificationData.get(Constants.DATE));
        Organization providerFromBBI = organizationService.getOrganizationById(Long.parseLong(verificationData.get(Constants.CUSTOMER_ID)));
        Long deviceId = getDeviceIdByDeviceTypeId(getDeviceTypeIdByTemperature(deviceTestData.getTemperature()));
        Device device = deviceService.getById(deviceId);

        Verification verification = new Verification(date, clientData, Status.CREATED_BY_CALIBRATOR, calibrator,
                providerFromBBI, calibratorEmployee, counter, null, verificationData.get(Constants.COMMENT),
                verificationData.get(Constants.DATE), device);

        List<String> listWithOneId = verificationService.saveVerificationCustom(verification, Constants.ONE_VERIFICATION,
                device.getDeviceType());

        return listWithOneId.get(Constants.FIRST_INDEX_IN_ARRAY);
    }

    private void updateVerificationFromMap(Map<String, String> verificationData, Verification verification, DeviceTestData deviceTestData) throws InvalidSymbolAndStandardSizeException, ParseException {
        Long deviceId;
        Integer deviceTypeId = getDeviceTypeIdByTemperature(deviceTestData.getTemperature());
        if (deviceTypeId != null) {
            deviceId = getDeviceIdByDeviceTypeId(deviceTypeId);
            Device device = deviceService.getById(deviceId);
            verification.setDevice(device);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        verification.setVerificationDate(formatter.parse(verificationData.get(Constants.DATE)));
        Counter counter = getCounterFromVerificationData(verificationData, deviceTestData);
        verification.setCounter(counter);
        verificationService.saveVerification(verification);

    }

    private void updateVerificationFromMapForStation(Date verificationDate, String date, Verification verification, DeviceTestData deviceTestData) throws InvalidSymbolAndStandardSizeException {
        Long deviceId;
        Integer deviceTypeId = getDeviceTypeIdByTemperature(deviceTestData.getTemperature());
        if (deviceTypeId != null) {
            deviceId = getDeviceIdByDeviceTypeId(deviceTypeId);
            Device device = deviceService.getById(deviceId);
            verification.setDevice(device);
        }
        verification.setVerificationTime(date);
        verification.setVerificationDate(verificationDate);
        updateCounterFromDeviceTestData(verification.getCounter(), deviceTestData);
        verificationService.saveVerification(verification);

    }

    private Integer getDeviceTypeIdByTemperature(int temperature) {
        if (temperature >= 0 && temperature <= 30) {
            return Constants.WATER_ID;
        } else if (temperature > 30 && temperature <= 90) {
            return Constants.THERMAL_ID;
        }
        return null;
    }

    private Long getDeviceIdByDeviceTypeId(Integer deviceTypeId) {
        String deviceType;
        switch (deviceTypeId) {
            case 2:
                deviceType = "THERMAL";
                break;
            default:
                deviceType = "WATER";
                break;
        }
        return deviceService.getByDeviceTypeAndDefaultDevice(deviceType, Constants.DEFAULT_DEVICE).getId();
    }

    private Counter getCounterFromVerificationData(Map<String, String> verificationData, DeviceTestData deviceTestData) throws InvalidSymbolAndStandardSizeException {
        String sizeAndSymbol = verificationData.get(Constants.COUNTER_SIZE_AND_SYMBOL);
        String[] parts = sizeAndSymbol.split(" ");
        String standardSize = parts[0] + " " + parts[1];
        String symbol = parts[2];
        if (parts.length > Constants.MIN_LENGTH) {
            for (int i = Constants.MIN_LENGTH; i < parts.length; i++) {
                symbol += " " + parts[i];
            }
        }
        List<CounterType> counterTypes = counterTypeService.findBySymbolAndStandardSize(symbol, standardSize);
        CounterType resultCounterType;
        switch (counterTypes.size()) {
            case 0: {
                throw new InvalidSymbolAndStandardSizeException();
            }
            case 1: {
                resultCounterType = counterTypes.get(0);
                break;
            }
            case 2: {
                Long deviceId = getDeviceIdByDeviceTypeId(getDeviceTypeIdByTemperature(deviceTestData.getTemperature()));
                Device.DeviceType deviceType = deviceService.getDeviceTypeById(deviceId);
                resultCounterType = getCounterTypeByDeviceType(counterTypes, deviceType);
                break;
            }
            default: {
                resultCounterType = counterTypes.get(0);
            }
        }
        return new Counter(verificationData.get(Constants.YEAR),
                verificationData.get(Constants.COUNTER_NUMBER), resultCounterType, verificationData.get(Constants.STAMP));
    }

    private void updateCounterFromDeviceTestData(Counter counter, DeviceTestData deviceTestData) throws InvalidSymbolAndStandardSizeException {
        String sizeAndSymbol = deviceTestData.getCounterType1() + deviceTestData.getCounterType2();
        String[] parts = sizeAndSymbol.split(" ");
        String standardSize = parts[0] + " " + parts[1];
        String symbol = parts[2];
        if (parts.length > Constants.MIN_LENGTH) {
            for (int i = Constants.MIN_LENGTH; i < parts.length; i++) {
                symbol += " " + parts[i];
            }
        }
        List<CounterType> counterTypes = counterTypeService.findBySymbolAndStandardSize(symbol, standardSize);
        CounterType resultCounterType;
        switch (counterTypes.size()) {
            case 0: {
                throw new InvalidSymbolAndStandardSizeException();
            }
            case 1: {
                resultCounterType = counterTypes.get(0);
                break;
            }
            case 2: {
                Long deviceId = getDeviceIdByDeviceTypeId(getDeviceTypeIdByTemperature(deviceTestData.getTemperature()));
                Device.DeviceType deviceType = deviceService.getDeviceTypeById(deviceId);
                resultCounterType = getCounterTypeByDeviceType(counterTypes, deviceType);
                break;
            }
            default: {
                resultCounterType = counterTypes.get(0);
            }
        }
        counter.setReleaseYear(String.valueOf(deviceTestData.getYear()));
        counter.setAccumulatedVolume(deviceTestData.getInitialCapacity());
        counter.setCounterType(resultCounterType);
    }

    private CounterType getCounterTypeByDeviceType(List<CounterType> counterType, Device.DeviceType deviceType) {
        for (CounterType type : counterType) {
            if (type.getDevice().getDeviceType().equals(deviceType)) {
                return type;
            }
        }
        return null;
    }
}