package com.softserve.edu.service.storage.impl;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import static org.junit.Assert.*;

public class FileOperationsImplTest {

    private FileOperationsImpl fileOperationsImpl = new FileOperationsImpl();

    @Test
    public void test1PutResourse() throws Exception {
        boolean actual;
        try {
            fileOperationsImpl.putResourse(null, "abc", "file.bbi");
            actual = true;
        } catch (Exception ex) {
            actual = false;
        }
        assertEquals(false, actual);
    }

    @Test
    public void testGetResourseURI() throws Exception {
        String relativeFilePath = "/rel.path";
        URI actual = fileOperationsImpl.getResourseURI(relativeFilePath);
        assertEquals("null/rel.path", actual.toString());
    }
}