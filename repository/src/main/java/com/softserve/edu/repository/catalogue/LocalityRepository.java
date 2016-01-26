package com.softserve.edu.repository.catalogue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softserve.edu.entity.catalogue.Locality;

@Repository
public interface LocalityRepository extends CrudRepository<Locality, Long> {

    List<Locality> findDistinctByDistrictId(Long id);

    /**
     * Finds all Localities by iDs, that are serviced by the organization
     *
     * @param ids List of Localities' ids
     * @return
     */
    List<Locality> findByIdIn(Collection<Long> ids);

    /**
     * Find all mail indexes by locality designation and district id
     *
     * @param designation
     * @param districtId
     * @return
     */
    @Query("SELECT locality.mailIndex FROM Locality locality INNER JOIN locality.district d " +
            "WHERE d.id=:districtId AND locality.designation=:designation")
    List<String> findMailIndexByDesignationAndDistrictId(@Param("designation") String designation, @Param("districtId") Long districtId);


    @Query("SELECT l FROM Locality l JOIN l.organizations organizations JOIN l.district district " +
            "WHERE district.id =:districtId AND organizations.id=:organizationId ")
    List<Locality> findByDistrictIdAndOrganizationId(@Param("districtId") Long districtId, @Param("organizationId") Long organizationId);

    /**
     * Find all localities by organization id
     *
     * @param organizationId
     * @return
     */
    @Query("SELECT l " +
            "FROM Organization org INNER JOIN org.localities l " +
            "WHERE org.id=:organizationId")
    List<Locality> findLocalitiesByOrganizationId(@Param("organizationId") Long organizationId);

    Long findIdByDesignation(@Param("Designation") String designation); // find city id by city name???
}
