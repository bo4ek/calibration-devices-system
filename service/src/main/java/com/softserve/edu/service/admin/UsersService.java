package com.softserve.edu.service.admin;

import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.utils.ListToPageTransformer;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

public interface UsersService {

    boolean isExistsWithUsername(String username);

    List<String> getRoles(String username);

    void addSysAdmin( String  username, String firstName, String lastName, String middleName, String phone,
                             String email,  Address address) throws MessagingException, UnsupportedEncodingException;

    List<User> findByOrganizationId(Long organizationId, int pageNumber, int itemsPerPage);

    Long countVerifications(User user);

    User findOne(String username);

    void deleteSysAdmin(String username);

    void editSysAdmin( String  username, String password, String firstName, String lastName, String middleName, String phone,
                       String email,  Address address)  throws MessagingException, UnsupportedEncodingException;

    ListToPageTransformer<User>
    findPageOfAllEmployees(int pageNumber, int itemsPerPage, String userName,
                           String role, String firstName, String lastName, String organization,
                           String telephone,  String sortCriteria, String sortOrder);

    ListToPageTransformer<User> findAllSysAdmins();
}