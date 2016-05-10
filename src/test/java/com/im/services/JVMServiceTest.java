package com.im.services;

import com.im.beans.JVM;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JVMServiceTest {
    @Autowired JVMService jvmService;

    @Test
    public void testGetAllJVMs() {
        List<JVM> jvms = jvmService.getAllJVMs();
        Assert.assertFalse(jvms.isEmpty());
    }
}
