package com.softserve.edu.service.verification.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.verification.VerificationGroup;
import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.calibration.RejectedInfo;
import com.softserve.edu.repository.*;
import com.softserve.edu.repository.CalibrationPlanningTaskRepository;
import com.softserve.edu.repository.CalibrationTestRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.exceptions.NotAvailableException;
import com.softserve.edu.service.utils.*;
import com.softserve.edu.service.verification.VerificationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class VerificationServiceImpl implements VerificationService {

    private Logger logger = Logger.getLogger(VerificationServiceImpl.class);

    @Autowired
    private VerificationGroupRepository groupRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private CalibrationTestRepository calibrationTestRepository;

    @Autowired
    private CounterTypeRepository counterTypeRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private AdditionalInfoRepository additionalInfoRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void saveVerification(Verification verification) {
        verificationRepository.save(verification);
    }

    @Override
    @Transactional(readOnly = true)
    public Verification findById(String code) {
        return verificationRepository.findOne(code);
    }

    /**
     * Returns requested number(page) of Verification entities(itemsPerPage
     * parameter) that belongs to specific organization. Note: pagination starts
     * from 1 at client side, but Spring Data JPA from 0.
     *
     * @param pageNumber   Number of partial data that will be returned.
     * @param itemsPerPage Number of Verification-s that will be present in one page(unit
     *                     of partial data).
     * @return Requested page of Verification-s that belong to specific
     * organization.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Verification> findPageOfAllVerificationsByProviderId(Long providerId, int pageNumber,
                                                                     int itemsPerPage) {
        Pageable pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
        return verificationRepository.findByProviderId(providerId, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Verification> findPageOfAllVerificationsByCalibratorId(Long calibratorId, int pageNumber,
                                                                       int itemsPerPage) {
        Pageable pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
        return verificationRepository.findByCalibratorId(calibratorId, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Verification> findPageOfAllVerificationsByStateVerificatorId(Long stateVerificatorId, int pageNumber,
                                                                             int itemsPerPage) {
        Pageable pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
        return verificationRepository.findByStateVerificatorId(stateVerificatorId, pageRequest);
    }

    /**
     * Finds count of rows in database for verifications assigned to Calibrator with Read Status = 'UNREAD'.
     * Method is used for notification about unwatched verifications
     *
     * @param calibratorId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Long findCountOfNewVerificationsByCalibratorId(Long calibratorId) {
        return verificationRepository.countByCalibratorIdAndStatusAndCalibratorEmployeeUsername(calibratorId, Status.IN_PROGRESS,
                null);
    }

    /**
     * Finds count of rows in database for verifications assigned to Provider with Read Status = 'UNREAD'.
     * Method is used for notification about unwatched verifications
     *
     * @param providerId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Long findCountOfNewVerificationsByProviderId(Long providerId) {
        return verificationRepository.countByProviderIdAndStatusAndReadStatus(providerId, Status.SENT,
                Verification.ReadStatus.UNREAD);
    }

    /**
     * Finds count of rows in database for verifications assigned to Provider with Read Status = 'UNREAD'.
     * Method is used for notification about unwatched verifications
     *
     * @param employeeUsername name of responsible person
     * @return count of accepted verifications
     */
    @Override
    @Transactional(readOnly = true)
    public Long findCountOfAcceptedVerificationsByProviderEmployeeUsername(String employeeUsername) {
        return verificationRepository.countByProviderEmployeeUsernameAndStatus(employeeUsername, Status.ACCEPTED);
    }

    /**
     * Finds count of rows in database for verifications assigned to State Verificator with Read Status = 'UNREAD'.
     * Method is used for notification about unwatched verifications
     *
     * @param stateVerificatorId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Long findCountOfNewVerificationsByStateVerificatorId(Long stateVerificatorId) {
        return verificationRepository.countByStateVerificatorIdAndStatusAndReadStatus(stateVerificatorId,
                Status.SENT_TO_VERIFICATOR, Verification.ReadStatus.UNREAD);
    }

    @Override
    public Long findCountOfRejectedProtocolsByStateVerificatorId(Long stateVerificatorId) {
        return verificationRepository.countByStateVerificatorIdAndStatus(stateVerificatorId, Status.PROTOCOL_REJECTED);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Verification> findPageOfSentVerificationsByProviderId(Long providerId, int pageNumber,
                                                                      int itemsPerPage) {
        Pageable pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
        return verificationRepository.findByProviderIdAndStatusOrderByInitialDateDesc(providerId, Status.SENT,
                pageRequest);
    }

    /**
     * Returns requested number(page) of Verification entities(itemsPerPage
     * parameter) that belongs to specific calibrator and have status received.
     * Note: pagination starts from 1 at client side, but Spring Data JPA from
     * 0.
     *
     * @param calibratorId id of calibrator.
     * @param pageNumber   Number of partial data that will be returned.
     * @param itemsPerPage Number of Verification-s that will be present in one page(unit
     *                     of partial data).
     * @return Requested page of Verification-s that belong to specific
     * organization.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Verification> findPageOfSentVerificationsByCalibratorId(Long calibratorId, int pageNumber,
                                                                        int itemsPerPage) {
        Pageable pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
        return verificationRepository.findByCalibratorIdAndStatusOrderByInitialDateDesc(calibratorId,
                Status.IN_PROGRESS, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Verification> findPageOfSentVerificationsByStateVerificatorId(Long stateVerificatorId, int pageNumber,
                                                                              int itemsPerPage) {
        Pageable pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
        return verificationRepository.findByStateVerificatorIdAndStatus(stateVerificatorId, Status.SENT_TO_VERIFICATOR,
                pageRequest);
    }


    /**
     * Find page of new verifications for provider with search parameters specified
     *
     * @param providerId        ID of organization
     * @param pageNumber        number of page requested by user
     * @param itemsPerPage      desired number of rows to be displayed on page
     * @param startDateToSearch search by initial date of verification
     * @param endDateToSearch   end date
     * @param idToSearch        search by verification ID
     * @param streetToSearch    search by street where client lives
     * @param providerEmployee  restrict query by provider employee user name. Allows restrict query so that simple
     *                          employee user
     *                          can only see verifications assigned to him and free verifications (not yet assigned)
     * @return ListToPageTransformer<Verification>
     */
    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfSentVerificationsByProviderIdAndCriteriaSearch(Long providerId, int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch,
                                                                                                        String streetToSearch, String region, String district, String locality, String status, String employeeName, String sortCriteria, String sortOrder, User providerEmployee) {

        CriteriaQuery<Verification> criteriaQuery = NewVerificationsQueryConstructorProvider.buildSearchQuery(providerId, startDateToSearch, endDateToSearch,
                idToSearch, fullNameToSearch, streetToSearch, region, district,
                locality, status,
                providerEmployee, sortCriteria,
                sortOrder, employeeName, em);

        Long count = em.createQuery(NewVerificationsQueryConstructorProvider.buildCountQuery(providerId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, providerEmployee, employeeName, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();

        ListToPageTransformer<Verification> result = new ListToPageTransformer<Verification>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByProviderId(Long organizationId, int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch,
                                                                                          String streetToSearch, String region, String district, String locality, String status, String employeeName, String building, String flat, String calibratorName, String sortCriteria, String sortOrder, User providerEmployee) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorProvider.buildSearchQuery(organizationId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, employeeName, building, flat, calibratorName, sortCriteria, sortOrder, providerEmployee, em);

        Long count = em.createQuery(ArchivalVerificationsQueryConstructorProvider.buildCountQuery(organizationId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, employeeName, building, flat, calibratorName, providerEmployee, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();

        ListToPageTransformer<Verification> result = new ListToPageTransformer<>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfRejectedVerificationsByProviderId(Long organizationId, int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch, String rejectedReason,
                                                                                           String providerEmployee, String calibratorName, String customerName, String district, String street, String building, String flat,
                                                                                           String verificationId, String status, String sortCriteria, String sortOrder) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorProvider.buildSearchRejectedProviderQuery(organizationId, startDateToSearch, endDateToSearch, rejectedReason, providerEmployee, calibratorName,
                customerName, district, street, building, flat, verificationId, status, sortCriteria, sortOrder, em);
        Long count = em.createQuery(ArchivalVerificationsQueryConstructorProvider.buildCountRejectedQuery(organizationId, startDateToSearch, endDateToSearch, rejectedReason, providerEmployee, calibratorName,
                customerName, district, street, building, flat, verificationId, status, em)).getSingleResult();
        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();
        ListToPageTransformer<Verification> result = new ListToPageTransformer<Verification>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }


    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByProviderIdOnMainPanel(Long organizationId, int pageNumber, int itemsPerPage, String initialDateToSearch, String idToSearch, String fullNameToSearch,
                                                                                                     String streetToSearch, String region, String district, String locality, String status, String employeeName, String building, String flat, String calibratorName, User providerEmployee) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorProvider.buildSearchQuery(organizationId, initialDateToSearch, null, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, "SENT", employeeName, building, flat, calibratorName, null, null, providerEmployee, em);

        Long count = em.createQuery(ArchivalVerificationsQueryConstructorProvider.buildCountQuery(organizationId, initialDateToSearch, null, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, "SENT", employeeName, building, flat, calibratorName, providerEmployee, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();

        ListToPageTransformer<Verification> result = new ListToPageTransformer<>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByCalibratorIdOnMainPanel(Long organizationId, int pageNumber, int itemsPerPage, String initialDateToSearch, String idToSearch, String fullNameToSearch,
                                                                                                       String streetToSearch, String region, String district, String locality, String status, String employeeName, User calibratorEmployee) {
        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorCalibrator.buildSearchQuery(organizationId, initialDateToSearch, null, idToSearch, fullNameToSearch, streetToSearch, "IN_PROGRESS", employeeName, null, null, null, null, null, null, null, em);

        Long count = em.createQuery(ArchivalVerificationsQueryConstructorCalibrator.buildCountQuery(organizationId, initialDateToSearch, null, idToSearch, fullNameToSearch, streetToSearch, "IN_PROGRESS", employeeName, null, null, null, null, null, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();

        ListToPageTransformer<Verification> result = new ListToPageTransformer<>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByVerificatorIdOnMainPanel(Long organizationId, int pageNumber, int itemsPerPage) {
        CriteriaQuery<Verification> criteriaQuery = MainPanelVerificationsQueryConstructorVerificator.buildSearchQuery(organizationId, em);

        Long count = em.createQuery(MainPanelVerificationsQueryConstructorVerificator.buildCountQuery(organizationId, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();

        ListToPageTransformer<Verification> result = new ListToPageTransformer<>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    //TODO: refactor methods of other guys (not only provider) to include endDateToSearch and name
    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfVerificationsByCalibratorIdAndCriteriaSearch(Long calibratorId, int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch,
                                                                                                      String streetToSearch, String region, String district, String locality, String status, String employeeName, String standardSize, String symbol, String nameProvider, String realiseYear, String dismantled, String building, String flat, String numberCounter, String comment, String sortCriteria, String sortOrder, User calibratorEmployee, ArrayList<Map<String, Object>> globalSearchParams) {


        CriteriaQuery<Verification> criteriaQuery = NewVerificationsQueryConstructorCalibrator.buildSearchQuery(calibratorId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled, building, flat, numberCounter, comment, sortCriteria, sortOrder, employeeName, em, globalSearchParams);


        Long count = em.createQuery(NewVerificationsQueryConstructorCalibrator.buildCountQuery(calibratorId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled, building, flat, numberCounter, comment, employeeName, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();
        ListToPageTransformer<Verification> result = new ListToPageTransformer<Verification>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }


    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByCalibratorId(Long organizationId, int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch, String idToSearch,
                                                                                            String fullNameToSearch, String streetToSearch, String status, String employeeName, Long protocolId, String protocolStatus,
                                                                                            String numberCounter, String measurementDeviceType, String sortCriteria, String sortOrder, User calibratorEmployee) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorCalibrator.buildSearchQuery(organizationId, startDateToSearch, endDateToSearch,
                idToSearch, fullNameToSearch, streetToSearch, status, employeeName, protocolId, protocolStatus, numberCounter, measurementDeviceType, sortCriteria, sortOrder, calibratorEmployee, em);
        Long count = em.createQuery(ArchivalVerificationsQueryConstructorCalibrator.buildCountQuery(organizationId, startDateToSearch, endDateToSearch,
                idToSearch, fullNameToSearch, streetToSearch, status, employeeName, protocolId, protocolStatus, numberCounter, measurementDeviceType, calibratorEmployee, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();

        ListToPageTransformer<Verification> result = new ListToPageTransformer<Verification>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfRejectedVerificationsByCalibratorId(Long organizationId, int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch, String rejectedReason,
                                                                                             String rejectedEmployee, String providerName, String customerName, String district, String street, String building, String flat,
                                                                                             String verificationId, String sortCriteria, String sortOrder, User calibratorEmployee) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorCalibrator.buildSearchRejectedQuery(organizationId, startDateToSearch, endDateToSearch, rejectedReason, rejectedEmployee, providerName,
                customerName, district, street, building, flat, verificationId, sortCriteria, sortOrder, calibratorEmployee, em);

        Long count = em.createQuery(ArchivalVerificationsQueryConstructorCalibrator.buildCountRejectedQuery(organizationId, startDateToSearch, endDateToSearch, rejectedReason, rejectedEmployee, providerName,
                customerName, district, street, building, flat, verificationId, sortCriteria, sortOrder, calibratorEmployee, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();
        ListToPageTransformer<Verification> result = new ListToPageTransformer<Verification>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfPlaningTaskVerificationsByCalibratorId(Long organizationId, Integer pageNumber, Integer itemsPerPage, User user, String date, String endDate, String client_full_name, String provider, String district,
                                                                                                String street, String building, String flat, String dateOfVerif, String time, String serviceability, String noWaterToDate, String sealPresence,
                                                                                                String telephone, String verificationWithDismantle, String notes, String sortCriteria, String sortOrder) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorCalibrator.buildSearchPlaningTaskQuery(organizationId, pageNumber, itemsPerPage, user, date, endDate, client_full_name, provider, district,
                street, building, flat, dateOfVerif, time, serviceability, noWaterToDate, sealPresence, telephone, verificationWithDismantle, notes, sortCriteria, sortOrder, em);

        Long count = em.createQuery(ArchivalVerificationsQueryConstructorCalibrator.buildCountPlaningTaskQuery(organizationId, pageNumber, itemsPerPage, user, date, endDate, client_full_name, provider, district,
                street, building, flat, dateOfVerif, time, serviceability, noWaterToDate, sealPresence, telephone, verificationWithDismantle, notes, sortCriteria, sortOrder, em)).getSingleResult();
        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);

        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();
        ListToPageTransformer<Verification> result = new ListToPageTransformer<Verification>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional
    public void updateAdditionalInfoNotes(String comment, String id) {
        Verification verification = null;
        if (id != null) {
            verification = verificationRepository.findOne(id);
        }
        if (verification != null) {
            AdditionalInfo additionalInfo = verification.getInfo();
            String currentComment = additionalInfo.getNotes();
            if (currentComment == null) {
                additionalInfo.setNotes("__" + Constants.DIVIDE_NOTES + comment + "__");
                additionalInfoRepository.save(additionalInfo);
            } else if (currentComment.lastIndexOf("__" + Constants.DIVIDE_NOTES.charAt(0)) == -1) {
                additionalInfo.setNotes(currentComment + Constants.DIVIDE_NOTES + comment + "__");
                additionalInfoRepository.save(additionalInfo);
            } else if (!currentComment.substring(currentComment.lastIndexOf(Constants.DIVIDE_NOTES.charAt(0)) + 1).equals(comment)) {
                additionalInfo.setNotes(currentComment + Constants.DIVIDE_NOTES + comment + "__");
                additionalInfoRepository.save(additionalInfo);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfVerificationsByVerificatorIdAndCriteriaSearch(Long verificatorId, int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch, String idToSearch, String status, String nameProvider,
                                                                                                       String nameCalibrator, String numberOfCounter, String numberOfProtocol, String sentToVerificatorDateFrom, String sentToVerificatorDateTo, String moduleNumber, String employeeLastName,
                                                                                                       String sortCriteria, String sortOrder, User verificatorEmployee) {

        CriteriaQuery<Verification> criteriaQuery = NewVerificationsQueryConstructorVerificator.buildSearchQuery(verificatorId, startDateToSearch, endDateToSearch, idToSearch, status, verificatorEmployee,
                nameProvider, nameCalibrator, numberOfCounter, numberOfProtocol, sentToVerificatorDateFrom, sentToVerificatorDateTo, moduleNumber, employeeLastName, sortCriteria, sortOrder, em);

        Long count = em.createQuery(NewVerificationsQueryConstructorVerificator.buildCountQuery(verificatorId, startDateToSearch, endDateToSearch, idToSearch, status, verificatorEmployee,
                nameProvider, nameCalibrator, numberOfCounter,
                numberOfProtocol, sentToVerificatorDateFrom, sentToVerificatorDateTo, moduleNumber, employeeLastName, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();
        ListToPageTransformer<Verification> result = new ListToPageTransformer<Verification>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Verification findNextVerificationByVerificationIndexAndModuleNumberAndTestDate(int verificationIndex, String moduleNumber, java.sql.Date testDate, User employee) {

        CriteriaQuery<Verification> criteriaQuery = VerificationsQueryConstructor.buildSearchQuery(moduleNumber, testDate, employee, em);
        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult(verificationIndex);
        typedQuery.setMaxResults(1);
        List<Verification> verificationList = typedQuery.getResultList();
        if (verificationList.isEmpty()) {
            return null;
        } else {
            return verificationList.get(0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long findCountByVerificationIndexAndModuleNumberAndTestDate(String moduleNumber, java.sql.Date testDate, User employee) {

        CriteriaQuery<Long> criteriaQuery = VerificationsQueryConstructor.buildCountQuery(moduleNumber, testDate, employee, em);
        return em.createQuery(criteriaQuery).getSingleResult();
    }


    @Override
    public Long getPositionOfVerification(String verificationId, String moduleId, Date date) {
        return verificationRepository.getNumberOfRowByVerificationIdAndModuleIdAndDate(verificationId, moduleId);
    }

    @Override
    public List<Verification> findByCounterNumberAndCalibratorIdAndStatusNot(String numberCounter, Long calibratorId, Status status1, Status status2) {
        return verificationRepository.findByCounterNumberAndCalibratorIdAndStatusNot(numberCounter, calibratorId, status1, status2);
    }

    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByVerificatorId(Long organizationId, int pageNumber, int itemsPerPage, String dateToSearch, String idToSearch, String fullNameToSearch,
                                                                                             String streetToSearch, String status, String employeeName, String sortCriteria, String sortOrder, User verificatorEmployee) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorVerificator.buildSearchQuery(organizationId, dateToSearch, idToSearch, fullNameToSearch, streetToSearch, status, employeeName,
                sortCriteria, sortOrder, verificatorEmployee, em);

        Long count = em.createQuery(ArchivalVerificationsQueryConstructorVerificator.buildCountQuery(organizationId, dateToSearch, idToSearch, fullNameToSearch, streetToSearch, status, employeeName, verificatorEmployee, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();

        ListToPageTransformer<Verification> result = new ListToPageTransformer<>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<CalibrationTest> findPageOfCalibrationTestsByVerificationId(int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch, String name, String region, String district, String locality, String streetToSearch, String idToSearch,
                                                                                             String fullNameToSearch, Integer settingNumber, String consumptionStatus, Long protocolId, String testResult, Long measurementDeviceId, String measurementDeviceType, String sortCriteria, String sortOrder) {

        CriteriaQuery<CalibrationTest> criteriaQuery = CalibrationTestQueryConstructorCalibrator.buildSearchQuery(startDateToSearch, endDateToSearch, name, region, district, locality, streetToSearch,
                idToSearch, fullNameToSearch, settingNumber, consumptionStatus, protocolId, testResult, measurementDeviceId, measurementDeviceType, sortCriteria, sortOrder, em);

        Long count = em.createQuery(CalibrationTestQueryConstructorCalibrator.buildCountQuery(startDateToSearch, endDateToSearch, name, region, district, locality, streetToSearch,
                idToSearch, fullNameToSearch, settingNumber, consumptionStatus, protocolId, testResult, measurementDeviceId, measurementDeviceType, em)).getSingleResult();

        TypedQuery<CalibrationTest> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<CalibrationTest> calibrationTestList = typedQuery.getResultList();

        ListToPageTransformer<CalibrationTest> result = new ListToPageTransformer<>();
        result.setContent(calibrationTestList);
        result.setTotalItems(count);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Verification findByIdAndProviderId(String id, Long providerId) {
        Verification verification = verificationRepository.findByIdAndProviderId(id, providerId);
        if (verification == null) {
            throw new AccessDeniedException("You have not permission to get this data.");
        }
        return verification;
    }

    /**
     * Returns requested number(page) of Verification entities(itemsPerPage
     * parameter) that belongs to specific calibrator and have status received.
     * Note: pagination starts from 1 at client side, but Spring Data JPA from
     * 0.
     *
     * @param id           Id of the verification
     * @param calibratorId Number id of provider assigned to this verification
     * @return Verification that belong to specific calibrator
     */
    @Override
    @Transactional(readOnly = true)
    public Verification findByIdAndCalibratorId(String id, Long calibratorId) {
        Verification verification = verificationRepository.findByIdAndCalibratorId(id, calibratorId);
        if (verification == null) {
            throw new AccessDeniedException("You have not permission to get this data.");
        }
        return verification;
    }

    @Override
    @Transactional(readOnly = true)
    public Verification findByIdAndStateVerificatorId(String id, Long stateVerificatorId) {
        Verification verification = verificationRepository.findByIdAndStateVerificatorId(id, stateVerificatorId);
        if (verification == null) {
            throw new AccessDeniedException("You have not permission to get this data.");
        }
        return verification;
    }

    /**
     * Changes verification read status to 'READ' when Provider or Calibrator or State Verificator reads it
     *
     * @param verificationId
     * @param readStatus
     */
    @Override
    @Transactional
    public void updateVerificationReadStatus(String verificationId, String readStatus) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification == null) {
            logger.error("verification haven't found");
            return;
        }
        verification.setReadStatus(Verification.ReadStatus.READ);
        verificationRepository.save(verification);
    }

    @Override
    @Transactional
    public void updateVerificationStatus(String verificationId, Status status) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification == null) {
            logger.error("verification haven't found");
            return;
        }
        verification.setStatus(status);
        verification.setReadStatus(Verification.ReadStatus.READ);
        verification.setExpirationDate(null);
        verificationRepository.save(verification);
    }

    @Override
    @Transactional
    public void sendVerificationTo(String verificationId, Organization oraganization, Status status) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification == null) {
            logger.error("verification haven't found");
            return;
        }
        if (status.equals(Status.IN_PROGRESS)) {
            verification.setCalibrator(oraganization);
            verification.setSentToCalibratorDate(new Date());
        } else if (status.equals(Status.SENT_TO_VERIFICATOR)) {
            verification.setStateVerificator(oraganization);
            verification.setSentToVerificatorDate(new Date());
        } else if ((status.equals(Status.TEST_OK)) || (status.equals(Status.TEST_NOK))) {
            verification.setProvider(oraganization);
        } else if (status.equals((Status.SENT_TO_PROVIDER))) {
            verification.setProvider(oraganization);
        }
        verification.setStatus(status);
        verification.setReadStatus(Verification.ReadStatus.UNREAD);
        verification.setExpirationDate(null);
        verificationRepository.save(verification);
    }


    /**
     * Find verification, add complete status to stateVerificator, add
     * stateVerificator to verification save verification
     */

    @Override
    @Transactional
    public void updateVerification(String verificationId, Organization stateVerificator) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification == null) {
            logger.error("verification haven't found");
            return;
        }
        verification.setStatus(Status.TEST_OK);
        verification.setStateVerificator(stateVerificator);
        verificationRepository.save(verification);
    }

    @Override
    @Transactional
    public void updateVerificationData(String id, ClientData clientData, Organization provider) {
        Verification verificationToEdit = verificationRepository.findOne(id);
        verificationToEdit.setInitialDate(new Date());
        verificationToEdit.setClientData(clientData);
        verificationToEdit.setProvider(provider);
        verificationToEdit.setStatus(Status.SENT);
        verificationToEdit.setReadStatus(Verification.ReadStatus.UNREAD);
        verificationRepository.save(verificationToEdit);
    }

    @Override
    @Transactional
    public void rejectVerification(Verification verification, RejectedInfo rejectedInfo, Status status) {
        verification.setStatus(status);
        verification.setTaskStatus(null);
        verification.setRejectedCalibratorDate(new Date());
        verification.setRejectedInfo(rejectedInfo);
        verification.setTaskStatus(Status.PLANNING_TASK);
        verification.setTask(null);
        verificationRepository.save(verification);
    }


    @Override
    @Transactional
    public boolean updateVerificationQueue(List<Verification> verifications, Long calibratorId) {

        List<String> listId = new ArrayList<>();
        for (Verification verificationNew : verifications) {
            listId.add(verificationNew.getId());
        }

        List<Verification> verificationList = verificationRepository.findByIdIn(listId);
        for (Verification verification : verificationList) {
            if (verification.getCalibrator().getId() != calibratorId) {
                logger.warn("Access denied");
                return false;
            }
        }

        for (Verification verificationNew : verifications) {
            verificationRepository.updateVerificationQueueById(verificationNew.getQueue(), verificationNew.getId());
        }
        return true;
    }

    /**
     * Returns calibration test assigned to verification
     *
     * @param verificationId Id of the verification
     * @param data           all data filled by calibrator in test
     * @return test data with assigned verification that belong to specific
     * calibrator
     * @throws NotAvailableException if there is no verification with such id
     */
    @Override
    @Transactional
    public CalibrationTest createCalibrationTest(String verificationId, CalibrationTest data) {
        Verification updatedVerification = verificationRepository.findOne(verificationId);
        if (updatedVerification == null) {
            throw new NotAvailableException("Повірки з таким ID не існує");
        }
        CalibrationTest testData = calibrationTestRepository.save(data);
        testData.setVerification(updatedVerification);
        return testData;
    }

    @Override
    @Transactional(readOnly = true)
    public CalibrationTest findByCalibrationTestId(Long id) {
        CalibrationTest calibrationTest = calibrationTestRepository.findById(id);
        if (calibrationTest == null) {
            throw new AccessDeniedException("You have not permission to get this data");
        }
        return calibrationTest;
    }

    @Override
    @Transactional(readOnly = true)
    public int findCountOfAllSentVerifications(Organization organization) {
        return verificationRepository.getCountOfAllSentVerifications(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public int findCountOfAllAcceptedVerification(Organization organization) {
        return verificationRepository.getCountOfAllAcceptedVerifications(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public int findCountOfAllCalibratorVerificationWithoutEmployee(Organization organization) {
        return verificationRepository.findCountOfAllCalibratorVerificationWithoutEmployee(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public int findCountOfAllCalibratorVerificationWithEmployee(Organization organization) {
        return verificationRepository.findCountOfAllCalibratorVerificationWithEmployee(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public int findCountOfAllVerificatorVerificationWithoutEmployee(Organization organization) {
        return verificationRepository.findCountOfAllVerificatorVerificationWithoutEmployee(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public int findCountOfAllVerificatorVerificationWithEmployee(Organization organization) {
        return verificationRepository.findCountOfAllVerificatorVerificationWithEmployee(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getProcessTimeProvider() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = cb.createQuery(Object[].class);
        Root<Verification> root = q.from(Verification.class);
        Join<Verification, Organization> provider = root.join("provider");
        q.select(cb.array(root.get("expirationDate"), root.get("id"), provider.get("maxProcessTime"), provider.get("email")));
        Predicate statusPredicate = cb.or(cb.equal(root.get("status"), Status.valueOf("SENT")), cb.equal(root.get("status"), Status.valueOf("ACCEPTED")));
        q.where(statusPredicate);

        return em.createQuery(q).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getProcessTimeCalibrator() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = cb.createQuery(Object[].class);
        Root<Verification> root = q.from(Verification.class);
        Join<Verification, Organization> provider = root.join("calibrator");
        q.select(cb.array(root.get("expirationDate"), root.get("id"), provider.get("maxProcessTime"), provider.get("email")));
        Predicate statusPredicate = cb.or(cb.equal(root.get("status"), Status.valueOf("IN_PROGRESS")), cb.equal(root.get("status"), Status.valueOf("TEST_PLACE_DETERMINED")),
                cb.equal(root.get("status"), Status.valueOf("SENT_TO_TEST_DEVICE")), cb.equal(root.get("status"), Status.valueOf("TEST_COMPLETED")));
        q.where(statusPredicate);

        return em.createQuery(q).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getProcessTimeVerificator() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = cb.createQuery(Object[].class);
        Root<Verification> root = q.from(Verification.class);
        Join<Verification, Organization> provider = root.join("stateVerificator");
        q.select(cb.array(root.get("expirationDate"), root.get("id"), provider.get("maxProcessTime"), provider.get("email")));
        Predicate statusPredicate = cb.or(cb.equal(root.get("status"), Status.valueOf("SENT_TO_VERIFICATOR")), cb.equal(root.get("status"), Status.valueOf("TEST_OK")), cb.equal(root.get("status"), Status.valueOf("TEST_NOK")));
        q.where(statusPredicate);

        return em.createQuery(q).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.sql.Date getNewVerificationEarliestDateByProvider(Organization organization) {
        return verificationRepository.getEarliestDateOfAllAcceptedOrSentVerificationsByProvider(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public java.sql.Date getArchivalVerificationEarliestDateByProvider(Organization organization) {
        return verificationRepository.getEarliestDateOfArchivalVerificationsByProvider(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public java.sql.Date getNewVerificationEarliestDateByCalibrator(Organization organization) {
        return verificationRepository.getEarliestDateOfAllNewVerificationsByCalibrator(organization);
    }

    @Override
    public java.sql.Date getEarliestDateOfDigitalVerificationProtocolsByCalibrator(Organization organization) {
        return verificationRepository.getEarliestDateOfDigitalVerificationProtocolsByCalibrator(organization);
    }

    @Override
    public java.sql.Date getEarliestDateOfRejectingVerificationProtocolsByCalibrator(Organization organization) {
        return verificationRepository.getEarliestDateOfRejectingVerificationProtocolsByCalibrator(organization);
    }

    @Override
    public java.sql.Date getEarliestDateOfDigitalVerificationProtocolsByVerificator(Organization organization) {
        return verificationRepository.getEarliestDateOfDigitalVerificationProtocolsByVerificator(organization);
    }

    @Override
    public java.sql.Timestamp getEarliestDateOfSentToVerificator(Organization organization) {
        return verificationRepository.getEarliestDateOfSentToVerificator(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public java.sql.Date getArchivalVerificationEarliestDateByCalibrator(Organization organization) {
        return verificationRepository.getEarliestDateOfArchivalVerificationsByCalibrator(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public java.sql.Date getEarliestPlanningTaskDate(Organization organization) {
        return verificationRepository.getEarliestPlanningTaskDate(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public java.sql.Date getEarliestInitialDateForPlanningTask(Organization organization) {
        return verificationRepository.getEarliestInitialDateForPlanningTask(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Verification> getVerificationsByTaskID(Long taskID, Pageable pageable) {
        return verificationRepository.findByTaskId(taskID, pageable);
    }

    @Transactional(readOnly = true)
    public List<Verification> getVerificationsByTaskID(Long taskID) {
        return verificationRepository.findByTaskId(taskID);
    }

    @Transactional(readOnly = true)
    public List<Verification> getTaskGroupVerifications(Verification verification, boolean all) {
        List<Verification> verifications;
        VerificationGroup group = verification.getGroup();
        if (all && group != null) {
            Long groupId = group.getId();
            Long taskId = verification.getTask().getId();
            verifications = verificationRepository.findByTaskIdAndGroupId(taskId, groupId);
        } else {
            verifications = Arrays.asList(verification);
        }
        return verifications;
    }

    @Transactional(readOnly = true)
    public boolean hasVerificationGroup(String verificationId) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification == null) {
            logger.error("verification wasn't found");
            throw new IllegalArgumentException();
        }
        VerificationGroup group = verification.getGroup();
        return group != null ? true : false;
    }


    @Override
    @Transactional
    public void removeVerificationFromTask(String verificationId) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification == null) {
            logger.error("verification wasn't found");
            throw new IllegalArgumentException();
        }
        verification.setStatus(Status.IN_PROGRESS);
        verification.setTaskStatus(Status.PLANNING_TASK);
        verification.setTask(null);
        verification.setQueue(0);
        verificationRepository.save(verification);
    }

    /**
     * update counter info
     *
     * @param verificationId
     * @param deviceName
     * @param dismantled
     * @param sealPresence
     * @param dateOfDismantled
     * @param dateOfMounted
     * @param numberCounter
     * @param releaseYear
     * @param symbol
     * @param standardSize
     * @param comment
     * @param deviceId
     */
    @Override
    @Transactional
    public void editCounter(String verificationId, String deviceName, Boolean dismantled, Boolean sealPresence,
                            Long dateOfDismantled, Long dateOfMounted, String numberCounter, String releaseYear,
                            String accumulatedVolume, String symbol, String standardSize, String comment, Long deviceId,
                            Boolean verificationWithDismantle, Device.DeviceType deviceType) {
        Verification verification = verificationRepository.findOne(verificationId);

        verification.setCounterStatus(dismantled);
        verification.setSealPresence(sealPresence);
        verification.setVerificationWithDismantle(verificationWithDismantle);
        verification.setComment(comment);

        Counter counter = verification.getCounter();
        List<CounterType> counterTypes = counterTypeRepository.findBySymbolAndStandardSize(symbol, standardSize);
        CounterType counterType = getCounterTypeByDeviceType(counterTypes, deviceType);
        if (counter != null) {
            counter.setDateOfDismantledLong(dateOfDismantled);
            counter.setDateOfMountedLong(dateOfMounted);
            counter.setNumberCounter(numberCounter);
            counter.setReleaseYear(releaseYear);
            counter.setAccumulatedVolume(accumulatedVolume);
            if (counterType != null) {
                counter.setCounterType(counterType);
            }
            counterRepository.save(counter);
        } else {
            counter = new Counter(releaseYear, dateOfDismantled, dateOfMounted, numberCounter, counterType);
            verification.setCounter(counter);
        }
        verificationRepository.save(verification);

    }

    @Override
    @Transactional
    public void editCounter(String verificationId, String deviceName, Boolean dismantled, Boolean sealPresence,
                            Date dateOfDismantled, Date dateOfMounted, String numberCounter, String releaseYear,
                            String accumulatedVolume, String symbol, String standardSize, String comment, Long deviceId,
                            Boolean verificationWithDismantle, Device.DeviceType deviceType) {
        Verification verification = verificationRepository.findOne(verificationId);

        verification.setCounterStatus(dismantled);
        verification.setSealPresence(sealPresence);
        verification.setVerificationWithDismantle(verificationWithDismantle);
        verification.setComment(comment);

        Counter counter = verification.getCounter();
        List<CounterType> counterTypes = counterTypeRepository.findBySymbolAndStandardSize(symbol, standardSize);
        CounterType counterType = getCounterTypeByDeviceType(counterTypes, deviceType);
        if (counter != null) {
            counter.setDateOfDismantled(dateOfDismantled);
            counter.setDateOfMounted(dateOfMounted);
            counter.setNumberCounter(numberCounter);
            counter.setReleaseYear(releaseYear);
            counter.setAccumulatedVolume(accumulatedVolume);
            if (counterType != null) {
                counter.setCounterType(counterType);
            }
            counterRepository.save(counter);
        } else {
            counter = new Counter(releaseYear, dateOfDismantled, dateOfMounted, numberCounter, counterType);
            verification.setCounter(counter);
        }
        verificationRepository.save(verification);

    }

    /**
     * update additional info
     *
     * @param entrance
     * @param doorCode
     * @param floor
     * @param dateOfVerif
     * @param timeFrom
     * @param timeTo
     * @param serviceability
     * @param noWaterToDate
     * @param notes
     * @param verificationId
     */
    @Override
    @Transactional
    public void editAddInfo(int entrance, int doorCode, int floor, Long dateOfVerif, String timeFrom, String timeTo,
                            Boolean serviceability, Long noWaterToDate, String notes, String verificationId) {
        Verification verification = verificationRepository.findOne(verificationId);

        AdditionalInfo info = verification.getInfo();

        if (info != null) {
            info.setEntrance(entrance);
            info.setDoorCode(doorCode);
            info.setFloor(floor);
            info.setDateOfVerifLong(dateOfVerif);
            info.setServiceability(serviceability);
            info.setNoWaterToDateLong(noWaterToDate);
            info.setNotes(notes);
            if (dateOfVerif != null) {
                info.setTimeFrom(timeFrom);
                info.setTimeTo(timeTo);
            } else {
                info.setTimeFrom(null);
                info.setTimeTo(null);
            }

            additionalInfoRepository.save(info);
        } else {
            AdditionalInfo newInfo = new AdditionalInfo(entrance, doorCode, floor, dateOfVerif, serviceability, noWaterToDate,
                    notes, timeFrom, timeTo);
            verification.setInfo(newInfo);
            verificationRepository.save(verification);
        }
    }

    @Override
    @Transactional
    public void editAddInfo(int entrance, int doorCode, int floor, Date dateOfVerif, String timeFrom, String timeTo,
                            Boolean serviceability, Date noWaterToDate, String notes, String verificationId) {
        Verification verification = verificationRepository.findOne(verificationId);

        AdditionalInfo info = verification.getInfo();

        if (info != null) {
            info.setEntrance(entrance);
            info.setDoorCode(doorCode);
            info.setFloor(floor);
            info.setDateOfVerif(dateOfVerif);
            info.setServiceability(serviceability);
            info.setNoWaterToDate(noWaterToDate);
            info.setNotes(notes);
            if (dateOfVerif != null) {
                info.setTimeFrom(timeFrom);
                info.setTimeTo(timeTo);
            } else {
                info.setTimeFrom(null);
                info.setTimeTo(null);
            }

            additionalInfoRepository.save(info);
        } else {
            AdditionalInfo newInfo = new AdditionalInfo(entrance, doorCode, floor, dateOfVerif, serviceability, noWaterToDate,
                    notes, timeFrom, timeTo);
            verification.setInfo(newInfo);
            verificationRepository.save(verification);
        }
    }

    /**
     * update client Info
     *
     * @param verificationId
     * @param clientData
     */
    @Override
    @Transactional
    public void editClientInfo(String verificationId, ClientData clientData) {
        Verification verification = verificationRepository.findOne(verificationId);
        verification.setClientData(clientData);
        verificationRepository.save(verification);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findAllSymbols(Long deviceId) {
        return counterTypeRepository.findByDeviceId(deviceId)
                .stream()
                .map(CounterType::getSymbol)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findStandardSizesBySymbolAndDeviceId(String symbol, Long deviceId) {
        return counterTypeRepository.findBySymbolAndDeviceId(symbol, deviceId)
                .stream()
                .map(CounterType::getStandardSize)
                .collect(Collectors.toSet());
    }

    /**
     * for finding symbols by deviceType the organization work with
     *
     * @param deviceType
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Set<String> findSymbolsByDeviceType(String deviceType) {
        return counterTypeRepository.findSymbolsByDeviceType(deviceType);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findStandardSizesBySymbolAndDeviceType(String symbol, String deviceType) {
        return counterTypeRepository.findStandardSizesBySymbolAndDeviceType(symbol, deviceType);
    }

    @Override
    @Transactional
    public CounterType findOneBySymbolAndStandardSizeAndDeviceId(String symbol, String standardSize, Long deviceId) {
        return counterTypeRepository.findOneBySymbolAndStandardSizeAndDeviceId(symbol, standardSize, deviceId);
    }

    //todo This method should be replaced by method "saveVerificationCustom"
    @Override
    @Deprecated
    public String getNewVerificationDailyIdByDeviceType(Date date, Device.DeviceType deviceType) {
        String datePart = (new SimpleDateFormat(Constants.DAY_MONTH_YEAR).format(date)) + deviceType.getId();
        String countPart = String.format("%04d", verificationRepository.getCountOfAllVerificationsCreatedWithDeviceTypeToday(date,
                deviceType) + 1);
        return datePart + countPart;
    }

    @Override
    @Transactional
    public synchronized List<String> saveVerificationCustom(Verification verification, Byte quantity, Device.DeviceType deviceType, Long groupId) {
        List<String> verificationIds = new ArrayList<>();
        Set<Verification> verifications = new HashSet<>();
        String id;
        String datePart;

        if (verification.getStatus().equals(Status.CREATED_BY_CALIBRATOR)) {
            verification.setCreatedByCalibrator(true);
            int deviceByBackFlow = deviceType.getId() + Constants.BACKFLOW_ID_BBI;
            datePart = (new SimpleDateFormat(Constants.DAY_MONTH_YEAR).format(verification.getInitialDate())) + deviceByBackFlow;
        } else if (verification.getStatus().equals(Status.CREATED_FOR_PROVIDER)) {
            verification.setCreatedByCalibrator(true);
            int deviceByBackFlow = deviceType.getId() + Constants.BACKFLOW_ID_MANUAL;
            datePart = (new SimpleDateFormat(Constants.DAY_MONTH_YEAR).format(verification.getInitialDate())) + deviceByBackFlow;
        } else {
            datePart = (new SimpleDateFormat(Constants.DAY_MONTH_YEAR).format(verification.getInitialDate())) + deviceType.getId();
        }

        long count = verificationRepository.countByIdStartingWith(datePart);

        VerificationGroup group;
        if (groupId == null) {
            group = new VerificationGroup();
            groupRepository.save(group);
        } else {
            group = groupRepository.findOne(groupId);
        }

        for (byte i = 0; i < quantity; i++) {
            id = datePart + String.format("%05d", count += 1);
            verification.setId(id);
            verification.setGroup(group);
            verificationRepository.save(verification);
            verificationIds.add(id);
            verifications.add(verification);
        }
        return verificationIds;
    }

    @Override
    public Long findCountOfNewNotStandardVerificationsByCalibratorId(Long calibratorId) {
        return verificationRepository.countByCalibratorIdAndStatusAndReadStatus(
                calibratorId, Status.CREATED_BY_CALIBRATOR, Verification.ReadStatus.UNREAD);
    }

    @Override
    public Long findCountOfPlanedTasksByCalibratorEmployeeUsername(User calibratorEmployee) {
        if (calibratorEmployee.getUserRoles().contains(UserRole.CALIBRATOR_ADMIN)) {
            return verificationRepository.countByTaskStatusAndCalibratorIdAndProviderEmployeeUsernameIsNotNullAndCounterStatusIsFalseAndStatus(Status.PLANNING_TASK,
                    calibratorEmployee.getOrganization().getId(), Status.IN_PROGRESS);
        }
        return verificationRepository.countByTaskStatusAndCalibratorEmployeeUsernameAndProviderEmployeeUsernameIsNotNullAndCounterStatusIsFalseAndStatus(Status.PLANNING_TASK,
                calibratorEmployee.getUsername(), Status.IN_PROGRESS);
    }

    @Override
    public Long findCountOfNewVerificationsForProviderByCalibratorId(Long calibratorId) {
        return verificationRepository.countByCalibratorIdAndStatusAndReadStatus(
                calibratorId, Status.CREATED_FOR_PROVIDER, Verification.ReadStatus.UNREAD);
    }

    @Override
    public Long findCountOfNewVerificationsForProviderByCalibratorEmployeeUsername(String calibratorEmployeeUsername) {
        return verificationRepository.countByCalibratorEmployeeUsernameAndStatusAndReadStatus(
                calibratorEmployeeUsername, Status.CREATED_FOR_PROVIDER, Verification.ReadStatus.UNREAD);
    }

    @Override
    public Long findCountOfNotStandardNewVerificationsByProviderId(Long providerId) {
        return verificationRepository.countByProviderIdAndStatusAndReadStatus(providerId,
                Status.SENT_TO_PROVIDER, Verification.ReadStatus.UNREAD);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Verification> findPageOfVerificationsByCalibratorEmployeeAndStatus(User calibratorEmployee,
                                                                                   int pageNumber, int itemsPerPage,
                                                                                   Status status, String sortCriteria, String sortOrder) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> cq = cb.createQuery(Verification.class);
        Root<Verification> verifications = cq.from(Verification.class);
        if ((sortCriteria != null) && (sortOrder != null)) {
            cq.orderBy(SortCriteriaVerification.valueOf(sortCriteria.toUpperCase()).getSortOrder(verifications, cb, sortOrder));
        } else {
            cq.orderBy(cb.desc(verifications.get("initialDate")));
        }
        Predicate calibratorEmployeePredicate = null;
        if (calibratorEmployee.getUserRoles().contains(UserRole.CALIBRATOR_EMPLOYEE)) {
            calibratorEmployeePredicate = cb.equal(verifications.get("calibratorEmployee"), calibratorEmployee);
        } else {
            calibratorEmployeePredicate = cb.equal(verifications.get("calibrator"), calibratorEmployee.getOrganization());
        }
        Predicate statusPredicate = cb.or(cb.equal(verifications.get("status"), status), cb.equal(verifications.get("status"), Status.SENT_TO_PROVIDER));
        Predicate verificationPredicate;
        if (status.equals(Status.CREATED_FOR_PROVIDER)) {
            verificationPredicate = cb.isNull(verifications.get("verificationTime"));
        } else {
            verificationPredicate = cb.isNotNull(verifications.get("verificationTime"));
        }
        cq.where(calibratorEmployeePredicate, statusPredicate, verificationPredicate);

        TypedQuery<Verification> typedQuery = em.createQuery(cq);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);

        return typedQuery.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByCalibratorEmployeeUsernameAndStatus(User calibratorEmployee, Status status) {
        return verificationRepository.countByCalibratorEmployeeUsernameAndStatus(calibratorEmployee.getUsername(),
                status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Verification> findPageOfVerificationsByProviderIdAndStatus(Organization provider, int pageNumber,
                                                                           int itemsPerPage, Status status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> cq = cb.createQuery(Verification.class);
        Root<Verification> verifications = cq.from(Verification.class);
        cq.where(cb.and(cb.equal(verifications.get("status"), status),
                cb.equal(verifications.get("provider"), provider)));

        TypedQuery<Verification> typedQuery = em.createQuery(cq);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);

        return typedQuery.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByProviderAndStatus(Organization provider, Status status) {
        return verificationRepository.countByProviderAndStatus(provider, status);
    }

    @Override
    @Transactional
    public void returnVerificationToCalibratorFromProvider(String verificationId, String rejectMessage) {

        Verification verification = verificationRepository.findOne(verificationId);
        verification.setRejectedMessage(rejectMessage);
        if (verification.getProviderFromBBI() != null) {
            verification.setStatus(Status.CREATED_BY_CALIBRATOR);
            verification.setProvider(null);
        } else {
            verification.setStatus(Status.CREATED_FOR_PROVIDER);
        }
        verificationRepository.save(verification);

    }

    /**
     * for finding symbol by standardSize and deviceType
     *
     * @param standardSize
     * @param deviceType
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> findSortedSymbolsByStandardSizeAndDeviceType(String standardSize, String deviceType) {
        Collator collator = Collator.getInstance(new Locale("uk", "UA"));
        List<String> listOfSymbols = counterTypeRepository.findSymbolsByStandardSizeAndDeviceType(standardSize, deviceType);
        Collections.sort(listOfSymbols, collator);
        return listOfSymbols;
    }

    /**
     * find all standardSizes
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Set<String> findAllStandardSizes() {
        return counterTypeRepository.findAllStandardSizes();
    }

    private CounterType getCounterTypeByDeviceType(List<CounterType> counterTypes, Device.DeviceType deviceType) {
        for (CounterType counterType : counterTypes) {
            if (counterType.getDevice().getDeviceType().equals(deviceType)) return counterType;
        }
        return null;
    }

    /**
     * find all by taskId
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<Verification> findAllByTaskId(String taskId) {
        return verificationRepository.findByTaskId(Long.valueOf(taskId));
    }
}
