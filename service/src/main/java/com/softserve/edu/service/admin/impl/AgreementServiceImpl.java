package com.softserve.edu.service.admin.impl;

import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Agreement;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.repository.AgreementRepository;
import com.softserve.edu.service.admin.AgreementService;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.utils.ListToPageTransformer;
import com.softserve.edu.specification.AgreementSpecificationBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service for agreements between organizations
 */
@Service
public class AgreementServiceImpl implements AgreementService {

    private final Logger logger = Logger.getLogger(AgreementServiceImpl.class);

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AgreementRepository agreementRepository;

    @Override
    @Transactional
    public Agreement add(Agreement agreement) {
        if (agreement == null) {
            throw new NullPointerException("Agreement is null");
        }

        return agreementRepository.save(agreement);
    }

    @Override
    @Transactional
    public Agreement add(Organization customer, Organization executor, String number, int deviceCount, Date date, Device.DeviceType deviceType) {
        Agreement agreement = new Agreement(customer, executor, number, deviceCount, date, deviceType);
        return agreementRepository.save(agreement);
    }

    @Override
    @Transactional
    public Agreement add(Long customerId, Long executorId, String number, int deviceCount, Date date, Device.DeviceType deviceType) {
        Organization customer = organizationService.getOrganizationById(customerId);
        Organization executor = organizationService.getOrganizationById(executorId);
        if (customer == null || executor == null) {
            logger.error("Organization not found");
            throw new NullPointerException("Organization not found");
        }
        Agreement agreement = new Agreement(customer, executor, number, deviceCount, date, deviceType);
        return agreementRepository.save(agreement);
    }

    @Override
    @Transactional(readOnly = true)
    public Agreement findAgreementById(Long id) {
        return agreementRepository.findOne(id);
    }

    @Override
    public Set<Agreement> findAll() {
        return agreementRepository.findAll();
    }

    @Override
    @Transactional
    public void update(Long agreementId, Long customerId, Long executorId, String number, int deviceCount, Date date, Device.DeviceType deviceType) {
        Agreement agreement = agreementRepository.findOne(agreementId);
        Organization customer = organizationService.getOrganizationById(customerId);
        Organization executor = organizationService.getOrganizationById(executorId);

        agreement.setCustomer(customer);
        agreement.setExecutor(executor);
        agreement.setNumber(number);
        agreement.setDate(date);
        agreement.setDeviceCount(deviceCount);
        agreement.setDeviceType(deviceType);

        agreementRepository.save(agreement);
    }

    /**
     * Return list of agreements on specific page sorted and filtered by specific field
     * @param pageNumber zero-based page index.
     * @param itemsPerPage the size of the page to be returned.
     * @param sortCriteria sorting criterion
     * @param sortOrder sorting order
     * @param searchKeys map of values to search
     * @return list of agreements
     */
    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<Agreement> getCategoryDevicesBySearchAndPagination(int pageNumber, int itemsPerPage,
                                                                                    Map<String, String> searchKeys,
                                                                                    String sortCriteria, String sortOrder) {

        AgreementSpecificationBuilder specificationBuilder = new AgreementSpecificationBuilder(searchKeys);
        Pageable pageSpec = specificationBuilder.constructPageSpecification(pageNumber - 1, itemsPerPage, sortCriteria, sortOrder);
        Specification<Agreement> searchSpec = specificationBuilder.buildPredicate();

        Page<Agreement> agreementPage = agreementRepository.findAll(searchSpec, pageSpec);
        List<Agreement> agreements = agreementPage.getContent();

        ListToPageTransformer<Agreement> result = new ListToPageTransformer<>();
        result.setContent(agreements);
        result.setTotalItems((long) agreements.size());
        return result;
    }

    /**
     * Disable specific agreement
     * @param agreementId id of agreement to disable
     */
    @Override
    @Transactional
    public void disableAgreement(Long agreementId) {
        Agreement agreement = agreementRepository.findOne(agreementId);
        agreement.setIsAvailable(false);
        agreementRepository.save(agreement);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Agreement> findByCustomerIdAndDeviceType(Long customerId, Device.DeviceType deviceType) {
        return agreementRepository.findByCustomerIdAndDeviceType(customerId, deviceType);
    }

    @Override
    @Transactional(readOnly = true)
    public java.sql.Date getEarliestDateAvailableAgreement() {
        return agreementRepository.findEarliestDateAvalibleAgreement();
    }
}
