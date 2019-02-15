/*
 * Java XSL code library
 *
 * Copyright (C) 2010 Benoit Mercier <info@servicelibre.com> — All rights reserved.
 *
 * This file is part of jxsl.
 *
 * jxsl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * jxsl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jxsl.  If not, see <http://www.gnu.org/licenses/>.
 */

package xsltestengine.engine;

import com.servicelibre.jxsl.dstest.XslDataSetJUnitTest;
import com.servicelibre.jxsl.dstest.XslDataSetRunner;
import com.servicelibre.jxsl.dstest.sources.DocumentSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A system test that verifies the components of the XslTestEngine framework
 * work together successfully. Uses Spring to bootstrap the application for use
 * in a test environment.
 */
@ContextConfiguration
/*
 * Note that if no file is specified to @ContextConfiguration, it is derived as
 * <classname>-context.xml, so in this case, it expects to find
 * XslTestEngineIntegrationTest-context.xml in the current directory
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class XslTestEngineIntegrationTest {

    // The object being tested
    @Autowired
    private XslDataSetRunner engine;

    @Autowired
    private File rootFolder;

    @Autowired
    private DocumentSource documentSource;

    @Test
    public void testXslTestEngine() {
        assertNotNull(rootFolder);
        assertTrue(rootFolder.exists());

        assertNotNull(documentSource);
        assertTrue(documentSource.getDocumentIds().size() > 0);

        assertNotNull(engine);


        XslDataSetJUnitTest.assertEngineRun(engine.runAll());

    }

}
