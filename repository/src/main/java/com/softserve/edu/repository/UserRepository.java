package com.softserve.edu.repository;

import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.repository.catalogue.UserRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String>, UserRepositoryCustom {

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u" +
            " FROM User u" +
            " WHERE  :userRole in elements(u.userRoles)")
    Set<User> findByUserRoleAllIgnoreCase(@Param("userRole") UserRole userRole);

    @Query("SELECT elements(u.userRoles)" +
            " FROM User u" +
            " WHERE u.username = :username")
    Set<UserRole> getRolesByUserName(@Param("username") String username);

    @Query("SELECT u" +
            " FROM User u" +
            " WHERE :userRole in elements(u.userRoles)" +
            " AND u.organization.id = :organizationId")
    Set<User> findByUserRoleAndOrganizationId(@Param("userRole") UserRole userRole,
                                              @Param("organizationId") Long organizationId);

    @Query("SELECT u" +
            " FROM User u" +
            " WHERE :userRole in elements(u.userRoles)" +
            " AND u.organization.id = :organizationId" +
            " AND u.isAvailable = 1")
    Set<User> findAllAvailableUsersByRoleAndOrganizationId(@Param("userRole") UserRole userRole,
                                                           @Param("organizationId") Long organizationId);

    @Query("SELECT u" +
            " FROM User u" +
            " WHERE (:employeeRole in elements(u.userRoles) OR :adminRole in elements(u.userRoles))" +
            " AND u.organization.id = :organizationId" +
            " AND u.isAvailable = 1")
    Set<User> findAllAvailableUsersByEmployeeAndAdminRoleAndOrganizationId(@Param("employeeRole") UserRole employeeRole,
                                                                           @Param("adminRole") UserRole adminRole,
                                                                           @Param("organizationId") Long organizationId);

    Page<User> findByOrganizationId(@Param("organizationId")Long organizationId, Pageable pageable);

    List<User> findAll(Specification<User> userSpecification);

    List<User> findByVerificatorSubdivision(@Param("subdivisionId")String subdivisionId);
}