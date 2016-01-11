package com.softserve.edu.service.calibrator.impl;

import com.softserve.edu.config.ServiceTestingConfig;
import com.softserve.edu.entity.catalogue.Team.DisassemblyTeam;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.calibrator.CalibratorDisassemblyTeamService;
import com.softserve.edu.service.exceptions.DuplicateRecordException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * this test was written for testingDatabase.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceTestingConfig.class})
public class CalibrationDisassemblyTeamServiceImplTest extends Assert {

    @Autowired
    private CalibratorDisassemblyTeamService teamService;

    @Autowired
    private OrganizationService organizationService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * return all teams from organization
     * @throws Exception
     */
    @Test
    public void testGetByOrganization() throws Exception {

        //get instances of organization for searching
        final Long idOrganization1 = 11l;
        final Long idOrganization2 = 100l;
        Organization organization1 = organizationService.getOrganizationById(idOrganization1);
        Organization organization2 = organizationService.getOrganizationById(idOrganization2);

        //get not expected teams
        Set<DisassemblyTeam> notExpectedTeams = organization2.getDisassemblyTeams();
        int actualLength = notExpectedTeams.toArray().length;
        assertEquals(1, actualLength);//in  test DB should be only one team in organization2

        //get one of expected team, and Set of actual teams
        DisassemblyTeam expectTeams = teamService.findById("500");
        Set<DisassemblyTeam> actualTeams = teamService.getByOrganization(organization1).stream().collect(Collectors.toSet());

        assertTrue(actualTeams.contains(expectTeams));
        assertFalse(actualTeams.contains(notExpectedTeams.iterator().next()));
    }


    @Test
    public void testAdd() throws DuplicateRecordException {

        final Long idOrganization = 11l;

        DisassemblyTeam disassemblyTeam = getDisassemblyTeamTest();
        disassemblyTeam.setOrganization(organizationService.getOrganizationById(idOrganization));
        //save in DB
        teamService.add(disassemblyTeam);
        //take from DB
        DisassemblyTeam disassemblyTeamDB = teamService.findById(disassemblyTeam.getId());

        assertEquals(disassemblyTeam, disassemblyTeamDB);

        assertEquals(disassemblyTeam.getName(), disassemblyTeamDB.getName());
        assertEquals(disassemblyTeam.getEffectiveTo(), disassemblyTeamDB.getEffectiveTo());
        assertEquals(disassemblyTeam.getSpecialization(), disassemblyTeamDB.getSpecialization());
        assertEquals(disassemblyTeam.getLeaderFullName(), disassemblyTeamDB.getLeaderFullName());
        assertEquals(disassemblyTeam.getLeaderPhone(), disassemblyTeamDB.getLeaderPhone());
        assertEquals(disassemblyTeam.getLeaderEmail(), disassemblyTeamDB.getLeaderEmail());
        assertEquals(disassemblyTeam.getOrganization(), disassemblyTeamDB.getOrganization());


        //checking DuplicateRecordException exception
        thrown.expect(DuplicateRecordException.class);
        thrown.expectMessage(equalTo("Team " + getDisassemblyTeamTest().getId() + " already exists."));
        teamService.add(getDisassemblyTeamTest());

    }


    @Test
    public void testEdit() throws Exception {

        DisassemblyTeam team = teamService.findById(getDisassemblyTeamTest().getId());

        team.setName("K-Man-Team");
        team.setEffectiveTo(new Date(11, 11, 11));
        Set<Device.DeviceType> deviceTypes = new HashSet<>();
        deviceTypes.add(Device.DeviceType.THERMAL);
        team.setSpecialization(deviceTypes);
        team.setLeaderFullName("K-Man");
        team.setLeaderPhone("0000000");
        team.setLeaderEmail("l@g.com");

        //save
        teamService.edit(team.getId(), team.getName(), team.getEffectiveTo(), team.getSpecialization(),
                team.getLeaderFullName(), team.getLeaderPhone(), team.getLeaderEmail());

        //get team from DB
        DisassemblyTeam teamFromDB = teamService.findById(team.getId());

        assertEquals(teamFromDB, team);

        assertEquals(team.getName(), teamFromDB.getName());
        assertEquals(team.getEffectiveTo(), teamFromDB.getEffectiveTo());
        assertEquals(team.getSpecialization(), teamFromDB.getSpecialization());
        assertEquals(team.getLeaderFullName(), teamFromDB.getLeaderFullName());
        assertEquals(team.getLeaderPhone(), teamFromDB.getLeaderPhone());
        assertEquals(team.getLeaderEmail(), teamFromDB.getLeaderEmail());
        assertEquals(team.getOrganization(), teamFromDB.getOrganization());

    }

    @Test
    public void testDelete() throws Exception {
        DisassemblyTeam disassemblyTeam = teamService.findById(getDisassemblyTeamTest().getId());
        assertNotNull(disassemblyTeam);
        teamService.delete(disassemblyTeam.getId());
        assertNull(teamService.findById(disassemblyTeam.getId()));
    }

    @Test
    public void testIsTeamExist() throws Exception {
        assertTrue(teamService.isTeamExist("500"));
        assertFalse(teamService.isTeamExist("499"));//not exist
    }


    /**
     * return test team with id 502
     * @return DisassemblyTeam
     */
    private DisassemblyTeam getDisassemblyTeamTest() {
        final String idTeam = "502";
        final String name = "Name";
        final Date date = new Date(2015, 1,1);
        final Set<Device.DeviceType> specialization = new HashSet<>();
        specialization.add(Device.DeviceType.THERMAL);
        final String leaderFullName = "Vova T T";
        final String leaderPhone = "9090104";
        final String leaderEmail = "c@gmail.com";

        return new DisassemblyTeam(idTeam, name, date, specialization, leaderFullName,
                leaderPhone, leaderEmail);
    }
}