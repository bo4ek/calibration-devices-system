package com.softserve.edu.service.user.impl;

import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.util.ConvertSetEnumsToListString;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Check whereas user with {@code username} exist in database
     * Checks whereas user with {@code username} exist in database
     *
     * @param username must not be non {@literal null}
     * @return {@literal true} if user with {@code username} doesn't exist in database, else {@literal false}
     */
    @Transactional
    @Override
    public boolean isExistsWithUsername(String username) {
        return userRepository.findOne(username) == null;
    }


    /**
     * Changes given type of user's field
     *
     * @param username must not be non {@literal null}
     * @param newValue new value
     * @param typeOfField   type  of user's field
     * @return {@literal true} if changed, else - {@literal false}
     */
    @Transactional
    @Override
    public boolean changeField(String username, String newValue, String typeOfField) {
        boolean isChanged = false;
        if (typeOfField != null && username != null && newValue != null) {
            User user = userRepository.findOne(username);
            if (user != null) {
                switch (typeOfField) {
                    case "firstName":
                        user.setFirstName(newValue);
                        isChanged = true;
                        break;
                    case "lastName":
                        user.setLastName(newValue);
                        isChanged = true;
                        break;
                    case "middleName":
                        user.setMiddleName(newValue);
                        isChanged = true;
                        break;
                    case "email":
                        user.setEmail(newValue);
                        isChanged = true;
                        break;
                    case "phone":
                        user.setPhone(newValue);
                        isChanged = true;
                        break;
                }
                if (isChanged) {
                    userRepository.save(user);
                }
            }
        }
        return isChanged;
    }

    /**
     * Finds an employee by the given username
     *
     * @param username of employee
     * @return employee entity
     * @throws ClassCastException if username isn't a employee
     */
    @Transactional
    @Override
    public User getUser(String username) throws ClassCastException {
        return userRepository.findOne(username);
    }

    /**
     * Changes user's password
     *
     * @param username    must not be non {@literal null}
     * @param oldPassword old password
     * @param newPassword new password
     * @return {@literal true} if changed, if not or passwords don't match - {@literal false}
     */
    @Transactional
    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        boolean isChanged = false;
        if (username != null && oldPassword != null && newPassword != null) {
            User user = userRepository.findOne(username);
            if (user != null) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                isChanged = passwordEncoder.matches(oldPassword, user.getPassword());
                if (isChanged) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                }
            }
        }
        return isChanged;
    }

    @Transactional
    @Override
    public List<User> findByRole(String role){
        return userRepository
                .findByUserRoleAllIgnoreCase(UserRole.valueOf(role))
                .stream()
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public User findOne(String username) {
        return userRepository.findOne(username);
    }

    @Transactional
    @Override
    public List<String> getRoles(String username) {
        return ConvertSetEnumsToListString.convertToListString(
                userRepository.getRolesByUserName(username));
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void createSuperAdminIfNotExists(User user){
        if (isExistsWithUsername(user.getUsername()) && findByRole("SUPER_ADMIN").isEmpty()){
            userRepository.save(user);
        }
    }

    /**
     * Finds all users according subdivision's id
     *
     * @param subdivisionId id of subdivision
     * @return list of Users
     */
    @Transactional
    @Override
    public List<User> findBySubdivisionId(String subdivisionId) {
        return userRepository.findByVerificatorSubdivision(subdivisionId);
    }

    /**
     * Updates users column "subdivisionId" with "null", if it's value matches with @param id
     *
     * @param id id of subdivision
     * @return
     */
    @Transactional
    @Override
    public int setSubdivisionIdNull(String id) {
        return userRepository.setSubdivisionIdNull(id);
    }

    @Override
    public boolean haveUserPermissionToAssignPerson(User user) {
        Set<UserRole> userRoles = user.getUserRoles();
        return userRoles.contains(UserRole.CALIBRATOR_ADMIN) || userRoles.contains(UserRole.PROVIDER_ADMIN)
                || userRoles.contains(UserRole.STATE_VERIFICATOR_ADMIN);
    }

    @Override
    public boolean isCalibrator(User user) {
        return user.getUserRoles().contains(UserRole.CALIBRATOR_ADMIN) || user.getUserRoles().contains(UserRole.CALIBRATOR_EMPLOYEE);
    }

    @Override
    public boolean isVerificator(User user) {
        return user.getUserRoles().contains(UserRole.STATE_VERIFICATOR_ADMIN) || user.getUserRoles().contains(UserRole.STATE_VERIFICATOR_EMPLOYEE);
    }
}