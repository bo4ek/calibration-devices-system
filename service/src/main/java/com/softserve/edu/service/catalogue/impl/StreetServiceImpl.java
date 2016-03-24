package com.softserve.edu.service.catalogue.impl;

import com.softserve.edu.entity.catalogue.Street;
import com.softserve.edu.repository.catalogue.StreetRepository;
import com.softserve.edu.service.catalogue.StreetService;
import com.softserve.edu.service.utils.ArchivalEmployeeQueryConstructorAdmin;
import com.softserve.edu.service.utils.ListToPageTransformer;
import com.softserve.edu.service.utils.StreetsQueryConstructor;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StreetServiceImpl implements StreetService {

    @Autowired
    private StreetRepository streetRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Street> getStreetsCorrespondingLocality(Long localityId) {
        return streetRepository.findByLocalityId(localityId);
    }

    @Override
    public Street findByDesignation(String designation) {
        return streetRepository.findByDesignation(designation);
    }

    @Override
    public Street findByLocalityIdAndDesignation(Long localityId, String designation) {
        return streetRepository.findByLocalityIdAndDesignation(localityId, designation);
    }

    @Override
    public Street findStreetById(Long id) {
        return streetRepository.findOne(id);
    }

    @Override
    public Iterable<Street> findAll() {
        return streetRepository.findAll();
    }

    @Override
    @Transactional
    public Street findByIdAndLocalityId(Long id, Long localityId) {
        return streetRepository.findByIdAndLocalityId(id, localityId);
    }

    @Override
    @Transactional
    public ListToPageTransformer<Street> findPageOfAllStreets(int pageNumber, int itemsPerPage, String city,
                           String district, String region, String streetName, String sortCriteria, String sortOrder) {
        CriteriaQuery<Street> criteriaQuery = StreetsQueryConstructor.buildSearchQuery(city, district, region,
                streetName, sortCriteria, sortOrder, em);

        Long count = em.createQuery(StreetsQueryConstructor.buildCountQuery(city, district, region,
                streetName, em)).getSingleResult();

        TypedQuery<Street> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<Street> employeeList = typedQuery.getResultList();
        ListToPageTransformer<Street> result = new ListToPageTransformer<>();
        result.setContent(employeeList);
        result.setTotalItems(count);

        return result;
    }
}
