package com.softserve.edu.service.admin;

import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.catalogue.util.LocalityDTO;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.enumeration.organization.OrganizationType;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.organization.OrganizationEditHistory;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.utils.ListToPageTransformer;

import java.util.List;
import java.util.Set;

public interface OrganizationService {


    void addOrganizationWithAdmin(String name, String email, String phone, List<String> types, List<String> counters, Integer employeesCapacity,
                                  Integer maxProcessTime, String firstName, String lastName, String middleName,
                                  String username, Address address, String adminName, Long[] localityIdList);


    ListToPageTransformer<Organization> getOrganizationsBySearchAndPagination(int pageNumber, int itemsPerPage, String name,
                                                                              String email, String number, String type, String region,
                                                                              String district, String locality, String streetToSearch,
                                                                              String sortCriteria, String sortOrder);

    Organization getOrganizationById(Long id);

    void editOrganization(Long organizationId, String name, String phone, String email, List<String> types, List<String> counters,
                          Integer employeesCapacity, Integer maxProcessTime, Address address, String password,
                          String username, String firstName, String lastName, String middleName, String adminName, List<Long> serviceAreas);

    Integer getOrganizationEmployeesCapacity(Long organizationId);

    void sendOrganizationChanges(Organization organization, User admin);

    List<OrganizationEditHistory> getHistoryByOrganizationId(Long organizationId);

    List<Organization> findAllByLocalityId(Long localityId);

    List<Organization> findAllByLocalityIdAndTypeId(Long localityId, OrganizationType typeId);

    Set<OrganizationType> findOrganizationTypesById(Long id);

    List<Organization> findByLocalityIdAndTypeAndDevice( Long localityId, OrganizationType orgType, Device.DeviceType deviceType );


    Set<Device.DeviceType> findDeviceTypesByOrganizationId( Long organizationId);

    List<Organization> findByServiceAreaIdsAndOrganizationType(Set<Long> serviceAreaIds, OrganizationType type);

    List<Organization> findByOrganizationTypeAndDeviceType( OrganizationType organizationType, Device.DeviceType deviceType);

    Set<Organization> findByIdAndTypeAndActiveAgreementDeviceType( Long customerId, OrganizationType organizationType, Device.DeviceType deviceType);

}
