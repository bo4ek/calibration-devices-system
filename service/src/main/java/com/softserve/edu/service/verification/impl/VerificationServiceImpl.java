package com.softserve.edu.service.verification.impl;

import com.softserve.edu.common.Constants;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.ClientData;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VerificationServiceImpl implements VerificationService {

    private Logger logger = Logger.getLogger(VerificationServiceImpl.class);

    @Autowired
    private CalibrationPlanningTaskRepository taskRepository;

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
        return verificationRepository.countByCalibratorIdAndStatusAndReadStatus(calibratorId, Status.IN_PROGRESS,
                Verification.ReadStatus.UNREAD);
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
                                                                                          String streetToSearch, String region, String district, String locality, String status, String employeeName, String sortCriteria, String sortOrder, User providerEmployee) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorProvider.buildSearchQuery(organizationId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, employeeName, sortCriteria, sortOrder, providerEmployee, em);

        Long count = em.createQuery(ArchivalVerificationsQueryConstructorProvider.buildCountQuery(organizationId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, employeeName, providerEmployee, em)).getSingleResult();

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
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByProviderIdOnMainPanel(Long organizationId, int pageNumber, int itemsPerPage, String initialDateToSearch, String idToSearch, String fullNameToSearch,
                                                                                                     String streetToSearch, String region, String district, String locality, String status, String employeeName, User providerEmployee) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorProvider.buildSearchQuery(organizationId, initialDateToSearch, null, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, "SENT", employeeName, null, null, providerEmployee, em);

        Long count = em.createQuery(ArchivalVerificationsQueryConstructorProvider.buildCountQuery(organizationId, initialDateToSearch, null, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, "SENT", employeeName, providerEmployee, em)).getSingleResult();

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
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByVerificatorIdOnMainPanel(Long organizationId, int pageNumber, int itemsPerPage, String initialDateToSearch, String idToSearch, String fullNameToSearch,
                                                                                                        String streetToSearch, String region, String district, String locality, String status, String employeeName, User stateVerificatorEmployee) {
        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorVerificator.buildSearchQuery(organizationId, initialDateToSearch, idToSearch, fullNameToSearch, streetToSearch, status, employeeName, null, null, null, em);

        Long count = em.createQuery(ArchivalVerificationsQueryConstructorVerificator.buildCountQuery(organizationId, initialDateToSearch, idToSearch, fullNameToSearch, streetToSearch, status, employeeName, null, em)).getSingleResult();

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
                                                                                                      String streetToSearch, String region, String district, String locality, String status, String employeeName, String standardSize, String symbol, String nameProvider, String realiseYear, String dismantled, String building, String sortCriteria, String sortOrder, User calibratorEmployee, ArrayList<Map<String, Object>> globalSearchParams) {


        CriteriaQuery<Verification> criteriaQuery = NewVerificationsQueryConstructorCalibrator.buildSearchQuery(calibratorId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled, building, sortCriteria, sortOrder, employeeName, em, globalSearchParams);


        Long count = em.createQuery(NewVerificationsQueryConstructorCalibrator.buildCountQuery(calibratorId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, region, district, locality, status, calibratorEmployee, standardSize, symbol, nameProvider, realiseYear, dismantled, building, employeeName, em)).getSingleResult();

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
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByCalibratorId(Long organizationId, int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch, String idToSearch, String fullNameToSearch,
                                                                                            String streetToSearch, String status, String employeeName, Long protocolId, String protocolStatus, Long measurementDeviceId, String measurementDeviceType, String sortCriteria, String sortOrder, User calibratorEmployee) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorCalibrator.buildSearchQuery(organizationId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, status, employeeName, protocolId, protocolStatus, measurementDeviceId, measurementDeviceType, sortCriteria, sortOrder, calibratorEmployee, em);
        Long count = em.createQuery(ArchivalVerificationsQueryConstructorCalibrator.buildCountQuery(organizationId, startDateToSearch, endDateToSearch, idToSearch, fullNameToSearch, streetToSearch, status, employeeName, protocolId, protocolStatus, measurementDeviceId, measurementDeviceType, calibratorEmployee, em)).getSingleResult();

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
    public ListToPageTransformer<Verification> findPageOfVerificationsByVerificatorIdAndCriteriaSearch(Long verificatorId, int pageNumber, int itemsPerPage, String dateToSearch, String idToSearch, String fullNameToSearch,
                                                                                                       String streetToSearch, String status, String employeeName, String nameProvider, String nameCalibrator, String lastName, String firstName, String middleName, String district, String building, String flat, String sortCriteria, String sortOrder, User verificatorEmployee) {

        CriteriaQuery<Verification> criteriaQuery = NewVerificationsQueryConstructorVerificator.buildSearchQuery(verificatorId, dateToSearch, idToSearch, fullNameToSearch, streetToSearch, status, verificatorEmployee, nameProvider, nameCalibrator, lastName, firstName, middleName, district, building, flat, sortCriteria, sortOrder, employeeName, em);

        Long count = em.createQuery(NewVerificationsQueryConstructorVerificator.buildCountQuery(verificatorId, dateToSearch, idToSearch, fullNameToSearch, streetToSearch, status, verificatorEmployee, nameProvider, nameCalibrator, lastName, firstName, middleName, district, building, flat, employeeName, em)).getSingleResult();

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
    public ListToPageTransformer<Verification> findPageOfArchiveVerificationsByVerificatorId(Long organizationId, int pageNumber, int itemsPerPage, String dateToSearch, String idToSearch, String fullNameToSearch,
                                                                                             String streetToSearch, String status, String employeeName, String sortCriteria, String sortOrder, User verificatorEmployee) {

        CriteriaQuery<Verification> criteriaQuery = ArchivalVerificationsQueryConstructorVerificator.buildSearchQuery(organizationId, dateToSearch, idToSearch, fullNameToSearch, streetToSearch, status, employeeName, sortCriteria, sortOrder, verificatorEmployee, em);

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
    @Transactional
    public int findCountOfAllSentVerifications(Organization organization) {
        return verificationRepository.getCountOfAllSentVerifications(organization);
    }

    @Override
    @Transactional
    public int findCountOfAllAcceptedVerification(Organization organization) {
        return verificationRepository.getCountOfAllAcceptedVerifications(organization);
    }

    @Override
    @Transactional
    public int findCountOfAllCalibratorVerificationWithoutEmployee(Organization organization) {
        return verificationRepository.findCountOfAllCalibratorVerificationWithoutEmployee(organization);
    }

    @Override
    @Transactional
    public int findCountOfAllCalibratorVerificationWithEmployee(Organization organization) {
        return verificationRepository.findCountOfAllCalibratorVerificationWithEmployee(organization);
    }

    @Override
    @Transactional
    public int findCountOfAllVerificatorVerificationWithoutEmployee(Organization organization) {
        return verificationRepository.findCountOfAllVerificatorVerificationWithoutEmployee(organization);
    }

    @Override
    @Transactional
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
    @Transactional
    public Page<Verification> getVerificationsByTaskID(Long taskID, Pageable pageable) {
        return verificationRepository.findByTask_Id(taskID, pageable);
    }

    @Transactional
    public Verification[] getVerificationsByTaskID(Long taskID) {
        return verificationRepository.findByTask_Id(taskID);
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
                            String symbol, String standardSize, String comment, Long deviceId) {
        Verification verification = verificationRepository.findOne(verificationId);

        verification.setCounterStatus(dismantled);
        verification.setSealPresence(sealPresence);
        verification.setComment(comment);

        Counter counter = verification.getCounter();
        CounterType counterType = counterTypeRepository.findOneBySymbolAndStandardSize(symbol, standardSize);

        if (counter != null) {
            counter.setDateOfDismantled(dateOfDismantled);
            counter.setDateOfMounted(dateOfMounted);
            counter.setNumberCounter(numberCounter);
            counter.setReleaseYear(releaseYear);

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
            info.setDateOfVerif(dateOfVerif);
            info.setServiceability(serviceability);
            info.setNoWaterToDate(noWaterToDate);
            info.setNotes(notes);
            info.setTimeFrom(timeFrom);
            info.setTimeTo(timeTo);

            additionalInfoRepository.save(info);
        } else {
            info = new AdditionalInfo(entrance, doorCode, floor, dateOfVerif, serviceability, noWaterToDate,
                    notes, timeFrom, timeTo);
            verification.setInfo(info);
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
    @Transactional
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
    @Transactional
    public Set<String> findSymbolsByDeviceType(String deviceType) {
        return counterTypeRepository.findSymbolsByDeviceType(deviceType);
    }

    @Override
    @Transactional
    public Set<String> findStandardSizesBySymbolAndDeviceType(String symbol, String deviceType) {
        return counterTypeRepository.findStandardSizesBySymbolAndDeviceType(symbol, deviceType);
    }

    @Override
    @Transactional
    public CounterType findOneBySymbolAndStandardSizeAndDeviceId(String symbol, String standardSize, Long deviceId) {
        return counterTypeRepository.findOneBySymbolAndStandardSizeAndDeviceId(symbol, standardSize, deviceId);
    }

    @Override
    public String getNewVerificationDailyIdByDeviceType(Date date, Device.DeviceType deviceType) {
        String datePart = (new SimpleDateFormat(Constants.DAY_MONTH_YEAR).format(date)) + deviceType.getId();
        String countPart = String.format("%04d", verificationRepository.getCountOfAllVerificationsCreatedWithDeviceTypeToday(date,
                deviceType) + 1);
        return datePart + countPart;
    }

    @Override
    public Long findCountOfNewNotStandardVerificationsByCalibratorId(Long calibratorId) {
        return verificationRepository.countByCalibratorIdAndStatusAndReadStatus(
                calibratorId, Status.CREATED_BY_CALIBRATOR, Verification.ReadStatus.UNREAD);
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
                                                                                   Status status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Verification> cq = cb.createQuery(Verification.class);
        Root<Verification> verifications = cq.from(Verification.class);

        cq.where(cb.and(cb.equal(verifications.get("calibratorEmployee"), calibratorEmployee),
                cb.equal(verifications.get("status"), status)));

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
        verification.setStatus(Status.CREATED_BY_CALIBRATOR);
        verification.setProvider(null);
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
    @Transactional
    public Set<String> findSymbolByStandardSizeAndDeviceType(String standardSize, String deviceType) {
        return counterTypeRepository.findSymbolByStandardSizeAndDeviceType(standardSize, deviceType);
    }

    /**
     * find all standardSizes
     *
     * @return
     */
    @Override
    @Transactional
    public Set<String> findAllStandardSizes() {
        return counterTypeRepository.findAllStandardSizes();
    }
}
