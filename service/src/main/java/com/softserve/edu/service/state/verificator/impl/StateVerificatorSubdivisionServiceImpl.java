package com.softserve.edu.service.state.verificator.impl;

import com.softserve.edu.entity.catalogue.Team.VerificatorSubdivision;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.repository.VerificatorSubdivisionRepository;
import com.softserve.edu.service.state.verificator.StateVerificatorSubdivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StateVerificatorSubdivisionServiceImpl implements StateVerificatorSubdivisionService {

    @Autowired
    private VerificatorSubdivisionRepository subdivisionRepository;

    @Override
    @Transactional
    public void addSubdivision(VerificatorSubdivision subdivision) {
        subdivisionRepository.save(subdivision);
    }

    @Override
    @Transactional
    public void editSubdivision(Long organizationId ,String id, String name, String leader, String leaderEmail, String leaderPhone) {
        VerificatorSubdivision subdivision = subdivisionRepository.findOne(id);
        subdivision.setName(name);
        subdivision.setLeader(leader);
        subdivision.setLeaderEmail(leaderEmail);
        subdivision.setLeaderPhone(leaderPhone);
        subdivisionRepository.save(subdivision);
    }

    @Override
    @Transactional
    public void deleteSubdivision(String subdivisionId) {
        subdivisionRepository.delete(subdivisionRepository.findOne(subdivisionId));
    }

    @Override
    @Transactional(readOnly = true)
    public VerificatorSubdivision findById(String subdivisionId) {
        return subdivisionRepository.findOne(subdivisionId);
    }

    @Override
    public List<VerificatorSubdivision> findByOrganizationId(Long organizationId) {
        return subdivisionRepository.findByOrganizationId(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isIdAvailable(String id) {
        return subdivisionRepository.exists(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VerificatorSubdivision> findByOrganizationAndSearchAndPagination(int pageNumber, int itemsPerPage,
                                                                                 Organization organization, String search) {
        PageRequest pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);
        return search == null ? subdivisionRepository.findByOrganization(organization, pageRequest) :
                subdivisionRepository.findByOrganizationAndIdLikeIgnoreCase(organization, "%" + search + "%", pageRequest);
    }
}
