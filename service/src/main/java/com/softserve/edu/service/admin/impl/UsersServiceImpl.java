package com.softserve.edu.service.admin.impl;


import com.softserve.edu.entity.Address;
import com.softserve.edu.entity.enumeration.user.UserRole;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.util.AddEmployeeBuilder;
import com.softserve.edu.entity.util.ConvertSetEnumsToListString;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.service.admin.UsersService;
import com.softserve.edu.service.tool.MailService;
import com.softserve.edu.service.utils.ArchivalEmployeeQueryConstructorAdmin;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private MailService mail;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;
    /**
     * Check whereas user with {@code username} exist in database
     *
     * @param username must not be non {@literal null}
     * @return {@literal true} if user with {@code username} doesn't exist in
     * database, else {@literal false}
     */
    @Override
    public boolean isExistsWithUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username) == null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getRoles(String username){
        return ConvertSetEnumsToListString
                .convertToListString(userRepository.getRolesByUserName(username));
    }

    /**
     * Fetch all users except ones has role SYS_ADMIN  or SUPER_ADMIN depends on received data
     *
     * @param pageNumber
     * @param itemsPerPage
     * @param username
     * @param role
     * @param firstName
     * @param lastName
     * @param organization
     * @param telephone
     * @param sortCriteria
     * @param sortOrder
     * @return ListToPageTransformer<User> contains all required users
     */
    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<User>
    findPageOfAllEmployees(int pageNumber, int itemsPerPage,  String username,
                           String role, String firstName, String lastName, String organization,
                           String telephone,  String sortCriteria, String sortOrder){
        CriteriaQuery<User> criteriaQuery = ArchivalEmployeeQueryConstructorAdmin.buildSearchQuery(username, role, firstName,
                lastName, organization, telephone, sortCriteria, sortOrder, em);

        Long count = em.createQuery(ArchivalEmployeeQueryConstructorAdmin.buildCountQuery(username, role, firstName,
                lastName, organization, telephone, em)).getSingleResult();

        TypedQuery<User> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult((pageNumber - 1) * itemsPerPage);
        typedQuery.setMaxResults(itemsPerPage);
        List<User> employeeList = typedQuery.getResultList();

        ListToPageTransformer<User> result = new ListToPageTransformer<>();
        result.setContent(employeeList);
        result.setTotalItems(count);

        return result;
    }

    /**
     * Add and save new sys admin with received data and send email contains created password
     *
     * @param username
     * @param firstName
     * @param lastName
     * @param middleName
     * @param phone
     * @param email
     * @param address
     */
    @Override
    @Transactional
    public void addSysAdmin ( String  username, String firstName, String lastName, String middleName, String phone,
                             String email,  Address address) throws MessagingException, UnsupportedEncodingException {
        String newPassword = RandomStringUtils.randomAlphanumeric(5);
        String passwordEncoded = new BCryptPasswordEncoder().encode(newPassword);

        User newUser = new AddEmployeeBuilder()
                                                .username(username)
                                                .password(passwordEncoded)
                                                .firstName(firstName)
                                                .lastName(lastName)
                                                .middleName(middleName)
                                                .phone(phone)
                                                .email(email)
                                                .address(address)
                                                .setIsAvailable(true)
                                                .build();

        newUser.addRole(UserRole.SYS_ADMIN);

        mail.sendAdminNewPasswordMail(email, firstName, newPassword);

        userRepository.save(newUser);
    }


    /**
     * Fetch all users with role SYS_ADMIN
     *
     * @return ListToPageTransformer<User> contains all users with role SYS_ADMIN
     */
    @Override
    @Transactional(readOnly = true)
    public ListToPageTransformer<User> findAllSysAdmins() {

        Set<User> sysAdmins = userRepository.findByUserRoleAllIgnoreCase(UserRole.SYS_ADMIN);
        ListToPageTransformer<User> result = new ListToPageTransformer<>();
        Long countItems = new Long(sysAdmins.size());
        result.setTotalItems(countItems);
        result.setContent(userRepository.findByUserRoleAllIgnoreCase(UserRole.SYS_ADMIN)
                .stream()
                .distinct()
                .collect(Collectors.toList()));
        result.setTotalItems(result.getTotalItems());

        return result;
    }

    /**
     * Delete sys admin with username {@param username}
     *
     * @param username
     */
    @Override
    @Transactional
    public void deleteSysAdmin(String username) {
        userRepository.delete(username);
    }

    /**
     * Edit sys admin with username {@param username}
     *
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @param middleName
     * @param phone
     * @param email
     * @param address
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    @Override
    @Transactional
    public void editSysAdmin( String  username, String password, String firstName, String lastName, String middleName, String phone,
                              String email,  Address address)  throws MessagingException, UnsupportedEncodingException{
        User sysAdmin = userRepository.findByUsernameIgnoreCase(username);

        sysAdmin.setAddress(address);
        sysAdmin.setEmail(email);
        sysAdmin.setFirstName(firstName);
        sysAdmin.setLastName(lastName);
        sysAdmin.setMiddleName(middleName);
        sysAdmin.setPhone(phone);

        sysAdmin.setPassword(password != null && password.equals("generate") ? "generate" : sysAdmin.getPassword());

        if (sysAdmin.getPassword().equals("generate")) {
            String newPassword = RandomStringUtils.randomAlphanumeric(5);
            mail.sendNewPasswordMail(sysAdmin.getEmail(), sysAdmin.getFirstName(), newPassword);
            String passwordEncoded = new BCryptPasswordEncoder().encode(newPassword);
            sysAdmin.setPassword(passwordEncoded);
        }

        userRepository.save(sysAdmin);
    }


    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<User> findByOrganizationId(Long organizationId, int pageNumber, int itemsPerPage) {
        return IteratorUtils.toList(
                userRepository.findByOrganizationId(organizationId, new PageRequest(pageNumber, itemsPerPage)).iterator()
        );
    }

    @Override
    public Long countVerifications(User user) {
        String username = user.getUsername();
        return user
                .getUserRoles()
                .stream()
                .mapToLong(userRole -> userRepository.countEmployeeVerifications(userRole, username))
                .sum();
    }

    @Override
    public User findOne(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }
}
