/*
 * Copyright 2014 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.technosf.posterer.modules.commons.config;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.github.technosf.posterer.models.impl.base.AbstractPropertiesModel;
import com.github.technosf.posterer.models.impl.base.AbstractPropertiesModelAbstractTest;
import com.github.technosf.posterer.modules.commons.config.CommonsConfiguratorPropertiesImpl;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class CommonsConfiguratorPropertiesImplTest
        extends AbstractPropertiesModelAbstractTest
{

    private CommonsConfiguratorPropertiesImpl classUnderTest;

    private final String prefix = Long.toString(System.currentTimeMillis())
            + ".";


    @Override
    protected AbstractPropertiesModel getClassUnderTest()
    {
        return classUnderTest;
    }


    /* ------------------ Test Setup and Teardown -------------------- */

    /**
     * Create a new CUT that reads from the file for every test.
     * 
     * @throws ConfigurationException
     * @throws IOException
     */
    @BeforeTest
    public final void beforeTest() throws ConfigurationException, IOException
    {
        classUnderTest =
                new CommonsConfiguratorPropertiesImpl(prefix);
    }


    /**
     * Delete the CUT
     */
    @AfterClass
    public final void afterClass() throws IOException
    {
        FileUtils.deleteQuietly(getFile(classUnderTest.pathPropsFile()));
    }


    /* ------------------ Tests -------------------- */

    @Test
    public final void getBasicPropertiesFileContent() throws IOException
    {
        assertEquals(CommonsConfiguratorPropertiesImpl.blankfile,
                readFileToString(getFile(classUnderTest.pathPropsFile())));
    }
}
