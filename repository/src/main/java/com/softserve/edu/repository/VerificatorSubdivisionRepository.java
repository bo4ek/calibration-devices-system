package com.softserve.edu.repository;

import com.softserve.edu.entity.catalogue.Team.VerificatorSubdivision;
import com.softserve.edu.entity.organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificatorSubdivisionRepository extends PagingAndSortingRepository<VerificatorSubdivision, String>, JpaSpecificationExecutor {

    Page<VerificatorSubdivision> findByOrganization(Organization organization, Pageable pageable);

    Page<VerificatorSubdivision> findByOrganizationAndIdLikeIgnoreCase(Organization organization, String id, Pageable pageable);

    List<VerificatorSubdivision> findByOrganizationId(Long organizationId);


}
