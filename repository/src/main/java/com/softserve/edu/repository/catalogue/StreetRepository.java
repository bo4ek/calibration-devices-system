package com.softserve.edu.repository.catalogue;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.softserve.edu.entity.catalogue.Street;

@Repository
public interface StreetRepository extends CrudRepository<Street, Long> {
    List<Street> findByLocalityId(Long id);

    Long findIdByDesignation(String designation);

    Street findByLocalityIdAndDesignation(Long localityId, String designation);

    Street findByDesignation(String designation);
}
