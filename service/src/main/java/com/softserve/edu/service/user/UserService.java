package com.softserve.edu.service.user;

import com.softserve.edu.entity.user.User;

import java.util.List;

public interface UserService {

    boolean isExistsWithUsername(String username);

    boolean changeField(String username, String newValue, String typeOfField);

    User getUser(String username);

    boolean changePassword(String username, String oldPassword, String newPassword);

    List<User> findByRole(String role);

    User findOne(String username);

    List<String> getRoles(String username);

    void updateUser(User user);

    void createSuperAdminIfNotExists(User user);

    List<User> findBySubdivisionId(String id);
}