package config;

import com.softserve.edu.config.ServiceTestingConfig;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {ServiceTestingConfig.class})
public class InitializerTestingDB {
    /**
     * Run this test for initialization Testing DB
     */
    @Test
    public void testInitializer()  {
        Assert.assertTrue(true);
    }

}
