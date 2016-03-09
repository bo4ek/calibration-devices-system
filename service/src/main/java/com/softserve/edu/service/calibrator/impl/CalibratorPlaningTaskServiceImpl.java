package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.entity.catalogue.Team.DisassemblyTeam;
import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import com.softserve.edu.repository.*;
import com.softserve.edu.repository.catalogue.DistrictRepository;
import com.softserve.edu.repository.catalogue.LocalityRepository;
import com.softserve.edu.repository.catalogue.StreetRepository;
import com.softserve.edu.service.calibrator.CalibratorEmployeeService;
import com.softserve.edu.service.calibrator.CalibratorPlanningTaskService;
import com.softserve.edu.service.tool.MailService;
import com.softserve.edu.service.utils.export.DbTableExporter;
import com.softserve.edu.service.utils.export.TableExportColumn;
import com.softserve.edu.service.utils.export.XlsTableExporter;
import com.softserve.edu.specification.CalibrationTaskSpecificationBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class CalibratorPlaningTaskServiceImpl implements CalibratorPlanningTaskService {

    @Autowired
    private CalibrationPlanningTaskRepository taskRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private CalibrationModuleRepository moduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalibrationDisassemblyTeamRepository teamRepository;

    @Autowired
    private CounterTypeRepository counterTypeRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private StreetRepository streetRepository;

    @Autowired
    CalibratorEmployeeService calibratorEmployeeService;


    private Logger logger = Logger.getLogger(CalibratorPlaningTaskServiceImpl.class);

    /**
     * This method returns filtered and sorted
     * page of calibration tasks
     *
     * @param filterParams filtering parameters
     * @param pageable     parameters for pagination and sorting
     * @return filtered and sorted page of calibration tasks
     */
    public Page<CalibrationTask> getFilteredPageOfCalibrationTasks(Map<String, String> filterParams,
                                                                   Pageable pageable, String username) {
        User user = userRepository.findOne(username);
        filterParams.put("organizationCode", user.getOrganization().getAdditionInfoOrganization().getCodeEDRPOU());
        CalibrationTaskSpecificationBuilder specificationBuilder = new CalibrationTaskSpecificationBuilder(filterParams);
        Specification<CalibrationTask> searchSpec = specificationBuilder.buildPredicate();
        return taskRepository.findAll(searchSpec, pageable);
    }

    /**
     * This method fetches all calibration tasks and returns
     * a sorted page of them
     *
     * @param pageable parameters for pagination and sorting
     * @return sorted page of calibration tasks
     */
    public Page<CalibrationTask> findAllCalibrationTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    /**
     * This method changes the date of calibration task
     *
     * @param taskID     ID of the calibration task the date of which is to be changed
     * @param dateOfTask new task date
     */
    public void changeTaskDate(Long taskID, Date dateOfTask) {
        CalibrationTask task = taskRepository.findOne(taskID);
        if (task == null || dateOfTask == null) {
            throw new IllegalArgumentException();
        }
        task.setDateOfTask(dateOfTask);
        try {
            taskRepository.save(task);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * This method saves new task for the station. It checks if counter
     * statuses for the verifications are the same, if not
     * Also it checks if calibration module device type is the same
     * as device type of the verification, if not method @throws IllegalArgumentException().
     *
     * @param taskDate
     * @param moduleSerialNumber
     * @param verificationsId
     * @param userName
     * @throws IllegalArgumentException().
     */
    @Override
    public Boolean addNewTaskForStation(Date taskDate, String moduleSerialNumber, List<String> verificationsId, String userName) {
        Boolean taskAlreadyExists = true;
        Boolean taskHasCompleteVerification = false;
        CalibrationTask task = taskRepository.findByDateOfTaskAndModule_SerialNumber(taskDate, moduleSerialNumber);
        Iterable<Verification> verifications = verificationRepository.findAll(verificationsId);

        User user = userRepository.findOne(userName);
        if (user == null) {
            logger.error("User with name:" + userName + "was trying to create task for user with name: " + userName + " wasn't found");
            throw new IllegalArgumentException();
        }

        for (Verification verification : verifications) {
            verification.getStatus().equals(Status.TEST_COMPLETED);
            if (!verification.getCalibrator().getId().equals(user.getOrganization().getId())) {
                logger.error("User with name: " + userName + " was trying to change verification and task from other organization");
                return taskAlreadyExists;
            }
            taskHasCompleteVerification = true;
        }

        if (task == null | taskHasCompleteVerification.equals(true)) {
            taskAlreadyExists = false;
            CalibrationModule module = moduleRepository.findBySerialNumber(moduleSerialNumber);
            if (module == null) {
                logger.error("User with name:" + userName + "was trying to create task for module with serial number: " + moduleSerialNumber + " wasn't found");
                throw new IllegalArgumentException();
            }
            task = new CalibrationTask(module, null, new Date(), taskDate, user);
            taskRepository.save(task);
        }

        for (Verification verification : verifications) {
            verification.setStatus(Status.TEST_PLACE_DETERMINED);
            verification.setTaskStatus(Status.TEST_PLACE_DETERMINED);
            verification.setTask(task);
        }
        verificationRepository.save(verifications);
        return taskAlreadyExists;
    }

    /**
     * This method save new task for the team. It checks if counter
     * statuses for the verifications are the same, if not
     *
     * @param taskDate
     * @param serialNumber
     * @param verificationsId
     * @param userId
     * @throws IllegalArgumentException(). Also it checks if station
     *                                     device type is is as device type of the verification, if not
     *                                     method @throws IllegalArgumentException().
     */
    @Override
    public void addNewTaskForTeam(Date taskDate, String serialNumber, List<String> verificationsId, String userId) {
        Set<Verification> verifications = new HashSet<>();
        DisassemblyTeam team = teamRepository.findOne(serialNumber);
        int i = 0;
        boolean counterStatus = false;
        for (String verifID : verificationsId) {
            Verification verification = verificationRepository.findOne(verifID);
            if (verification == null) {
                logger.error("verification haven't found");
            } else {
                if (i == 0) {
                    counterStatus = verification.isCounterStatus();
                }
                if (counterStatus == verification.isCounterStatus()) {
                    if (team.getSpecialization().contains(verification.getDevice().getDeviceType())) {
                        verification.setTaskStatus(Status.TASK_PLANED);
                        verificationRepository.save(verification);
                        verifications.add(verification);
                        i++;
                    } else {
                        logger.error("verification and module has different device types");
                        throw new IllegalArgumentException();
                    }
                } else {
                    logger.error("verifications has different counter status");
                    throw new IllegalArgumentException();
                }
            }
        }
        teamRepository.save(team);
        User user = userRepository.findOne(userId);
        CalibrationTask calibrationTask = taskRepository.save(new CalibrationTask(null, team, new Date(), taskDate, user, verifications));
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DAY_MONTH_YEAR);
        String filename = calibrationTask.getTeam().getId() + "-" +
                dateFormat.format(calibrationTask.getDateOfTask()) + "_";
        File xlsFile;
        try {
            xlsFile = File.createTempFile(filename, "." + Constants.XLS_EXTENSION);
            xlsFile.setWritable(true);
            xlsFile.setReadable(true);
            xlsFile.setExecutable(true);
            XlsTableExporter xls = new XlsTableExporter();
            Verification[] verif = verifications.toArray(new Verification[verifications.size()]);
            List<TableExportColumn> dataForXls = getDataForXls(calibrationTask, verif);
            xls.exportToFile(dataForXls, xlsFile);
            mailService.sendMailWithAttachments(calibrationTask.getTeam().getLeaderEmail(), Constants.TASK + " " + calibrationTask.getId(), " ", xlsFile);
        } catch (Exception ex) {/*Temporary try block*/}
    }

    /**
     * This method find count for the verifications with status
     * planning task which assigned for the calibrator employee.
     * If employee has role admin it return count of all verifications with status planning
     * task which related to the calibrator organization
     *
     * @param userName
     * @return count of verifications (int)
     */
    @Override
    public int findVerificationsByCalibratorEmployeeAndTaskStatusCount(String userName) {
        User user = userRepository.findOne(userName);
        if (user == null) {
            logger.error("Cannot found user!");
        }
        Set<UserRole> roles = user.getUserRoles();
        for (UserRole role : roles) {
            if (role.equals(UserRole.CALIBRATOR_ADMIN)) {
                return verificationRepository.findByTaskStatusAndCalibratorId(Status.PLANNING_TASK, user.getOrganization().getId()).size();
            }
        }
        return verificationRepository.findByCalibratorEmployeeUsernameAndTaskStatus(user.getUsername(), Status.PLANNING_TASK).size();

    }

    /**
     * This method returns page of verifications with
     * status planning task filtered by calibrator id,
     * when calibrator is admin
     * and sorted by client address
     *
     * @param id
     * @param pageNumber
     * @param itemsPerPage
     * @param sortCriteria
     * @param sortOrder
     * @return Page<Verification>
     */
    @Override
    public Page<Verification> findByTaskStatusAndCalibratorId(Long id, int pageNumber, int itemsPerPage,
                                                              String sortCriteria, String sortOrder) {
        Pageable pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                "clientData.clientAddress.district", "clientData.clientAddress.street", "clientData.clientAddress.building", "clientData.clientAddress.flat"));
        if (sortCriteria.equals("date")) {
            if (sortOrder.equals("asc")) {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                        "sentToCalibratorDate"));
            } else {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                        "sentToCalibratorDate"));
            }
        } else if (sortCriteria.equals("client_last_name")) {
            if (sortOrder.equals("asc")) {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                        "clientData.lastName", "clientData.firstName"));
            } else {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                        "clientData.lastName", "clientData.firstName"));
            }
        } else if (sortCriteria.equals("providerName")) {
            if (sortOrder.equals("asc")) {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                        "provider.name"));
            } else {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                        "provider.name"));
            }
        } else if (sortCriteria.equals("dateOfVerif") || sortCriteria.equals("timeOfVerif")) {
            if (sortOrder.equals("asc")) {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                        "info.dateOfVerif", "info.timeFrom"));
            } else {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                        "info.dateOfVerif", "info.timeFrom"));
            }
        } else if (sortCriteria.equals("noWaterToDate")) {
            if (sortOrder.equals("asc")) {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                        "info.noWaterToDate"));
            } else {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                        "info.noWaterToDate"));
            }
        } else if (sortCriteria.equals("district") || sortCriteria.equals("street") || sortCriteria.equals("building_flat")) {
            if (sortOrder.equals("asc")) {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                        "clientData.clientAddress.district", "clientData.clientAddress.street", "clientData.clientAddress.building", "clientData.clientAddress.flat"));
            } else {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                        "clientData.clientAddress.district", "clientData.clientAddress.street", "clientData.clientAddress.building", "clientData.clientAddress.flat"));
            }
        } else if (sortCriteria.equals("serviceability")) {
            if (sortOrder.equals("asc")) {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                        "info.serviceability"));
            } else {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                        "info.serviceability"));
            }
        } else if (sortCriteria.equals("sealPresence")) {
            if (sortOrder.equals("asc")) {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                        "sealPresence"));
            } else {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                        "sealPresence"));
            }
        } else if (sortCriteria.equals("telephone")) {
            if (sortOrder.equals("asc")) {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                        "clientData.phone"));
            } else {
                pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                        "clientData.phone"));
            }
        }

        return verificationRepository.findByTaskStatusAndCalibratorId(Status.PLANNING_TASK, id, pageRequest);
    }


    /**
     * This method returns page of verifications with
     * status planning task filtered by calibrator id,
     * and sorted by client address. If user has role
     * admin it calls method findByTaskStatusAndCalibratorId()
     *
     * @param userName
     * @param pageNumber
     * @param itemsPerPage
     * @param sortCriteria
     * @param sortOrder
     * @return Page<Verification>
     * @throws NullPointerException();
     */
    @Override
    public Page<Verification> findVerificationsByCalibratorEmployeeAndTaskStatus(String userName, int pageNumber,
                                                                                 int itemsPerPage, String sortCriteria,
                                                                                 String sortOrder) {
        User user = userRepository.findOne(userName);
        if (user == null) {
            logger.error("Cannot found user with name " + userName);
            throw new NullPointerException();
        }
        Set<UserRole> roles = user.getUserRoles();
        for (UserRole role : roles) {
            if (role.equals(UserRole.CALIBRATOR_ADMIN)) {
                return findByTaskStatusAndCalibratorId(user.getOrganization().getId(), pageNumber, itemsPerPage
                        , sortCriteria, sortOrder);
            }
        }
        Pageable pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                "clientData.clientAddress.district", "clientData.clientAddress.street", "clientData.clientAddress.building", "clientData.clientAddress.flat"));
        return verificationRepository.findByCalibratorEmployeeUsernameAndTaskStatus(user.getUsername(), Status.PLANNING_TASK, pageRequest);
    }

    /**
     * This method returns list of counter types
     * which has the same device id with the verification device id
     *
     * @param verifId
     * @return List<CounterType>
     * @throws NullPointerException();
     */
    @Override
    public List<CounterType> findSymbolsAndSizes(String verifId) {
        Verification verification = verificationRepository.findOne(verifId);
        if (verification == null) {
            logger.error("Cannot found verification!");
            throw new NullPointerException();
        }
        List<CounterType> counterTypes = counterTypeRepository.findByDeviceId(verification.getDevice().getId());
        if (counterTypes == null) {
            logger.error("Cannot found counter types for verification!");
            throw new NullPointerException();
        }
        return counterTypes;
    }

    /**
     * Sends task to station
     *
     * @param id Task id
     * @throws Exception
     */
    public void sendTaskToStation(Long id, String senderUsername) throws Exception {
        CalibrationTask calibrationTask = taskRepository.findOne(id);
        Verification[] verifications = verificationRepository.findByTaskIdOrderByQueueAsc(id);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DAY_MONTH_YEAR);

        String filename = calibrationTask.getModule().getModuleNumber() + "-" +
                dateFormat.format(calibrationTask.getDateOfTask()) + "_";

        File xlsFile = File.createTempFile(filename, "." + Constants.XLS_EXTENSION);
        xlsFile.setWritable(true);
        xlsFile.setReadable(true);
        xlsFile.setExecutable(true);
        File dbFile = File.createTempFile(filename, "." + Constants.DB_EXTENSION);
        dbFile.setWritable(true);
        dbFile.setReadable(true);
        dbFile.setExecutable(true);

        try {
            XlsTableExporter xls = new XlsTableExporter();
            List<TableExportColumn> dataForXls = getDataForXls(calibrationTask, verifications);
            xls.exportToFile(dataForXls, xlsFile);

            DbTableExporter db = new DbTableExporter(Constants.DEFAULT_DB_TABLE_NAME);
            List<TableExportColumn> dataForDb = getDataForDb(calibrationTask, verifications);
            db.exportToFile(dataForDb, dbFile);

            String email = calibrationTask.getModule().getEmail();
            User user = calibratorEmployeeService.oneCalibratorEmployee(senderUsername);

            mailService.sendMailToStationWithAttachments(user, calibrationTask.getModule().getModuleNumber(), dateFormat.format(calibrationTask.getDateOfTask()).toString(), email, Constants.TASK + " " + calibrationTask.getId(), xlsFile, dbFile);
            calibrationTask.setStatus(Status.SENT_TO_TEST_DEVICE);
            for (Verification verification : verifications) {
                verification.setStatus(Status.SENT_TO_TEST_DEVICE);
                verification.setTaskStatus(Status.SENT_TO_TEST_DEVICE);
            }
            taskRepository.save(calibrationTask);
            verificationRepository.save(Arrays.asList(verifications));
        } catch (Exception ex) {
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    private List<TableExportColumn> getDataForXls(CalibrationTask calibrationTask, Verification[] verifications) {
        List<TableExportColumn> data = new ArrayList<TableExportColumn>();

        // region Define lists
        List<String> taskDate = new ArrayList<>();
        List<String> provider = new ArrayList<>();
        List<String> district = new ArrayList<>();
        List<String> address = new ArrayList<>();
        List<String> building = new ArrayList<>();
        List<String> flat = new ArrayList<>();
        List<String> entrance = new ArrayList<>();
        List<String> floor = new ArrayList<>();
        List<String> countersNumber = new ArrayList<>();
        List<String> fullName = new ArrayList<>();
        List<String> telephone = new ArrayList<>();
        List<String> time = new ArrayList<>();
        List<String> comment = new ArrayList<>();

        // endregion

        // region Fill lists

        for (Verification verification : verifications) {
            SimpleDateFormat simpleTaskDate = new SimpleDateFormat("dd.MM.yyyy");
            String empty = " ";

            try {
                taskDate.add(simpleTaskDate.format(calibrationTask.getDateOfTask()));
            } catch (IllegalArgumentException e) {
                taskDate.add(empty);
                logger.debug("The date of calibration task is absent or having wrong format, id of this task is:" + calibrationTask.getId(), e);
            }

            provider.add(verification.getProvider() != null && verification.getProvider().getName() != null ? verification.getProvider().getName() : empty);

            if (verification.getClientData() != null) {
                fullName.add(verification.getClientData().getFullName() != null ? verification.getClientData().getFullName() : empty);
                telephone.add(verification.getClientData().getPhone() != null ? verification.getClientData().getPhone() : empty);

                if (verification.getClientData().getClientAddress() != null) {
                    district.add(verification.getClientData().getClientAddress().getDistrict() != null ? verification.getClientData().getClientAddress().getDistrict() : empty);
                    address.add(verification.getClientData().getClientAddress().getAddress() != null ? verification.getClientData().getClientAddress().getAddress() : empty);
                    building.add(verification.getClientData().getClientAddress().getBuilding() != null ? verification.getClientData().getClientAddress().getBuilding() : empty);
                    flat.add(verification.getClientData().getClientAddress().getFlat() != null ? verification.getClientData().getClientAddress().getFlat() : empty);
                } else {
                    district.add(empty);
                    address.add(empty);
                    building.add(empty);
                    flat.add(empty);
                }

            } else {
                fullName.add(empty);
                telephone.add(empty);
                district.add(empty);
                address.add(empty);
                building.add(empty);
                flat.add(empty);
            }

            if (verification.getInfo() != null) {
                entrance.add(verification.getInfo().getEntrance() != 0 ? String.valueOf(verification.getInfo().getEntrance()) : empty);
                floor.add(verification.getInfo().getFloor() != 0 ? String.valueOf(verification.getInfo().getFloor()) : empty);
                time.add(verification.getInfo().getTimeFrom() != null && verification.getInfo().getTimeTo() != null ? verification.getInfo().getTimeFrom() + "-" + verification.getInfo().getTimeTo() : empty);
            } else {
                entrance.add(empty);
                floor.add(empty);
                time.add(empty);
            }

            countersNumber.add(String.valueOf(1));
            comment.add(verification.getComment() != null ? verification.getComment().toString() : empty);
        }

        // endregion

        // region Fill map

        data.add(new TableExportColumn(Constants.TASK_DATE, taskDate));
        data.add(new TableExportColumn(Constants.PROVIDER, provider));
        data.add(new TableExportColumn(Constants.REGION, district));
        data.add(new TableExportColumn(Constants.ADDRESS, address));
        data.add(new TableExportColumn(Constants.BUILDING, building));
        data.add(new TableExportColumn(Constants.FLAT, flat));
        data.add(new TableExportColumn(Constants.ENTRANCE, entrance));
        data.add(new TableExportColumn(Constants.FLOOR, floor));
        data.add(new TableExportColumn(Constants.COUNTERS_NUMBER, countersNumber));
        data.add(new TableExportColumn(Constants.FULL_NAME_SHORT, fullName));
        data.add(new TableExportColumn(Constants.PHONE_NUMBER, telephone));
        data.add(new TableExportColumn(Constants.DESIRABLE_TIME, time));
        data.add(new TableExportColumn(Constants.COMMENT, comment));

        // endregion

        return data;
    }

    private List<TableExportColumn> getDataForDb(CalibrationTask calibrationTask, Verification[] verifications) {
        List<TableExportColumn> data = new ArrayList<>();

        // region Define lists
        List<String> id = new ArrayList<>();
        List<String> surname = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> middlename = new ArrayList<>();
        List<String> city = new ArrayList<>();
        List<String> district = new ArrayList<>();
        List<String> sector = new ArrayList<>();
        List<String> street = new ArrayList<>();
        List<String> building = new ArrayList<>();
        List<String> flat = new ArrayList<>();
        List<String> telephone = new ArrayList<>();
        List<String> datetime = new ArrayList<>();
        List<String> counterNumber = new ArrayList<>();
        List<String> comments = new ArrayList<>();
        List<String> customer = new ArrayList<>();
        // endregion

        // region Fill lists

        for (Verification verification : verifications) {
            String empty = " ";

            id.add(verification.getId() != null ? verification.getId() : empty);

            if (verification.getClientData() != null) {
                surname.add(verification.getClientData().getLastName() != null ? verification.getClientData().getLastName() : empty);
                name.add(verification.getClientData().getFirstName() != null ? verification.getClientData().getFirstName() : empty);
                middlename.add(verification.getClientData().getMiddleName() != null ? verification.getClientData().getMiddleName() : empty);
                telephone.add(verification.getClientData().getPhone() != null ? verification.getClientData().getPhone() : empty);

                if (verification.getClientData().getClientAddress() != null) {
                    city.add(verification.getClientData().getClientAddress().getLocality() != null ? verification.getClientData().getClientAddress().getLocality() : empty);
                    district.add(verification.getClientData().getClientAddress().getDistrict() != null ? verification.getClientData().getClientAddress().getDistrict() : empty);
                    sector.add(verification.getClientData().getClientAddress().getRegion() != null ? verification.getClientData().getClientAddress().getRegion() : empty);
                    street.add(verification.getClientData().getClientAddress().getStreet() != null ? verification.getClientData().getClientAddress().getStreet() : empty);
                    building.add(verification.getClientData().getClientAddress().getBuilding() != null ? verification.getClientData().getClientAddress().getBuilding() : empty);
                    flat.add(verification.getClientData().getClientAddress().getFlat() != null ? verification.getClientData().getClientAddress().getFlat() : empty);
                } else {
                    city.add(empty);
                    district.add(empty);
                    sector.add(empty);
                    street.add(empty);
                    building.add(empty);
                    flat.add(empty);
                }

            } else {
                surname.add(empty);
                name.add(empty);
                middlename.add(empty);
                city.add(empty);
                district.add(empty);
                sector.add(empty);
                street.add(empty);
                building.add(empty);
                flat.add(empty);
            }

            try {
                SimpleDateFormat simpleTaskDate = new SimpleDateFormat("dd.MM.yyyy");
                String date = simpleTaskDate.format(calibrationTask.getDateOfTask());
                String time = (verification.getInfo() != null && verification.getInfo().getTimeFrom() != null && verification.getInfo().getTimeTo() != null ? verification.getInfo().getTimeFrom() + "" : empty);
                datetime.add(date + " " + time);
            } catch (IllegalArgumentException e) {
                datetime.add(empty);
                logger.debug("The date of calibration task is absent or having wrong format, id of this task is:" + calibrationTask.getId(), e);
            }

            counterNumber.add(verification.getCounter() != null && verification.getCounter().getNumberCounter() != null ? verification.getCounter().getNumberCounter() : "-");
            comments.add(verification.getComment() != null ? verification.getComment() : empty);
            customer.add(verification.getCalibratorEmployee() != null && verification.getCalibratorEmployee().getUsername() != null ? verification.getCalibratorEmployee().getUsername() : empty);
        }

        // endregion

        // region Fill List<TableExportColumn>
        data.add(new TableExportColumn("id_pc", "INTEGER", id));
        data.add(new TableExportColumn("surname", "TEXT", surname));
        data.add(new TableExportColumn("name", "TEXT", name));
        data.add(new TableExportColumn("middlename", "TEXT", middlename));
        data.add(new TableExportColumn("city", "TEXT", city));
        data.add(new TableExportColumn("district", "TEXT", district));
        data.add(new TableExportColumn("bush", "TEXT", sector));
        data.add(new TableExportColumn("street", "TEXT", street));
        data.add(new TableExportColumn("Building", "TEXT", building));
        data.add(new TableExportColumn("Apartment", "TEXT", flat));
        data.add(new TableExportColumn("Tel", "TEXT", telephone));
        data.add(new TableExportColumn("Date_visit", "TEXT", datetime));
        data.add(new TableExportColumn("Counter_number", "TEXT", counterNumber));
        data.add(new TableExportColumn("Note", "TEXT", comments));
        data.add(new TableExportColumn("Customer", "TEXT", customer));
        // endregion

        return data;
    }
}