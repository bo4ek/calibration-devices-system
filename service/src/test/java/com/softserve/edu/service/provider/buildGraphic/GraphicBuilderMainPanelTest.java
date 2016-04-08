package com.softserve.edu.service.provider.buildGraphic;

import com.softserve.edu.entity.enumeration.organization.OrganizationType;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraphicBuilderMainPanelTest {

    @Test
    public void testListOfMonths() throws Exception {
        List<MonthOfYear> monthsFullYear = GraphicBuilder.listOfMonths(new Date(2015 - 1900, 0, 1), new Date(2015 - 1900, 11, 31));
        assertEquals(12, monthsFullYear.size());


        List<MonthOfYear> monthsFullYearPlusOneMonth = GraphicBuilder.listOfMonths(new Date(2015 - 1900, 0, 1), new Date(2016 - 1900, 0, 1));
        assertEquals(13, monthsFullYearPlusOneMonth.size());


        //arguments set backwards case
        List<MonthOfYear> months_wrong_args = GraphicBuilder.listOfMonths(new Date(2015 - 1900, 0, 1), new Date(2014 - 1900, 0, 1));
        assertEquals(0, months_wrong_args.size());


        List<MonthOfYear> one_month = GraphicBuilder.listOfMonths(new Date(2015 - 1900, 0, 15), new Date(2015 - 1900, 0, 31));
        assertEquals(1, one_month.size());
        assertEquals(0, one_month.get(0).month);


        List<MonthOfYear> one_month_from_start = GraphicBuilder.listOfMonths(new Date(2015 - 1900, 0, 1), new Date(2015 - 1900, 0, 31));
        assertEquals(1, one_month_from_start.size());


        List<MonthOfYear> one_month_day = GraphicBuilder.listOfMonths(new Date(2015 - 1900, 0, 1), new Date(2015 - 1900, 0, 1));
        assertEquals(1, one_month_day.size());
        assertEquals(0, one_month_day.get(0).month);
    }

    @Test
    public void testBuilderData() throws Exception {
        List<MonthOfYear> months = new ArrayList<>();

        MonthOfYear month = new MonthOfYear(1, 2015);
        MonthOfYear month2 = new MonthOfYear(2, 2015);
        months.add(month);
        months.add(month2);


        Organization petroOrganization = mock(Organization.class);
        String organizationName = "Petro Organization";
        when(petroOrganization.getName()).thenReturn("Petro Organization");
        Set<OrganizationType> organizationTypes = new HashSet<>();
        organizationTypes.add(OrganizationType.STATE_VERIFICATOR);
        when(petroOrganization.getOrganizationTypes()).thenReturn(organizationTypes);


        List<Verification> verifications = new ArrayList<>();
        Verification verification = mock(Verification.class);

        when(verification.getSentToVerificatorDate())
                .thenReturn(new Date(2015 - 1900, 1, 20))
                .thenReturn(new Date(2015 - 1900, 2, 25))
                .thenReturn(new Date(2015 - 1900, 2, 31));
        verifications.add(verification);
        verifications.add(verification);
        verifications.add(verification);


        List<ProviderEmployeeGraphic> providerEmployeeGraphicList = GraphicBuilderMainPanel.builderData(verifications, months, petroOrganization);
        assertNotEquals(providerEmployeeGraphicList, null);
        assertEquals(1, providerEmployeeGraphicList.size());

        assertArrayEquals(providerEmployeeGraphicList.get(0).data, new double[]{1.0, 2.0}, 0);
        assertEquals(organizationName, providerEmployeeGraphicList.get(0).name);
        assertEquals(1, providerEmployeeGraphicList.get(0).monthList.get(0).month);
        assertEquals(2, providerEmployeeGraphicList.get(0).monthList.get(1).month);
    }
}