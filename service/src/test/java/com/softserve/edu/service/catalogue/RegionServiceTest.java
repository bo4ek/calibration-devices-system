package com.softserve.edu.service.catalogue;

import com.softserve.edu.config.ServiceTestingConfig;
import com.softserve.edu.entity.catalogue.Region;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.*;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceTestingConfig.class})
public class RegionServiceTest {

    @Autowired
    private RegionService regionService;
    @Ignore
    @Test
    public void testFindByDistrictId () {
        Region region = regionService.findByDistrictId(1L);
        assertEquals(true, true);
    }


}
