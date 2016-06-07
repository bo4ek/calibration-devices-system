package com.softserve.edu.service.verification.impl;

import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.service.tool.MailService;

import com.softserve.edu.service.verification.VerificationProviderEmployeeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VerificationProviderEmployeeServiceImpl implements VerificationProviderEmployeeService {

    Logger logger = Logger.getLogger(VerificationProviderEmployeeServiceImpl.class);

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    MailService mailService;

    /**
     * Assigns provider employee to the verification
     * and asynchronously sends mail to customer
     *
     * @param verificationId
     * @param providerEmployee
     */
    @Transactional
    public void assignProviderEmployee(String verificationId, User providerEmployee) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification != null && verification.getProvider().getId().equals(providerEmployee.getOrganization().getId())) {
            verification.setProviderEmployee(providerEmployee);
            verification.setStatus(Status.ACCEPTED);
            if (verification.getClientData() != null && verification.getClientData().getEmail() != null) {
                mailService.sendAcceptMail(verification.getClientData().getEmail(), verificationId, verification.getDevice().getDeviceType().name());
            }
            verification.setReadStatus(Verification.ReadStatus.READ);
            verification.setExpirationDate(null);
            verificationRepository.save(verification);
        }
    }

    @Override
    @Transactional
    public void removeProviderEmployee(String verificationId, User providerEmployee) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification != null && verification.getProvider().getId().equals(providerEmployee.getOrganization().getId())) {
            verification.setProviderEmployee(null);
            verification.setStatus(Status.SENT);
            verification.setExpirationDate(null);
            verificationRepository.save(verification);
        }
    }

    @Transactional
    public void assignProviderEmployeeForNotStandardVerification(String verificationId, User providerEmployee) {
        Verification verification = verificationRepository.findOne(verificationId);
        if (verification != null && verification.getProviderEmployee() == null && verification.getProvider().getId().equals(providerEmployee.getOrganization().getId())) {
            verification.setProviderEmployee(providerEmployee);
            if (providerEmployee == null) {
                verification.setStatus(Status.SENT_TO_PROVIDER);
            } else {
                if (verification.getVerificationTime() == null) {
                    verification.setStatus(Status.IN_PROGRESS);
                } else {
                    verification.setStatus(Status.TEST_COMPLETED);
                }
            }
            verification.setProviderFromBBI(verification.getProvider());
            verification.setExpirationDate(null);
            verificationRepository.save(verification);
        }
    }

    @Override
    public boolean isAdmin(User user) {
        return user.getUserRoles().contains(UserRole.PROVIDER_ADMIN);
    }

    /**
     * @return Returns user (provider) assigned to the verification or null
     */
    @Transactional(readOnly = true)
    public User getProviderEmployeeById(String idVerification) {
        return verificationRepository.getProviderEmployeeById(idVerification);
    }

    /**
     * Current method return list of verifications by current Provider user
     *
     * @param username
     * @return list of Verification
     */
    @Transactional(readOnly = true)
    public List<Verification> getVerificationListByProviderEmployee(String username) {
        return verificationRepository.findByProviderEmployeeUsernameAndStatus(username, Status.ACCEPTED);
    }

    /**
     * Current method return list of verifications by current Calibrator user
     *
     * @param username
     * @return list of Verification
     */
    @Transactional(readOnly = true)
    public List<Verification> getVerificationListByCalibratorEmployee(String username) {
        return verificationRepository.findByCalibratorEmployeeUsernameAndStatus(username, Status.IN_PROGRESS);
    }

    /**
     * Current method return list of verifications by current Calibrator user
     *
     * @param username
     * @return list of Verification
     */
    @Transactional(readOnly = true)
    public List<Verification> getVerificationListByStateVerificatorEmployee(String username) {
        return verificationRepository.findByStateVerificatorEmployeeUsernameAndStatus(username, Status.SENT_TO_VERIFICATOR);
    }

    /**
     * This method count of work in current time
     *
     * @param username
     * @return number of work for user
     */
    @Transactional
    public Long countByProviderEmployeeTasks(String username) {
        return verificationRepository.countByProviderEmployeeUsernameAndStatus(username, Status.ACCEPTED);
    }

    /**
     * This method count of work in current time
     *
     * @param username
     * @return number of work for user
     */
    @Transactional
    public Long countByCalibratorEmployeeTasks(String username) {
        return verificationRepository.countByCalibratorEmployeeUsernameAndStatus(username, Status.IN_PROGRESS);
    }

    /**
     * This method count of work in current time
     *
     * @param username
     * @return number of work for user
     */
    @Transactional
    public Long countByStateVerificatorEmployeeTasks(String username) {
        return verificationRepository.countByStateVerificatorEmployeeUsernameAndStatus(username, Status.SENT_TO_VERIFICATOR);
    }

    /**
     * This method search user by username
     *
     * @param username
     * @return user
     */
    @Transactional(readOnly = true)
    public User oneProviderEmployee(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }
}
