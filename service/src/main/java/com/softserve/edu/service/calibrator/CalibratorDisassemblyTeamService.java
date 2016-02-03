package com.softserve.edu.service.calibrator;

import com.softserve.edu.entity.catalogue.Team.DisassemblyTeam;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.service.exceptions.DuplicateRecordException;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CalibratorDisassemblyTeamService {

    List<DisassemblyTeam> getByOrganization(Organization organization);

    Page<DisassemblyTeam> findByOrganizationAndSearchAndPagination(int pageNumber, int itemsPerPage,
                                                                   Organization organization, String search);

    void add(DisassemblyTeam disassemblyTeam) throws DuplicateRecordException;

    DisassemblyTeam findById(String teamId);

    void edit(String id, String name, Date effectiveTo, Set<Device.DeviceType> specialization, String leaderFullName,
              String leaderPhone, String leaderEmail);

    void delete(String teamId);

    boolean isTeamExist(String teamUsername);

    List<DisassemblyTeam> findAllAvaliableTeams(Date workDate, String applicationFiled, String userId);
}