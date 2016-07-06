package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.catalogue.Team.DisassemblyTeam;
import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import com.softserve.edu.repository.*;
import com.softserve.edu.service.calibrator.CalibratorEmployeeService;
import com.softserve.edu.service.calibrator.CalibratorPlanningTaskService;
import com.softserve.edu.service.exceptions.InvalidModuleSerialNumberException;
import com.softserve.edu.service.exceptions.PermissionDeniedException;
import com.softserve.edu.service.tool.MailService;
import com.softserve.edu.service.utils.CalibrationTaskQueryConstructorCalibrator;
import com.softserve.edu.service.utils.ListToPageTransformer;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
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
    CalibratorEmployeeService calibratorEmployeeService;

    @PersistenceContext
    private EntityManager em;

    private Logger logger = Logger.getLogger(CalibratorPlaningTaskServiceImpl.class);

    /**
     * This method returns filtered and sorted
     * page of calibration tasks
     *
     * @param filterParams filtering parameters
     * @param pageable     parameters for pagination and sorting
     * @return filtered and sorted page of calibration tasks
     */
    @Transactional(readOnly = true)
    public Page<CalibrationTask> getFilteredPageOfCalibrationTasks(Map<String, String> filterParams,
                                                                   Pageable pageable, String username) {
        User user = userRepository.findByUsernameIgnoreCase(username);
        filterParams.put("organizationCode", user.getOrganization().getAdditionInfoOrganization().getCodeEDRPOU());
        CalibrationTaskSpecificationBuilder specificationBuilder = new CalibrationTaskSpecificationBuilder(filterParams);
        Specification<CalibrationTask> searchSpec = specificationBuilder.buildPredicate();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CalibrationTask> criteriaQuery = cb.createQuery(CalibrationTask.class);
        Root<CalibrationTask> root = criteriaQuery.from(CalibrationTask.class);
        Predicate predicate = searchSpec.toPredicate(root, criteriaQuery, cb);
        criteriaQuery.select(root);
        criteriaQuery.where(predicate);
        TypedQuery<CalibrationTask> typedQuery = em.createQuery(criteriaQuery);
        String s = typedQuery.unwrap(org.hibernate.Query.class).getQueryString();
        return taskRepository.findAll(searchSpec, pageable);
    }

    @Override
    public ListToPageTransformer<CalibrationTask> findPageOfCalibrationTasks(int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch,
                                                                             String name, String leaderFullName,
                                                                             String leaderPhone, String sortCriteria,
                                                                             String sortOrder, User calibratorEmployee, Boolean allTests) {

        CriteriaQuery<CalibrationTask> criteriaQuery;
        Long count;
        try {
            criteriaQuery = CalibrationTaskQueryConstructorCalibrator.buildSearchQuery(startDateToSearch, endDateToSearch,
                    name, leaderFullName, leaderPhone, calibratorEmployee, sortCriteria, sortOrder, em, allTests);
            count = em.createQuery(CalibrationTaskQueryConstructorCalibrator.buildCountQuery(startDateToSearch, endDateToSearch,
                    name, leaderFullName, leaderPhone, calibratorEmployee, em, allTests)).getSingleResult();
        } catch (ParseException e) {
            return null;
        }

        TypedQuery<CalibrationTask> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        String s = typedQuery.unwrap(org.hibernate.Query.class).getQueryString();
        List<CalibrationTask> calibrationTaskList = typedQuery.getResultList();

        ListToPageTransformer<CalibrationTask> result = new ListToPageTransformer<>();
        result.setContent(calibrationTaskList);
        result.setTotalItems(count);
        return result;
    }

    /**
     * This method fetches all calibration tasks and returns
     * a sorted page of them
     *
     * @param pageable parameters for pagination and sorting
     * @return sorted page of calibration tasks
     */
    @Transactional(readOnly = true)
    public Page<CalibrationTask> findAllCalibrationTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    /**
     * This method changes the date of calibration task
     *
     * @param taskID     ID of the calibration task the date of which is to be changed
     * @param dateOfTask new task date
     */
    @Transactional
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
    @Transactional
    public Boolean addNewTaskForStation(Date taskDate, String moduleSerialNumber, List<String> verificationsId, String userName) throws PermissionDeniedException, InvalidModuleSerialNumberException {
        Boolean taskAlreadyExists = true;
        Boolean taskHasCompleteVerification = false;
        CalibrationTask task = taskRepository.findByDateOfTaskAndModule_SerialNumber(taskDate, moduleSerialNumber);
        Iterable<Verification> verifications = verificationRepository.findAll(verificationsId);

        User user = userRepository.findByUsernameIgnoreCase(userName);
        if (user == null) {
            logger.error("User with name:" + userName + "was trying to create task for user with name: " + userName + " wasn't found");
            throw new PermissionDeniedException();
        }

        for (Verification verification : verifications) {
            if (!verification.getCalibrator().getId().equals(user.getOrganization().getId())) {
                logger.error("User with name: " + userName + " was trying to change verification and task from other organization");
                throw new PermissionDeniedException();
            }
            taskHasCompleteVerification = verification.getStatus().equals(Status.TEST_COMPLETED);
        }

        if (task == null | taskHasCompleteVerification.equals(true)) {
            taskAlreadyExists = false;
            CalibrationModule module = moduleRepository.findBySerialNumber(moduleSerialNumber);
            if (module == null) {
                logger.error("User with name:" + userName + "was trying to create task for module with serial number: " + moduleSerialNumber + " wasn't found");
                throw new InvalidModuleSerialNumberException();
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
    @Transactional
    public Boolean addNewTaskForTeam(Date taskDate, String serialNumber, List<String> verificationsId, String userId) throws PermissionDeniedException {
        Set<Verification> verifications = new HashSet<>();
        Boolean taskAlreadyExists = true;
        DisassemblyTeam team = teamRepository.findOne(serialNumber);
        User user = userRepository.findByUsernameIgnoreCase(userId);
        CalibrationTask calibrationTask = taskRepository.findByDateOfTaskAndTeam_Id(taskDate, team.getId());
        for (String verificationId : verificationsId) {
            Verification verification = verificationRepository.findOne(verificationId);
            if (!verification.getCalibrator().getId().equals(user.getOrganization().getId())) {
                logger.error("User with name: " + user.getUsername() + " was trying to change verification and task from other organization");
                throw new PermissionDeniedException();
            } else {
                if (team.getSpecialization().contains(verification.getDevice().getDeviceType())) {
                    if (calibrationTask == null) {
                        taskAlreadyExists = false;
                        calibrationTask = new CalibrationTask(null, team, new Date(), taskDate, user);
                        taskRepository.save(calibrationTask);
                    }
                    verification.setTaskStatus(Status.TASK_PLANED);
                    verification.setStatus(Status.SENT_TO_DISMANTLING_TEAM);
                    verification.setTask(calibrationTask);
                    verifications.add(verification);
                } else {
                    logger.error("verification and module has different device types");
                    throw new IllegalArgumentException();
                }
            }
        }
        verificationRepository.save(verifications);
        return taskAlreadyExists;
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
    @Transactional(readOnly = true)
    public long findVerificationsByCalibratorEmployeeAndTaskStatusCount(String userName) {
        User user = userRepository.findByUsernameIgnoreCase(userName);
        if (user == null) {
            logger.error("Cannot found user!");
        }
        Set<UserRole> roles = user.getUserRoles();
        for (UserRole role : roles) {
            if (role.equals(UserRole.CALIBRATOR_ADMIN)) {
                return verificationRepository.countByTaskStatusAndCalibratorIdAndProviderEmployeeUsernameIsNotNull(Status.PLANNING_TASK, user.getOrganization().getId());
            }
        }
        return verificationRepository.countByTaskStatusAndCalibratorEmployeeUsernameAndProviderEmployeeUsernameIsNotNull(Status.PLANNING_TASK, user.getUsername());

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
    @Transactional(readOnly = true)
    public Page<Verification> findByTaskStatusAndCalibratorId(Long id, int pageNumber, int itemsPerPage,
                                                              String sortCriteria, String sortOrder) {
        Pageable pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                "clientData.clientAddress.district", "clientData.clientAddress.street", "clientData.clientAddress.building", "clientData.clientAddress.flat"));
        switch (sortCriteria) {
            case "date":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "sentToCalibratorDate"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "sentToCalibratorDate"));
                }
                break;
            case "client_last_name":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "clientData.lastName", "clientData.firstName"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "clientData.lastName", "clientData.firstName"));
                }
                break;
            case "providerName":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "provider.name"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "provider.name"));
                }
                break;
            case "dateOfVerif":
            case "timeOfVerif":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "info.dateOfVerif", "info.timeFrom"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "info.dateOfVerif", "info.timeFrom"));
                }
                break;
            case "noWaterToDate":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "info.noWaterToDate"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                            "info.noWaterToDate"));
                }
                break;
            case "district":
            case "street":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC,
                            "clientData.clientAddress.district", "clientData.clientAddress.street", "clientData.clientAddress.building", "clientData.clientAddress.flat"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC,
                            "clientData.clientAddress.district", "clientData.clientAddress.street", "clientData.clientAddress.building", "clientData.clientAddress.flat"));
                }
                break;
            case "building":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "clientData.clientAddress.building"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "clientData.clientAddress.building"));
                }
                break;
            case "flat":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "clientData.clientAddress.flat"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "clientData.clientAddress.flat"));
                }
                break;
            case "serviceability":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "info.serviceability"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "info.serviceability"));
                }
                break;
            case "sealPresence":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "sealPresence"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "sealPresence"));
                }
                break;
            case "verificationWithDismantle":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "verificationWithDismantle"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "verificationWithDismantle"));
                }
                break;
            case "telephone":
                if (sortOrder.equals("asc")) {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.ASC, "clientData.phone"));
                } else {
                    pageRequest = new PageRequest(pageNumber - 1, itemsPerPage, new Sort(Sort.Direction.DESC, "clientData.phone"));
                }
                break;
        }

        return verificationRepository.findByTaskStatusAndCalibratorIdAndCounterStatusAndProviderEmployeeUsernameIsNotNull(Status.PLANNING_TASK, id, false, pageRequest);
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
    @Transactional(readOnly = true)
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
    @Override
    @Transactional
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

        ArrayList lists = getData(calibrationTask, verifications);
        List<TableExportColumn> dataForXls = (List<TableExportColumn>) lists.get(0);
        List<TableExportColumn> dataForDb = (List<TableExportColumn>) lists.get(1);

        XlsTableExporter xls = new XlsTableExporter();
        xls.exportToFile(dataForXls, xlsFile);

        DbTableExporter db = new DbTableExporter(Constants.DEFAULT_DB_TABLE_NAME);
        db.exportToFile(dataForDb, dbFile);

        String email = calibrationTask.getModule().getEmail();
        User user = calibratorEmployeeService.oneCalibratorEmployee(senderUsername);

        mailService.sendMailToStationWithAttachments(user, calibrationTask.getModule().getSerialNumber(), dateFormat.format(calibrationTask.getDateOfTask()).toString(), email, Constants.TASK + " " + calibrationTask.getId(), xlsFile, dbFile);
        calibrationTask.setStatus(Status.SENT_TO_TEST_DEVICE);
        for (Verification verification : verifications) {
            if (verification.getStatus().equals(Status.PLANNING_TASK) || verification.getStatus().equals(Status.TEST_PLACE_DETERMINED)) {
                verification.setStatus(Status.SENT_TO_TEST_DEVICE);
                verification.setTaskStatus(Status.SENT_TO_TEST_DEVICE);
            }
        }
        taskRepository.save(calibrationTask);
        verificationRepository.save(Arrays.asList(verifications));
    }

    @Override
    @Transactional
    public void sendTaskToTeam(Long id, String senderUsername) throws Exception {
        CalibrationTask calibrationTask = taskRepository.findOne(id);
        Verification[] verifications = verificationRepository.findByTaskIdOrderByQueueAsc(id);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DAY_MONTH_YEAR);

        String filename = calibrationTask.getTeam().getId() + "-" +
                dateFormat.format(calibrationTask.getDateOfTask()) + "_";

        File xlsFile = File.createTempFile(filename, "." + Constants.XLS_EXTENSION);
        xlsFile.setWritable(true);
        xlsFile.setReadable(true);
        xlsFile.setExecutable(true);

        XlsTableExporter xls = new XlsTableExporter();
        List<TableExportColumn> dataForXls = getDataForXls(calibrationTask, verifications);
        xls.exportToFile(dataForXls, xlsFile);

        mailService.sendMailWithAttachments(calibrationTask.getTeam().getLeaderEmail(), Constants.TASK + " " + calibrationTask.getId(), " ", xlsFile);
        calibrationTask.setStatus(Status.SENT_TO_DISMANTLING_TEAM);
        for (Verification verification : verifications) {
            if (verification.getStatus().equals(Status.PLANNING_TASK) || verification.getStatus().equals(Status.TEST_PLACE_DETERMINED)) {
                verification.setStatus(Status.SENT_TO_DISMANTLING_TEAM);
                verification.setTaskStatus(Status.SENT_TO_TEST_DEVICE);
            }
        }
        taskRepository.save(calibrationTask);
        verificationRepository.save(Arrays.asList(verifications));
    }

    private ArrayList getData(CalibrationTask calibrationTask, Verification[] verifications) {

        List<TableExportColumn> dataDb = new ArrayList<>();
        List<TableExportColumn> dataXls = new ArrayList<>();

        List<String> id = new ArrayList<>();
        List<String> surname = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> middlename = new ArrayList<>();
        List<String> city = new ArrayList<>();
        List<String> district = new ArrayList<>();
        List<String> region = new ArrayList<>();
        List<String> building = new ArrayList<>();
        List<String> flat = new ArrayList<>();
        List<String> street = new ArrayList<>();
        List<String> telephone = new ArrayList<>();
        List<String> mainPhone = new ArrayList<>();
        List<String> datetime = new ArrayList<>();
        List<String> counterNumber = new ArrayList<>();
        List<String> comment = new ArrayList<>();
        List<String> note = new ArrayList<>();
        List<String> customer = new ArrayList<>();
        List<String> taskDate = new ArrayList<>();
        List<String> provider = new ArrayList<>();
        List<String> entrance = new ArrayList<>();
        List<String> floor = new ArrayList<>();
        List<String> countersNumber = new ArrayList<>();
        List<String> fullName = new ArrayList<>();
        List<String> times = new ArrayList<>();
        List<String> symbols = new ArrayList<>();
        List<String> standartSize = new ArrayList<>();
        List<String> realiseYear = new ArrayList<>();
        List<String> acumulatedVolume = new ArrayList<>();

        String empty = " ";

        for (Verification verification : verifications) {
            SimpleDateFormat simpleTaskDate = new SimpleDateFormat("dd.MM.yyyy");
            try {
                taskDate.add(simpleTaskDate.format(calibrationTask.getDateOfTask()));
                String date = simpleTaskDate.format(calibrationTask.getDateOfTask());
                String time = getTime(verification);
                datetime.add(date + " " + time);
            } catch (IllegalArgumentException e) {
                taskDate.add(empty);
                logger.debug("The date of calibration task is absent or having wrong format, id of this task is:" + calibrationTask.getId(), e);
            }

            provider.add(getProvider(verification));
            id.add(verification.getId());
            countersNumber.add(String.valueOf(1));
            counterNumber.add(getCounterNumber(verification.getCounter()));
            comment.add(getComent(verification));
            note.add(getNotes(verification));
            customer.add(getCustomer(verification));


            Counter counter = verification.getCounter();
            symbols.add(getCounterSymbol(counter));
            standartSize.add(getCounterStandarSize(counter));
            realiseYear.add(getCounterReliasYear(counter));
            acumulatedVolume.add(getCounterAccumulatedVolume(counter));

            ClientData clientData = verification.getClientData();
            if (clientData != null) {
                setClientDataCard(clientData, fullName, surname, name, middlename, telephone);
                setClientAddress(verification.getClientData().getClientAddress(), region, district, city, street, building, flat);
                mainPhone.add(getMainPhoneNumber(verification.getClientData()));
            } else {
                setClientDataCard(null, fullName, surname, name, middlename, telephone);
                setClientAddress(null, region, district, city, street, building, flat);
                mainPhone.add(null);
            }
            setAdditionalInfo(verification.getInfo(), entrance, floor, times);
        }

        if (verifications[0].getCalibrationModule() != null && verifications[0].getCalibrationModule().getModuleType().equals(CalibrationModule.ModuleType.INSTALLATION_PORT)) {
            dataXls.add(new TableExportColumn(Constants.TASK_DATE, taskDate));
            dataXls.add(new TableExportColumn(Constants.PROVIDER, provider));
            dataXls.add(new TableExportColumn(Constants.REGION, district));
            dataXls.add(new TableExportColumn(Constants.STREET, street));
            dataXls.add(new TableExportColumn(Constants.BUILDING, building));
            dataXls.add(new TableExportColumn(Constants.FLAT, flat));
            dataXls.add(new TableExportColumn(Constants.ENTRANCE, entrance));
            dataXls.add(new TableExportColumn(Constants.FLOOR, floor));
            dataXls.add(new TableExportColumn(Constants.COUNTERS_NUMBER, countersNumber));
            dataXls.add(new TableExportColumn(Constants.FULL_NAME_SHORT, fullName));
            dataXls.add(new TableExportColumn(Constants.PHONE_NUMBER, telephone));
            dataXls.add(new TableExportColumn(Constants.DESIRABLE_TIME, times));
            dataXls.add(new TableExportColumn(Constants.VERIFICATION_ID, id));
            dataXls.add(new TableExportColumn(Constants.COMMENT, comment));
            dataXls.add(new TableExportColumn(Constants.NOTES, note));
        } else {
            dataXls.add(new TableExportColumn(Constants.TASK_DATE, taskDate));
            dataXls.add(new TableExportColumn(Constants.PROVIDER, provider));
            dataXls.add(new TableExportColumn(Constants.REGION, district));
            dataXls.add(new TableExportColumn(Constants.STREET, street));
            dataXls.add(new TableExportColumn(Constants.BUILDING, building));
            dataXls.add(new TableExportColumn(Constants.FLAT, flat));
            dataXls.add(new TableExportColumn(Constants.COUNTERS_NUMBER, countersNumber));
            dataXls.add(new TableExportColumn(Constants.FULL_NAME_SHORT, fullName));
            dataXls.add(new TableExportColumn(Constants.PHONE_NUMBER, telephone));
            dataXls.add(new TableExportColumn(Constants.COMMENT, comment));
            dataXls.add(new TableExportColumn(Constants.NOTES, note));
            dataXls.add(new TableExportColumn(Constants.COUNTER_SYMBOL, symbols));
            dataXls.add(new TableExportColumn(Constants.COUNTER_TYPE_SIZE, standartSize));
            dataXls.add(new TableExportColumn(Constants.COUNTER_NUMBER, counterNumber));
            dataXls.add(new TableExportColumn(Constants.COUNTER_YEAR, realiseYear));
            dataXls.add(new TableExportColumn(Constants.COUNTER_CAPACITY, acumulatedVolume));
        }

        dataDb.add(new TableExportColumn("id_pc", "INTEGER", id));
        dataDb.add(new TableExportColumn("surname", "TEXT", surname));
        dataDb.add(new TableExportColumn("name", "TEXT", name));
        dataDb.add(new TableExportColumn("middlename", "TEXT", middlename));
        dataDb.add(new TableExportColumn("city", "TEXT", city));
        dataDb.add(new TableExportColumn("district", "TEXT", district));
        dataDb.add(new TableExportColumn("bush", "TEXT", region));
        dataDb.add(new TableExportColumn("street", "TEXT", street));
        dataDb.add(new TableExportColumn("Building", "TEXT", building));
        dataDb.add(new TableExportColumn("Apartment", "TEXT", flat));
        dataDb.add(new TableExportColumn("Tel", "TEXT", mainPhone));
        dataDb.add(new TableExportColumn("Date_visit", "TEXT", datetime));
        dataDb.add(new TableExportColumn("Counter_number", "TEXT", counterNumber));
        dataDb.add(new TableExportColumn("Note", "TEXT", note));
        dataDb.add(new TableExportColumn("Customer", "TEXT", customer));

        ArrayList arrayList = new ArrayList(2);
        arrayList.add(dataXls);
        arrayList.add(dataDb);
        return arrayList;
    }

    private List<TableExportColumn> getDataForXls(CalibrationTask calibrationTask, Verification[] verifications) {
        List<TableExportColumn> data = new ArrayList<TableExportColumn>();

        // region Define lists
        List<String> taskDate = new ArrayList<>();
        List<String> provider = new ArrayList<>();
        List<String> district = new ArrayList<>();
        List<String> street = new ArrayList<>();
        List<String> building = new ArrayList<>();
        List<String> flat = new ArrayList<>();
        List<String> entrance = new ArrayList<>();
        List<String> floor = new ArrayList<>();
        List<String> doorCode = new ArrayList<>();
        List<String> countersNumber = new ArrayList<>();
        List<String> fullName = new ArrayList<>();
        List<String> telephone = new ArrayList<>();
        List<String> dateOfVerif = new ArrayList<>();
        List<String> times = new ArrayList<>();
        List<String> comment = new ArrayList<>();

        for (Verification verification : verifications) {
            SimpleDateFormat simpleTaskDate = new SimpleDateFormat("dd.MM.yyyy");
            String empty = " ";

            try {
                taskDate.add(simpleTaskDate.format(calibrationTask.getDateOfTask()));
            } catch (IllegalArgumentException e) {
                taskDate.add(empty);
                logger.debug("The date of calibration task is absent or having wrong format, id of this task is:" + calibrationTask.getId(), e);
            }

            provider.add(getProvider(verification));
            countersNumber.add(String.valueOf(1));
            comment.add(getComent(verification));
            dateOfVerif.add(getDateOfVerif(verification.getInfo()));
            times.add(getTime(verification.getInfo()));
            floor.add(getFloor(verification.getInfo()));
            entrance.add(getEntrance(verification.getInfo()));
            doorCode.add(getDoorCode(verification.getInfo()));

            ClientData clientData = verification.getClientData();
            if (clientData != null) {
                setClientDataCard(clientData, fullName, null, null, null, telephone);
                setClientAddress(verification.getClientData().getClientAddress(), null, district, null, street, building, flat);
            } else {
                setClientDataCard(null, fullName, null, null, null, telephone);
                setClientAddress(null, null, district, null, street, building, flat);
            }
        }

        data.add(new TableExportColumn(Constants.TASK_DATE, taskDate));
        data.add(new TableExportColumn(Constants.PROVIDER, provider));
        data.add(new TableExportColumn(Constants.REGION, district));
        data.add(new TableExportColumn(Constants.STREET, street));
        data.add(new TableExportColumn(Constants.BUILDING, building));
        data.add(new TableExportColumn(Constants.FLAT, flat));
        data.add(new TableExportColumn(Constants.ENTRANCE, entrance));
        data.add(new TableExportColumn(Constants.FLOOR, floor));
        data.add(new TableExportColumn(Constants.DOORCODE, doorCode));
        data.add(new TableExportColumn(Constants.COUNTERS_NUMBER, countersNumber));
        data.add(new TableExportColumn(Constants.FULL_NAME_SHORT, fullName));
        data.add(new TableExportColumn(Constants.PHONE_NUMBER, telephone));
        data.add(new TableExportColumn(Constants.DESIRABLE_DATE, dateOfVerif));
        data.add(new TableExportColumn(Constants.DESIRABLE_TIME, times));
        data.add(new TableExportColumn(Constants.COMMENT, comment));

        return data;
    }

    private void setClientDataCard(ClientData clientData, List<String> fullName, List<String> surname, List<String> name, List<String> middlename, List<String> telephone) {
        String empty = " ";
        if (clientData != null) {
            if (fullName != null) fullName.add(getFullName(clientData));
            if (telephone != null) telephone.add(getPhoneNumber(clientData));
            if (name != null) name.add(getName(clientData));
            if (surname != null) surname.add(getSurname(clientData));
            if (middlename != null) middlename.add(getMiddleName(clientData));
        } else {
            fullName.add(empty);
            telephone.add(empty);
            name.add(empty);
            surname.add(empty);
            middlename.add(empty);

        }
    }

    private void setClientAddress(Address clientAddress, List<String> region, List<String> district, List<String> city, List<String> street, List<String> building, List<String> flat) {
        String empty = " ";
        if (clientAddress != null) {
            if (district != null) district.add(getDistrict(clientAddress));
            if (building != null) building.add(getBuilding(clientAddress));
            if (flat != null) flat.add(getFlat(clientAddress));
            if (city != null) city.add(getCity(clientAddress));
            if (region != null) region.add(getRegion(clientAddress));
            if (street != null) street.add(getStreet(clientAddress));
        } else {
            district.add(empty);
            building.add(empty);
            flat.add(empty);
            city.add(empty);
            region.add(empty);
            street.add(empty);
        }
    }

    private void setAdditionalInfo(AdditionalInfo info, List<String> entrance, List<String> floor, List<String> times) {
        String empty = " ";
        if (info != null) {
            entrance.add(getEntrance(info));
            floor.add(getFloor(info));
            times.add(getTime(info));
        } else {
            entrance.add(empty);
            floor.add(empty);
            times.add(empty);
        }
    }

    private String getNotes(Verification verification) {
        return getComent(verification) + " , " + Constants.ENTRANCE + ": " + getEntrance(verification.getInfo()) + ", " + Constants.DOORCODE + ": " + getDoorCode(verification.getInfo()) + ", " + Constants.FLOOR + ": " + getFloor(verification.getInfo());
    }

    private String getTime(AdditionalInfo info) {
        return info.getTimeFrom() != null && info.getTimeTo() != null ? info.getTimeFrom() + "-" + info.getTimeTo() : " ";
    }

    private String getDateOfVerif(AdditionalInfo info) {
        return info != null && info.getDateOfVerif() != null ? info.getDateOfVerif().toString() : " ";
    }

    private String getFloor(AdditionalInfo info) {
        return info.getFloor() != 0 ? String.valueOf(info.getFloor()) : " ";
    }

    private String getDoorCode(AdditionalInfo info) {
        return String.valueOf(info.getDoorCode() != 0 ? info.getDoorCode() : " ");
    }

    private String getEntrance(AdditionalInfo info) {
        return info.getEntrance() != 0 ? String.valueOf(info.getEntrance()) : " ";
    }

    public String getCounterAccumulatedVolume(Counter counter) {
        return counter != null && counter.getAccumulatedVolume() != null ? counter.getAccumulatedVolume() : "-";
    }

    public String getCounterReliasYear(Counter counter) {
        return counter != null && counter.getReleaseYear() != null ? counter.getReleaseYear() : "-";
    }

    public String getCounterStandarSize(Counter counter) {
        return counter != null && counter.getCounterType() != null && counter.getCounterType().getStandardSize() != null ? counter.getCounterType().getStandardSize() : "-";
    }

    public String getCounterSymbol(Counter counter) {
        return counter != null && counter.getCounterType() != null && counter.getCounterType().getSymbol() != null ? counter.getCounterType().getSymbol() : "-";
    }

    public String getCounterNumber(Counter counter) {
        return counter != null && counter.getNumberCounter() != null ? counter.getNumberCounter() : "-";
    }

    public String getCustomer(Verification verification) {
        return verification.getProvider().getName();
    }

    public String getComent(Verification verification) {
        return verification.getInfo() != null && verification.getInfo().getNotes().toString() != null ? verification.getInfo().getNotes().toString() : " ";
    }

    public String getTime(Verification verification) {
        return verification.getInfo() != null && verification.getInfo().getTimeFrom() != null && verification.getInfo().getTimeTo() != null ? verification.getInfo().getTimeFrom() + "" : " ";
    }

    public String getStreet(Address clientAddress) {
        return clientAddress.getStreet() != null ? clientAddress.getStreet() : " ";
    }

    public String getRegion(Address clientAddress) {
        return clientAddress.getRegion() != null ? clientAddress.getRegion() : " ";
    }

    public String getName(ClientData clientData) {
        return clientData.getFirstName() != null ? clientData.getFirstName() : " ";
    }

    public String getSurname(ClientData clientData) {
        return clientData.getLastName() != null ? clientData.getLastName() : " ";
    }

    public String getCity(Address clientAddress) {
        return clientAddress.getLocality() != null ? clientAddress.getLocality() : " ";
    }

    public String getMiddleName(ClientData clientData) {
        return clientData.getMiddleName() != null ? clientData.getMiddleName() : " ";
    }

    public String getProvider(Verification verification) {
        return verification.getProvider() != null && verification.getProvider().getName() != null ? verification.getProvider().getName() : " ";
    }

    public String getFlat(Address clientAddress) {
        return clientAddress.getFlat() != null ? clientAddress.getFlat() : " ";
    }

    public String getBuilding(Address clientAddress) {
        return clientAddress.getBuilding() != null ? clientAddress.getBuilding() : " ";
    }

    public String getAddress(Address clientAddress) {
        return clientAddress.getAddress() != null ? clientAddress.getAddress() : " ";
    }

    public String getDistrict(Address clientAddress) {
        return clientAddress.getDistrict() != null ? clientAddress.getDistrict() : " ";
    }

    public String getPhoneNumber(ClientData clientData) {
        if (clientData.getSecondPhone() != null) {
            return clientData.getPhone() != null ? clientData.getPhone() + "," + clientData.getSecondPhone() : " ";
        } else {
            return clientData.getPhone() != null ? clientData.getPhone() : " ";
        }
    }

    public String getMainPhoneNumber(ClientData clientData) {
        return clientData.getPhone() != null ? clientData.getPhone() : " ";
    }

    public String getFullName(ClientData clientData) {
        return clientData.getFullName() != null ? clientData.getFullName() : " ";
    }
}