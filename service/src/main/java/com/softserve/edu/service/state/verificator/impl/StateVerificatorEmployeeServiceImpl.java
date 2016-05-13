package com.softserve.edu.service.state.verificator.impl;

import com.softserve.edu.service.state.verificator.StateVerificatorEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.softserve.edu.entity.user.User;
import com.softserve.edu.repository.UserRepository;

@Service
public class StateVerificatorEmployeeServiceImpl implements StateVerificatorEmployeeService {

    @Autowired
    private UserRepository stateVerificatorEmployeeRepository;

    /**
     * Adds Employee for state verificator. Saves encoded password and
     * gives role STATE_VERIFICATOR_EMPLOYEE for user
     *
     * @param stateVerificatorEmployee data for creation employee
     */
    @Override
    @Transactional
    public void addEmployee(User stateVerificatorEmployee) {

        String passwordEncoded = new BCryptPasswordEncoder().encode(stateVerificatorEmployee.getPassword());
        stateVerificatorEmployee.setPassword(passwordEncoded);
        stateVerificatorEmployeeRepository.save(stateVerificatorEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public User oneProviderEmployee(String username) {
        return stateVerificatorEmployeeRepository.findOne(username);
    }
}
