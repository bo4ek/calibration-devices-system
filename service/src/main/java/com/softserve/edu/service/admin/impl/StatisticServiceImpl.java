package com.softserve.edu.service.admin.impl;

import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.repository.*;
import com.softserve.edu.service.admin.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private CounterTypeRepository counterTypeRepository;

    @Override
    public Long countOrganizations() {
        return organizationRepository.count();
    }

    @Override
    public Long countUsers() {
        return userRepository.count();
    }

    @Override
    public Long countDevices() {
        return deviceRepository.count();
    }

    @Override
    public Long countCounterTypes() {
        return counterTypeRepository.count();
    }

    @Override
    public Long countVerifications() {
        return verificationRepository.count();
    }

    @Override
    public Long countSysAdmins() {
        return new Long(userRepository
                        .findByUserRoleAllIgnoreCase(UserRole.SYS_ADMIN)
                        .size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User employeeExist(String username) {
        return userRepository.findOne(username);
    }
}
