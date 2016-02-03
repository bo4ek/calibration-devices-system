package com.softserve.edu.repository;

import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.organization.OrganizationType;
import com.softserve.edu.entity.organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Long> {

    Page<Organization> findAll(Pageable pageable);

    Set<Organization> findAll(); //TODO: delete

    /**
     * Find all codeEDRPOU and organization names
     * @return list of arrays
     */
    @Query("SELECT a.codeEDRPOU, o.name  FROM Organization o join o.additionInfoOrganization a")
    List<Object> findAllCodeEDRPOU();

    /**
     * Find organization types by organization id
     *
     * @param organizationId
     * @return
     */
    @Query("SELECT elements(org.organizationTypes) FROM Organization org WHERE org.id=:organizationId")
    Set<OrganizationType> findOrganizationTypesById(@Param("organizationId") Long organizationId);

    /**
     * Find all device types by organization id
     *
     * @param organizationId
     * @return
     */
    @Query("SELECT elements(org.deviceTypes) FROM Organization org WHERE org.id=:organizationId")
    Set<Device.DeviceType> findDeviceTypesByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * FInd all organizations in selected location by
     *
     * @param localityId
     * @return
     */
    @Query("SELECT o FROM Organization o INNER JOIN o.localities l WHERE l.id=:localityId")
    List<Organization> findOrganizationByLocalityId(@Param("localityId") Long localityId);

    /**
     * Find all organizations in selected locality and organization type
     *
     * @param localityId
     * @param organizationType
     * @return
     */
    @Query("SELECT org FROM Organization org " +
            "INNER JOIN org.localities l " +
            "WHERE l.id=:localityId AND  :organizationType in elements(org.organizationTypes)")
    List<Organization> findOrganizationByLocalityIdAndType(@Param("localityId") Long localityId, @Param("organizationType") OrganizationType organizationType);

    /**
     * Find all organizations by organization types and device types
     * @param organizationType type of organization
     * @param deviceType type of device
     * @return list of organization
     */
    @Query("SELECT org FROM Organization org " +
            "WHERE ( :organizationType in elements(org.organizationTypes)) AND ( :deviceType in elements(org.deviceTypes)) ")
    List<Organization> findByOrganizationTypeAndDeviceType(@Param("organizationType") OrganizationType organizationType,
                                                           @Param("deviceType") Device.DeviceType deviceType);

    /**
     * Find all organizations in selected locality, organization type and device type
     *
     * @param localityId id of locality
     * @param orgType    type of organization
     * @param deviceType device type
     * @return list of organizations
     */
    @Query("SELECT org FROM Organization org " +
            "INNER JOIN org.localities l " +
            "WHERE l.id=:localityId AND  :orgType in elements(org.organizationTypes) AND :deviceType in elements(org.deviceTypes)")
    List<Organization> findByLocalityIdAndTypeAndDevice(@Param("localityId") Long localityId,
                                                        @Param("orgType") OrganizationType orgType, @Param("deviceType") Device.DeviceType deviceType);

    /**
     * Find organizations by organization id and
     * @param customerId
     * @param orgType
     * @param deviceType
     * @return
     */
    @Query("SELECT E FROM Organization O INNER JOIN O.agreements A " +
            "INNER JOIN A.executor E " +
            "WHERE O.id =:customerId AND A.deviceType =:deviceType AND A.isAvailable = true AND  :orgType in elements(E.organizationTypes)")
    Set<Organization> findByIdAndTypeAndActiveAgreementDeviceType(@Param("customerId") Long customerId,
                                                           @Param("orgType") OrganizationType orgType,
                                                           @Param("deviceType") Device.DeviceType deviceType);

    /**
     * Find organizations by organization id and
     * @param executorId
     * @param deviceType
     * @return
     */
    @Query(value = "select * from ORGANIZATION where ORGANIZATION.id in " +
            "(select agr.customerId from AGREEMENT agr inner join ORGANIZATION org on agr.executorId = org.id " +
            "where agr.executorId = ?1 and agr.deviceType = ?2 and agr.isAvailable = '1')", nativeQuery = true)
    Set<Organization> findCustomersByIdAndTypeAndActiveAgreementDeviceType(Long executorId, String deviceType);
}
