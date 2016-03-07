package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.repository.CalibrationTestRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.calibrator.CalibratorDigitalProtocolsService;

import com.softserve.edu.service.utils.DigitalProtocolQueryConstructorCalibrator;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalibratorDigitalProtocolsServiceImpl implements CalibratorDigitalProtocolsService {

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    CalibrationTestRepository calibrationTestRepository;

    @PersistenceContext
    private EntityManager em;

    public Long countByCalibratorEmployeeUsernameAndStatus(User calibratorEmployee, Status status) {
        return verificationRepository.countByCalibratorEmployeeUsernameAndStatus(calibratorEmployee.getUsername(), status);
    }

    /**
     * Find and return from database Verifications by user and status
     * is used for table with protocols
     *
     * @param calibratorEmployee - user
     * @param pageNumber
     * @param itemsPerPage
     * @param status
     * @return verifications
     */
    @Transactional(readOnly = true)
    public List<Verification> findPageOfVerificationsByCalibratorIdAndStatus(
            User calibratorEmployee, int pageNumber, int itemsPerPage, Status status) {

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
    public ListToPageTransformer<Verification> findPageOfVerificationsByCalibratorIdAndStatus(Long verificatorId, int pageNumber, int itemsPerPage, String startDateToSearch, String endDateToSearch,
                                                                                              String idToSearch, String status, String nameProvider, String nameCalibrator, String numberOfCounter,
                                                                                              String numberOfProtocol, String moduleNumber, String sortCriteria,
                                                                                              String sortOrder, User verificatorEmployee) {

        CriteriaQuery<Verification> criteriaQuery = DigitalProtocolQueryConstructorCalibrator.buildSearchQuery(verificatorId, startDateToSearch, endDateToSearch, idToSearch, status, verificatorEmployee,
                nameProvider, nameCalibrator, numberOfCounter, numberOfProtocol, moduleNumber, sortCriteria, sortOrder, em);

        Long count = em.createQuery(DigitalProtocolQueryConstructorCalibrator.buildCountQuery(verificatorId, startDateToSearch, endDateToSearch, idToSearch, status, verificatorEmployee,
                nameProvider, nameCalibrator, numberOfCounter,
                numberOfProtocol, moduleNumber, em)).getSingleResult();

        TypedQuery<Verification> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Verification> verificationList = typedQuery.getResultList();

        ListToPageTransformer<Verification> result = new ListToPageTransformer<Verification>();
        result.setContent(verificationList);
        result.setTotalItems(count);
        return result;
    }
}
