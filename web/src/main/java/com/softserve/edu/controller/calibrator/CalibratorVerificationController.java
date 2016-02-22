package com.softserve.edu.controller.calibrator;

import com.softserve.edu.controller.calibrator.util.CalibratorTestPageDTOTransformer;
import com.softserve.edu.controller.provider.util.VerificationPageDTOTransformer;
import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.dto.*;
import com.softserve.edu.dto.admin.OrganizationDTO;
import com.softserve.edu.dto.application.ClientStageVerificationDTO;
import com.softserve.edu.dto.provider.*;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.organization.OrganizationType;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.admin.UsersService;
import com.softserve.edu.service.calibrator.BBIFileServiceFacade;
import com.softserve.edu.service.calibrator.BbiFileService;
import com.softserve.edu.service.calibrator.CalibratorEmployeeService;
import com.softserve.edu.service.calibrator.CalibratorService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestDataService;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.service.provider.ProviderService;
import com.softserve.edu.service.state.verificator.StateVerificatorService;
import com.softserve.edu.service.tool.DeviceService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.utils.BBIOutcomeDTO;
import com.softserve.edu.service.utils.ListToPageTransformer;
import com.softserve.edu.service.verification.VerificationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/calibrator/verifications/", produces = "application/json")
public class CalibratorVerificationController {

    private static final String contentExtensionPattern = "^.*\\.(bbi|BBI|)$";
    private static final String archiveExtensionPattern = "^.*\\.(zip|ZIP|)$";

    private final Logger logger = Logger.getLogger(CalibratorVerificationController.class.getSimpleName());

    @Autowired
    VerificationService verificationService;

    @Autowired
    ProviderService providerService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    CalibratorService calibratorService;

    @Autowired
    CalibratorEmployeeService calibratorEmployeeService;

    @Autowired
    CalibrationTestService testService;

    @Autowired
    CalibrationTestDataService testDataService;

    @Autowired
    StateVerificatorService verificatorService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    UsersService usersService;

    @Autowired
    BbiFileService bbiFileService;

    @Autowired
    BBIFileServiceFacade bbiFileServiceFacade;

    @RequestMapping(value = "edit/{verificationID}", method = RequestMethod.PUT)
    public ResponseEntity editVerification(@RequestBody OrganizationStageVerificationDTO verificationDTO,
                                           @PathVariable String verificationID,
                                           @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        HttpStatus httpStatus = HttpStatus.OK;
        Organization calibrator = calibratorService.findById(employeeUser.getOrganizationId());
        Verification verification = verificationService.findById(verificationID);

        if (calibrator == null || verification == null) {
            throw new RuntimeException();
        }
        updateVerificationData(verification, verificationDTO, calibrator);
        try {
            verificationService.saveVerification(verification);
            logger.info("Verification was saved with parameters: "+verification);
        } catch (Exception e) {
            logger.error("GOT EXCEPTION WHILE UPDATING VERIFICATION", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Receives bbi file, saves it in the system, parses it and
     * returns parsed data
     *
     * @param file           uploaded file
     * @param verificationId id of verification
     * @return Entity which contains Calibration Test Data and HTTP status
     */
    @RequestMapping(value = "new/upload", method = RequestMethod.POST)
    public ResponseEntity uploadFileBbi(@RequestBody MultipartFile file, @RequestParam String verificationId) {
        ResponseEntity responseEntity;
        try {
            String originalFileName = file.getOriginalFilename();
            String fileType = originalFileName.substring(originalFileName.lastIndexOf('.'));
            if (Pattern.compile(contentExtensionPattern, Pattern.CASE_INSENSITIVE).matcher(fileType).matches()) {
                DeviceTestData deviceTestData = bbiFileServiceFacade.parseAndSaveBBIFile(
                        file, verificationId, originalFileName);
                long calibrationTestId = testService.createNewTest(deviceTestData, verificationId);

                CalibrationTest calibrationTest = testService.findTestById(calibrationTestId);

                responseEntity = new ResponseEntity(new CalibrationTestFileDataDTO(
                        calibrationTest, testService, verificationService.findById(verificationId)), HttpStatus.OK);

            } else {
                logger.error("Failed to load file: pattern does not match.");
                responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Failed to load file " + e.getMessage());
            logger.error(e); // for prevent critical issue "Either log or rethrow this exception"
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    @RequestMapping(value = "new/{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.GET)
    public PageDTO<VerificationPageDTO> getPageOfAllSentVerificationsByProviderIdAndSearch(
            @PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage, @PathVariable String sortCriteria, @PathVariable String sortOrder, Object globalSearchParamsString,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        User calibratorEmployee = calibratorEmployeeService.oneCalibratorEmployee(employeeUser.getUsername());
        NewVerificationsFilterSearch searchData = new NewVerificationsFilterSearch();
        ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
        ListToPageTransformer<Verification> queryResult = verificationService.findPageOfVerificationsByCalibratorIdAndCriteriaSearch(employeeUser.getOrganizationId(), pageNumber, itemsPerPage,
                searchData.getDate(),
                searchData.getEndDate(),
                searchData.getId(),
                searchData.getClient_full_name(),
                searchData.getStreet(),
                searchData.getRegion(),
                searchData.getDistrict(),
                searchData.getLocality(),
                searchData.getStatus(),
                searchData.getEmployee_last_name(),
                searchData.getStandardSize(),
                searchData.getSymbol(),
                searchData.getNameProvider(),
                searchData.getRealiseYear(),
                searchData.getDismantled(),
                searchData.getBuilding(),
                sortCriteria, sortOrder, calibratorEmployee, arrayList);
        List<VerificationPageDTO> content = VerificationPageDTOTransformer.toDtoFromList(queryResult.getContent());
        return new PageDTO<>(queryResult.getTotalItems(), content);
    }

    @RequestMapping(value = "new/{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.POST)
    public
    @ResponseBody
    PageDTO<VerificationPageDTO> getPageOfAllSentVerificationsByProviderIdAndGlobalSearch(
            @PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage, @PathVariable String sortCriteria, @PathVariable String sortOrder,
            @RequestBody LinkedHashMap<String, Object> params,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        User calibratorEmployee = calibratorEmployeeService.oneCalibratorEmployee(employeeUser.getUsername());
        LinkedHashMap<String, String> searchData = (LinkedHashMap<String, String>) params.get("newVerificationsFilterSearch");
        ArrayList<Map<String, Object>> globalSearchParams = (ArrayList<Map<String, Object>>) params.get("globalSearchParams");
        ListToPageTransformer<Verification> queryResult = verificationService.findPageOfVerificationsByCalibratorIdAndCriteriaSearch(employeeUser.getOrganizationId(), pageNumber, itemsPerPage,
                searchData.get("date"),
                searchData.get("endDate"),
                searchData.get("id"),
                searchData.get("client_full_name"),
                searchData.get("street"),
                searchData.get("region"),
                searchData.get("district"),
                searchData.get("locality"),
                searchData.get("status"),
                searchData.get("employee_last_name"),
                searchData.get("standardSize"),
                searchData.get("symbol"),
                searchData.get("nameProvider"),
                searchData.get("realiseYear"),
                searchData.get("dismantled"),
                searchData.get("building"),
                sortCriteria, sortOrder, calibratorEmployee, globalSearchParams);
        List<VerificationPageDTO> content = VerificationPageDTOTransformer.toDtoFromList(queryResult.getContent());
        return new PageDTO<>(queryResult.getTotalItems(), content);
    }

    /**
     * Responds a page according to input data and search value
     *
     * @param pageNumber   current page number
     * @param itemsPerPage count of elements per one page
     * @return a page of CalibrationTests with their total amount
     */
    @RequestMapping(value = "calibration-test/{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.GET)
    public PageDTO<CalibrationTestDTO> pageCalibrationTestWithSearch(@PathVariable Integer pageNumber,
                                                                     @PathVariable Integer itemsPerPage,
                                                                     @PathVariable String sortCriteria,
                                                                     @PathVariable String sortOrder,
                                                                     CalibrationTestSearch searchData) {
        ListToPageTransformer<CalibrationTest> queryResult = verificationService
                .findPageOfCalibrationTestsByVerificationId(
                        pageNumber,
                        itemsPerPage,
                        searchData.getDate(),
                        searchData.getEndDate(),
                        searchData.getName(),
                        searchData.getRegion(),
                        searchData.getDistrict(),
                        searchData.getLocality(),
                        searchData.getStreet(),
                        searchData.getId(),
                        searchData.getClientFullName(),
                        searchData.getSettingNumber(),
                        searchData.getConsumptionStatus(),
                        searchData.getProtocolId(),
                        searchData.getTestResult(),
                        searchData.getMeasurementDeviceId(),
                        searchData.getMeasurementDeviceType(),
                        sortCriteria,
                        sortOrder);
        List<CalibrationTestDTO> content = CalibratorTestPageDTOTransformer.toDtoFromList(queryResult.getContent());
        return new PageDTO<>(queryResult.getTotalItems(), content);
    }

    /**
     * Find page of verifications by specific criterias on main panel
     *
     * @param pageNumber
     * @param itemsPerPage
     * @param employeeUser
     * @return PageDTO<VerificationPageDTO>
     */
    @RequestMapping(value = "new/mainpanel/{pageNumber}/{itemsPerPage}", method = RequestMethod.GET)
    public PageDTO<VerificationPageDTO> getPageOfAllSentVerificationsByProviderIdAndSearchOnMainPanel(@PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage,
                                                                                                      NewVerificationsSearch searchData, @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        User calibratorEmployee = calibratorEmployeeService.oneCalibratorEmployee(employeeUser.getUsername());
        ListToPageTransformer<Verification> queryResult = verificationService.findPageOfArchiveVerificationsByCalibratorIdOnMainPanel(
                employeeUser.getOrganizationId(),
                pageNumber,
                itemsPerPage,
                searchData.getFormattedDate(),
                searchData.getIdText(),
                searchData.getClient_full_name(),
                searchData.getStreetText(),
                searchData.getRegion(),
                searchData.getDistrict(),
                searchData.getLocality(),
                searchData.getStatus(),
                searchData.getEmployee(),
                calibratorEmployee);
        List<VerificationPageDTO> content = VerificationPageDTOTransformer.toDtoFromList(queryResult.getContent());
        return new PageDTO<>(queryResult.getTotalItems(), content);
    }

    /**
     * Finds count of verifications which have read status 'UNREAD' and are
     * assigned to this organization
     *
     * @param user
     * @return Long
     */
    @RequestMapping(value = "new/count/calibrator", method = RequestMethod.GET)
    public Long getCountOfNewVerificationsByCalibratorId(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        if (user != null) {
            return verificationService.findCountOfNewVerificationsByCalibratorId(user.getOrganizationId());
        } else {
            return null;
        }
    }

    @RequestMapping(value = "new/verificators", method = RequestMethod.GET)
    public Set<OrganizationDTO> getMatchingVerificators(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        Organization userOrganization = organizationService.getOrganizationById(user.getOrganizationId());
        return organizationService.findByIdAndTypeAndActiveAgreementDeviceType(user.getOrganizationId(), OrganizationType.STATE_VERIFICATOR, userOrganization.getDeviceTypes().iterator().next()).stream()
                .map(organization -> new OrganizationDTO(organization.getId(), organization.getName()))
                .collect(Collectors.toSet());
    }

    @RequestMapping(value = "new/update", method = RequestMethod.PUT)
    public void updateVerification(@RequestBody VerificationUpdateDTO verificationUpdateDTO) {
        for (String verificationId : verificationUpdateDTO.getIdsOfVerifications()) {
            Long idCalibrator = verificationUpdateDTO.getOrganizationId();
            Organization calibrator = calibratorService.findById(idCalibrator);
            verificationService.sendVerificationTo(verificationId, calibrator, Status.SENT_TO_VERIFICATOR);
        }
    }

    /**
     * Update verification when user reads it
     *
     * @param verificationDto
     */
    @RequestMapping(value = "new/read", method = RequestMethod.PUT)
    public void markVerificationAsRead(@RequestBody VerificationReadStatusUpdateDTO verificationDto) {
        verificationService.updateVerificationReadStatus(verificationDto.getVerificationId(),
                verificationDto.getReadStatus());
    }

    @RequestMapping(value = "new/{verificationId}", method = RequestMethod.GET)
    public VerificationDTO getNewVerificationDetailsById(@PathVariable String verificationId) {
        Verification verification = verificationService.findById(verificationId);
        if (verification != null) {
            return new VerificationDTO(verification.getClientData(), verification.getId(),
                    verification.getInitialDate(), verification.getExpirationDate(), verification.getStatus(),
                    verification.getCalibrator(), verification.getCalibratorEmployee(), verification.getDevice(),
                    verification.getProvider(), verification.getProviderEmployee(), verification.getStateVerificator(),
                    verification.getStateVerificatorEmployee(), verification.getRejectedMessage());
        } else {
            return null;
        }
    }

    /**
     * Receives archive with BBI files and DB file, calls appropriate services
     * and returns the outcomes of parsing back to the client.
     *
     * @param file Archive with BBIs and DBF
     * @return List of DTOs containing BBI filename, verification id, outcome of parsing (true/false)
     */
    @RequestMapping(value = "new/upload-archive", method = RequestMethod.POST)
    public List<BBIOutcomeDTO> uploadFileArchive(@RequestBody MultipartFile file,
                                                 @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        User calibratorEmployee = calibratorEmployeeService.oneCalibratorEmployee(employeeUser.getUsername());

        List<BBIOutcomeDTO> bbiOutcomeDTOList = null;
        try {
            String originalFileFullName = file.getOriginalFilename();
            String fileType = originalFileFullName.substring(originalFileFullName.lastIndexOf('.'));
            if (Pattern.compile(archiveExtensionPattern, Pattern.CASE_INSENSITIVE).matcher(fileType).matches()) {
                bbiOutcomeDTOList = bbiFileServiceFacade.parseAndSaveArchiveOfBBIfiles(file, originalFileFullName,
                        calibratorEmployee);
            }
        } catch (Exception e) {
            logger.error("Failed to load file " + e.getMessage());
            logger.error(e); // for prevent critical issue "Either log or rethrow this exception"
        }
        return bbiOutcomeDTOList;
    }

    @RequestMapping(value = "archive/{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.GET)
    public PageDTO<VerificationPageDTO> getPageOfArchivalVerificationsByOrganizationId(@PathVariable Integer pageNumber,
                                                                                       @PathVariable Integer itemsPerPage,
                                                                                       @PathVariable String sortCriteria,
                                                                                       @PathVariable String sortOrder,
                                                                                       ArchiveVerificationsFilterAndSort searchData,
                                                                                       @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails employeeUser) {
        User calibratorEmployee = calibratorEmployeeService.oneCalibratorEmployee(employeeUser.getUsername());
        ListToPageTransformer<Verification> queryResult = verificationService
                .findPageOfArchiveVerificationsByCalibratorId(
                        employeeUser.getOrganizationId(),
                        pageNumber,
                        itemsPerPage,
                        searchData.getDate(),
                        searchData.getEndDate(),
                        searchData.getId(),
                        searchData.getClient_full_name(),
                        searchData.getStreet(),
                        searchData.getStatus(),
                        searchData.getEmployee_last_name(),
                        searchData.getProtocol_id(),
                        searchData.getProtocol_status(),
                        searchData.getMeasurement_device_id(),
                        searchData.getMeasurement_device_type(),
                        sortCriteria,
                        sortOrder, calibratorEmployee);
        List<VerificationPageDTO> content = VerificationPageDTOTransformer.toDtoFromList(queryResult.getContent());
        return new PageDTO<VerificationPageDTO>(queryResult.getTotalItems(), content);
    }

    @RequestMapping(value = "archive/{verificationId}", method = RequestMethod.GET)
    public VerificationDTO getArchivalVerificationDetailsById(@PathVariable String verificationId,
                                                              @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        Verification verification = verificationService.findByIdAndCalibratorId(verificationId,
                user.getOrganizationId());

        return new VerificationDTO(
                verification.getClientData(),
                verification.getId(),
                verification.getInitialDate(),
                verification.getExpirationDate(),
                verification.getStatus(),
                verification.getCalibrator(),
                verification.getCalibratorEmployee(),
                verification.getDevice(),
                verification.getProvider(),
                verification.getProviderEmployee(),
                verification.getStateVerificator(),
                verification.getStateVerificatorEmployee());
    }

    @RequestMapping(value = "new/earliest_date/calibrator", method = RequestMethod.GET)
    public String getNewVerificationEarliestDateByProviderId(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        if (user != null) {
            Organization organization = organizationService.getOrganizationById(user.getOrganizationId());
            Date gottenDate = verificationService.getNewVerificationEarliestDateByCalibrator(organization);
            Date date;
            if (gottenDate != null) {
                date = new Date(gottenDate.getTime());
            } else {
                return null;
            }
            DateTimeFormatter dbDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            String isoLocalDateString = localDate.format(dbDateTimeFormatter);
            return isoLocalDateString;
        } else {
            return null;
        }
    }

    /**
     * Find date of earliest new verification
     *
     * @param user
     * @return String date
     */
    @RequestMapping(value = "archive/earliest_date/calibrator", method = RequestMethod.GET)
    public String getArchivalVerificationEarliestDateByProviderId(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        if (user != null) {
            Organization organization = organizationService.getOrganizationById(user.getOrganizationId());
            Date gottenDate = verificationService.getArchivalVerificationEarliestDateByCalibrator(organization);
            Date date;
            if (gottenDate != null) {
                date = new Date(gottenDate.getTime());
            } else {
                return null;
            }
            DateTimeFormatter dbDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            return localDate.format(dbDateTimeFormatter);
        } else {
            return null;
        }
    }

    /**
     * Current method search for file name witch user decided to deleteSubdivision
     *
     * @param idVerification
     * @return name of file and corresponding verification ID
     */
    @RequestMapping(value = "find/uploadFile", method = RequestMethod.GET)
    public List<String> getBbiFile(@RequestParam String idVerification) {
        List<String> data;
        String fileName = calibratorService.findBbiFileByOrganizationId(idVerification);
        data = Arrays.asList(idVerification, fileName);
        return data;
    }

    /**
     * Check if current user is Employee
     *
     * @param user
     * @return true if user has role CALIBRATOR_EMPLOYEE
     * false if user has role CALIBRATOR_ADMIN
     */
    @RequestMapping(value = "calibrator/role", method = RequestMethod.GET)
    public Boolean isEmployeeCalibrator(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        User checkedUser = usersService.findOne(user.getUsername());
        return checkedUser.getUserRoles().contains(UserRole.CALIBRATOR_EMPLOYEE);
    }

    /**
     * get list of employees if it calibrator admin
     * or get data about employee.
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "new/calibratorEmployees", method = RequestMethod.GET)
    public List<com.softserve.edu.service.utils.EmployeeDTO> employeeVerification(
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        User employee = calibratorEmployeeService.oneCalibratorEmployee(user.getUsername());
        List<String> role = usersService.getRoles(user.getUsername());
        return calibratorEmployeeService.getAllCalibrators(role, employee);
    }

    /**
     * Assigning employee to verification
     *
     * @param verificationProviderEmployeeDTO
     */
    @RequestMapping(value = "assign/calibratorEmployee", method = RequestMethod.PUT)
    public void assignCalibratorEmployee(@RequestBody VerificationProviderEmployeeDTO verificationProviderEmployeeDTO) {
        String userNameCalibrator = verificationProviderEmployeeDTO.getEmployeeCalibrator().getUsername();
        String idVerification = verificationProviderEmployeeDTO.getIdVerification();
        User employeeCalibrator = calibratorService.oneCalibratorEmployee(userNameCalibrator);
        calibratorService.assignCalibratorEmployee(idVerification, employeeCalibrator);
    }

    /**
     * remove from verification assigned employee.
     *
     * @param verificationUpdatingDTO
     */
    @RequestMapping(value = "remove/calibratorEmployee", method = RequestMethod.PUT)
    public void removeCalibratorEmployee(@RequestBody VerificationProviderEmployeeDTO verificationUpdatingDTO) {
        String idVerification = verificationUpdatingDTO.getIdVerification();
        calibratorService.assignCalibratorEmployee(idVerification, null);
    }

    /**
     * check if additional info exists for the
     * the verification
     *
     * @param verificationId id of the verification
     * @return {@literal true} if yes, or {@literal false} if not.
     */
    @RequestMapping(value = "/checkInfo/{verificationId}", method = RequestMethod.GET)
    public boolean checkIfAdditionalInfoExists(@PathVariable String verificationId) {
        return calibratorService.checkIfAdditionalInfoExists(verificationId);
    }

    /**
     * method for updating counter info
     *
     * @param counterInfo
     * @return
     */
    @RequestMapping(value = "editCounterInfo", method = RequestMethod.PUT)
    public ResponseEntity editCounterInfo(@RequestBody CounterInfoDTO counterInfo) {
        HttpStatus httpStatus = HttpStatus.OK;

        try {
            verificationService.editCounter(counterInfo.getVerificationId(), counterInfo.getDeviceName(), counterInfo.getDismantled(),
                    counterInfo.getSealPresence(), counterInfo.getDateOfDismantled(), counterInfo.getDateOfMounted(),
                    counterInfo.getNumberCounter(), counterInfo.getReleaseYear(), counterInfo.getAccumulatedVolume(), counterInfo.getSymbol(),
                    counterInfo.getStandardSize(), counterInfo.getComment(), counterInfo.getDeviceId());
        } catch (Exception e) {
            logger.error("GOT EXCEPTION " + e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(httpStatus);
    }

    /**
     * method for updation additional info
     *
     * @param infoDTO
     * @return
     */
    @RequestMapping(value = "saveInfo", method = RequestMethod.PUT)
    public ResponseEntity editAddInfo(@RequestBody AdditionalInfoDTO infoDTO) {
        HttpStatus httpStatus = HttpStatus.OK;

        try {
            verificationService.editAddInfo(infoDTO.getEntrance(), infoDTO.getDoorCode(), infoDTO.getFloor(),
                    infoDTO.getDateOfVerif(), infoDTO.getTimeFrom(), infoDTO.getTimeTo(), infoDTO.isServiceability(), infoDTO.getNoWaterToDate(),
                    infoDTO.getNotes(), infoDTO.getVerificationId());
        } catch (Exception e) {
            logger.error("GOT EXCEPTION " + e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(httpStatus);
    }

    /**
     * method for updating client information
     *
     * @param clientDTO
     * @return
     */
    @RequestMapping(value = "editClientInfo", method = RequestMethod.PUT)
    public ResponseEntity editClientInfo(@RequestBody ClientStageVerificationDTO clientDTO) {
        HttpStatus httpStatus = HttpStatus.OK;

        Address address = new Address(clientDTO.getRegion(),
                clientDTO.getDistrict(),
                clientDTO.getLocality(),
                clientDTO.getStreet(),
                clientDTO.getBuilding(),
                clientDTO.getFlat(),
                clientDTO.getMailIndex()
        );

        ClientData clientData = new ClientData(
                clientDTO.getFirstName(),
                clientDTO.getLastName(),
                clientDTO.getMiddleName(),
                clientDTO.getEmail(),
                clientDTO.getPhone(),
                clientDTO.getSecondPhone(),
                address
        );

        try {
            verificationService.editClientInfo(clientDTO.getVerificationId(), clientData);
        } catch (Exception e) {
            logger.error("GOT EXCEPTION " + e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(httpStatus);
    }

    private void updateVerificationData(Verification verification, OrganizationStageVerificationDTO verificationDTO,
                                        Organization calibrator) {
        // updating client data
        ClientData clientData = new ClientData(
                verificationDTO.getFirstName(),
                verificationDTO.getLastName(),
                verificationDTO.getMiddleName(),
                verificationDTO.getEmail(),
                verificationDTO.getPhone(),
                verificationDTO.getSecondPhone(),
                new Address(
                        verificationDTO.getRegion(),
                        verificationDTO.getDistrict(),
                        verificationDTO.getLocality(),
                        verificationDTO.getStreet(),
                        verificationDTO.getBuilding(),
                        verificationDTO.getFlat(),
                        verificationDTO.getMailIndex()
                )
        );

        // updating counter information
        CounterType counterType = calibratorService.findOneBySymbolAndStandardSize(verificationDTO.getSymbol(),
                verificationDTO.getStandardSize());
        Counter counter = verification.getCounter();
        if (counter == null) {
            counter = new Counter();
        }
        counter.setReleaseYear(verificationDTO.getReleaseYear());
        counter.setDateOfDismantled(verificationDTO.getDateOfDismantled());
        counter.setDateOfMounted(verificationDTO.getDateOfMounted());
        counter.setNumberCounter(verificationDTO.getNumberCounter());
        counter.setAccumulatedVolume(verificationDTO.getAccumulatedVolume());
        counter.setCounterType(counterType);

        // updating addition info
        AdditionalInfo info = verification.getInfo();
        if (info == null) {
            info = new AdditionalInfo();
        }
        info.setEntrance(verificationDTO.getEntrance());
        info.setDoorCode(verificationDTO.getDoorCode());
        info.setFloor(verificationDTO.getFloor());
        info.setDateOfVerif(verificationDTO.getDateOfVerif());
        info.setServiceability(verificationDTO.getServiceability());
        info.setNoWaterToDate(verificationDTO.getNoWaterToDate());
        info.setNotes(verificationDTO.getNotes());
        if (verificationDTO.getTimeFrom() != null) {
            info.setTimeFrom(verificationDTO.getTimeFrom());
            info.setTimeTo(verificationDTO.getTimeTo());
        }

        Organization provider = providerService.findById(verificationDTO.getProviderId());
        Device device = deviceService.getById(verificationDTO.getDeviceId());
        if (provider == null || device == null) {
            throw new RuntimeException();
        }

        // updating verification data
        verification.setClientData(clientData);
        verification.setProvider(provider);
        verification.setDevice(device);
        verification.setCalibrator(calibrator);
        verification.setInfo(info);
        verification.setCounterStatus(verificationDTO.getDismantled());
        verification.setComment(verificationDTO.getComment());
        verification.setSealPresence(verificationDTO.getSealPresence());
    }
}