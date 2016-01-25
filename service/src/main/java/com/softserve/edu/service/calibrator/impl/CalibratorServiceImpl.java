package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.BbiProtocol;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.repository.*;
import com.softserve.edu.service.calibrator.CalibratorService;
import com.softserve.edu.service.storage.FileOperations;
import com.softserve.edu.service.utils.EmployeeDTO;

import com.softserve.edu.service.utils.TypeConverter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;
@Setter
@Service
public class CalibratorServiceImpl implements CalibratorService {

    @Autowired
    private OrganizationRepository calibratorRepository;

    @Autowired
    private UploadBbiRepository uploadBbiRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileOperations fileOperations;

    @Autowired
    private AdditionalInfoRepository additionalInfoRepository;

    @Autowired
    private CounterTypeRepository counterTypeRepository;

    private final Logger logger = Logger.getLogger(CalibratorServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public Organization findById(Long id) {
        return calibratorRepository.findOne(id);
    }

    @Override
    @Transactional
    public CounterType findOneBySymbolAndStandardSize(String symbol, String standardSize) {
        return counterTypeRepository.findOneBySymbolAndStandardSize(symbol, standardSize);
    }

    @Override
    @Transactional
    public void uploadBbi(InputStream fileStream, String verificationId,
                         String originalFileFullName) throws IOException{
        Optional<Verification> retrievedVerification = Optional.ofNullable(verificationRepository.findOne(verificationId));
        Verification verification = retrievedVerification.get();
        uploadBbi(fileStream, verification, originalFileFullName);
    }

    @Override
    @Transactional
    public void uploadBbi(InputStream fileStream, Verification verification,
                          String originalFileFullName) throws IOException{
        fileOperations.putBbiFile(fileStream, verification.getId(), originalFileFullName);
        BbiProtocol bbiProtocol = new BbiProtocol(originalFileFullName, verification);
        Set<BbiProtocol> bbiProtocolsOfVerification = verification.getBbiProtocols();
        bbiProtocolsOfVerification.add(bbiProtocol);
        verification.setBbiProtocols(bbiProtocolsOfVerification);
        verificationRepository.save(verification);
        uploadBbiRepository.save(bbiProtocol);
    }


    @Override
    @Transactional(readOnly = true)
    public String findBbiFileByOrganizationId(String id) {
        return uploadBbiRepository.findFileNameByVerificationId(id);
    }

    @Override
    @Transactional
    public User oneCalibratorEmployee(String username) {
        return userRepository.findOne(username);
    }

    @Override
    @Transactional
    public List<EmployeeDTO> getAllCalibratorEmployee(List<String> role, User employee) {
        List<EmployeeDTO> calibratorListEmployee = new ArrayList<>();
        if (role.contains(UserRole.CALIBRATOR_ADMIN.name())) {
            List<User> allAvailableUsersList = userRepository.findAllAvailableUsersByRoleAndOrganizationId(UserRole.CALIBRATOR_EMPLOYEE,
                    employee.getOrganization().getId())
                    .stream()
                    .collect(Collectors.toList());
            calibratorListEmployee = EmployeeDTO.giveListOfEmployeeDTOs(allAvailableUsersList);
        } else {
            EmployeeDTO userPage = new EmployeeDTO(employee.getUsername(), employee.getFirstName(),
                    employee.getLastName(), employee.getMiddleName(), role.get(0));
            calibratorListEmployee.add(userPage);
        }
        return calibratorListEmployee;
    }

    @Override
    @Transactional
    public void assignCalibratorEmployee(String verificationId, User calibratorEmployee) {
        Verification verification = verificationRepository.findOne(verificationId);
        verification.setCalibratorEmployee(calibratorEmployee);
        verification.setReadStatus(Verification.ReadStatus.READ);
        if (calibratorEmployee != null) {
            verification.setSentToCalibratorDate(new Date());
        }
        if (!verification.isCounterStatus() && (calibratorEmployee != null)) {
            verification.setTaskStatus(Status.PLANNING_TASK);
        } else {
            verification.setTaskStatus(null);
        }
        verificationRepository.save(verification);
    }

    /**
     * Check if additional info is already added
     * for the verification
     *
     * @param verificationId
     * @return {@literal true} if exists, else {@literal false}
     */
    @Override
    public boolean checkIfAdditionalInfoExists(String verificationId) {
        Verification verification = verificationRepository.findOne(verificationId);
        return verification.isAddInfoExists();
    }

    @Override
    public Set<String> getTypesById(Long id) {
        return TypeConverter.enumToString(calibratorRepository.findOrganizationTypesById(id));
    }


}
