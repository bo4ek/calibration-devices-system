package com.softserve.edu.service.utils;

import com.softserve.edu.entity.user.User;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeDTOTest {

    @Mock
    private User user;

    @Test
    public void testgiveListOfEmployeeDTOs() {
        when(user.getUsername()).thenReturn("testName");
        when(user.getFirstName()).thenReturn("testFirstName");
        when(user.getLastName()).thenReturn("testgetLastName");
        when(user.getMiddleName()).thenReturn("testMiddleName");
        List<User>listUser = Arrays.asList(user, user);
        List<EmployeeDTO> employeeDTOList = EmployeeDTO.giveListOfEmployeeDTOs(listUser);
        assertEquals(2, employeeDTOList.size());
    }


}
