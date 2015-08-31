package com.softserve.edu.service.admin;

import com.softserve.edu.entity.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.user.UserRole;
import com.softserve.edu.entity.util.Roles;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.repository.UserRoleRepository;
import com.softserve.edu.service.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Dmytro on 8/20/2015.
 */
public class UsersServiceTest {

 /*   @InjectMocks
    private UsersService injectMockUserService;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private UserRoleRepository mockUserRoleRepository;

    @Mock
    private User mockUser;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExistsWithUsername() throws Exception {
        when(mockUserRepository.findOne(anyString())).thenReturn(null);
        assertEquals(true, injectMockUserService.existsWithUsername(anyString()));
        when(mockUserRepository.findOne(anyString())).thenReturn(new User());
        assertTrue(injectMockUserService.existsWithUsername(anyString()) != true);
    }

    @Test
    public void testGetUsersBySearchAndPagination() throws Exception {
        final int pageNumber = 5;
        final int itemsPerPage = 10;
        final String search = "calib";
        final Page<User> pageUsers = (Page<User>) mock(Page.class);
        PageRequest pageRequest = new PageRequest(pageNumber - 1, itemsPerPage);

        when(mockUserRepository.findAll(pageRequest)).thenReturn(pageUsers);
        when(mockUserRepository.findByRoleLikeIgnoreCase("%" + search
                + "%", pageRequest)).thenReturn(pageUsers);

        ArgumentCaptor<PageRequest> pageRequestArg = ArgumentCaptor.forClass(PageRequest.class);

        injectMockUserService.getUsersBySearchAndPagination(pageNumber, itemsPerPage, null);

        verify(mockUserRepository).findAll(pageRequestArg.capture());
        assertEquals(pageRequest.first(), pageRequestArg.getValue().first());
        assertEquals(pageUsers, injectMockUserService
                .getUsersBySearchAndPagination(pageNumber, itemsPerPage, null));

        ArgumentCaptor<PageRequest> pageRequestArg2 = ArgumentCaptor.forClass(PageRequest.class);

        injectMockUserService.getUsersBySearchAndPagination(pageNumber, itemsPerPage, search);

        verify(mockUserRepository).findByRoleLikeIgnoreCase(anyString(), pageRequestArg2.capture());
        assertEquals(pageRequest.first(), pageRequestArg.getValue().first());
        assertEquals(pageRequest.first(), pageRequestArg2.getValue().first());
        assertEquals(pageUsers, injectMockUserService
                .getUsersBySearchAndPagination(pageNumber, itemsPerPage, search));
    }

    @Test
    public void testGetRoleByUserName() throws Exception {
        final String username = "Admin";
        final String expectedString = "OK";
        when(mockUserRepository.getRoleByUserName(username)).thenReturn(expectedString);
        assertEquals(expectedString, injectMockUserService.getRoleByUserName(username));
    }

    @Test
    public void testGetRoleByUserNam() throws Exception {
        final List<UserRole> listUsers = (List<UserRole>) mock(List.class);
        final String username = "Admin";
        when(mockUserRepository.getRoleByUserNam(username)).thenReturn(listUsers);
        assertEquals(listUsers, injectMockUserService.getRoleByUserNam(username));
    }

    @Test
    public void testGetRoles() throws Exception {
        final List<String> listString = (List<String>) mock(List.class);
        final String username = "Admin";
        when(mockUserRoleRepository.getRoles(username)).thenReturn(listString);
        assertEquals(listString, injectMockUserService.getRoles(username));
    }

    @Ignore
    @Test
    public void testGetOrganization() throws Exception {
        final List<String> listString = (List<String>) mock(List.class);
        final String username = "Admin";
        final String expectedString = "OK";
        final User user = new User();

        when(mockUserRoleRepository.getRoles(username)).thenReturn(listString);
        when(listString.contains(Roles.SYS_ADMIN.name())).thenReturn(false);
        when(mockUser.getOrganization().getName()).thenReturn(expectedString);
        assertEquals(expectedString, injectMockUserService.getOrganization(username, mockUser));
    }*/
}