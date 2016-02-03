package com.softserve.edu.service.state.verificator;

import com.softserve.edu.entity.catalogue.Team.VerificatorSubdivision;
import com.softserve.edu.entity.organization.Organization;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Project system-calibration-devices
 * Created by bo4ek on 27.01.2016.
 */
public interface StateVerificatorSubdivisionService {

    void addSubdivision(VerificatorSubdivision subdivision);

    void editSubdivision(String id, String name, String leader, String leaderEmail, String leaderPhone);

    void deleteSubdivision(String subdivisionId);

    VerificatorSubdivision findById(String id);

    List<VerificatorSubdivision> findByOrganizationId(Long id);

    Page<VerificatorSubdivision> findByOrganizationAndSearchAndPagination(int pageNumber, int itemsPerPage,
                                                                          Organization organization, String search);

    boolean isIdAvailable(String id);
}