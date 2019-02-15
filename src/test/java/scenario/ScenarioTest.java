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

package scenario;

import com.servicelibre.jxsl.scenario.RunReport;
import com.servicelibre.jxsl.scenario.XslScenario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:ScenarioTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ScenarioTest {

    @javax.annotation.Resource
    private Map<String, Resource> testResources;

    @Test
    public void basicScenarioXslt2() throws IOException {
        System.setProperty("javax.xml.transform.TransformerFactory", XslScenario.SAXON_TRANSFORMER_FACTORY_FQCN);
        runBasicScenarioOn(testResources.get("test01-xslt2.xsl").getURL().toExternalForm());
    }

    private XslScenario runBasicScenarioOn(String xslPath) throws IOException {
        File xmlFile = new File(testResources.get("test01.xml").getURI());

        XslScenario sc = new XslScenario();
//        sc.setSaveOutputOnDisk(true);
//        sc.setSaveRunReport(true);
//        sc.setSubDirTimeStamp(true);
//        sc.setSaveXmlSource(true);

        assertNull("xslPath MUST be null.", sc.getXslPath());
        assertNotNull("OutputURIResolver cannot be null.", sc.getOutputURIResolver());
        assertNotNull("Parameters cannot be null.", sc.getParameters());
        assertEquals("There should be no parameter at this stage.", 0, sc.getParameters().size());

        sc.setXslPath(xslPath);
        Map<String, String> outputs = sc.apply(xmlFile);

        assertNotNull("outputs object cannot be null.", outputs);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>falseBenoit Mercier,Géraldine Westerkamp,",
                outputs.get(XslScenario.MAIN_OUTPUT_KEY));

        sc.setParameter("capitalize", true);
        outputs = sc.apply(xmlFile);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>trueBenoit MERCIER,Géraldine WESTERKAMP,",
                outputs.get(XslScenario.MAIN_OUTPUT_KEY));

        outputs = sc.apply(xmlFile);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>trueBenoit MERCIER,Géraldine WESTERKAMP,",
                outputs.get(XslScenario.MAIN_OUTPUT_KEY));

        sc.setParameter("capitalize", false);
        outputs = sc.apply(xmlFile);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>falseBenoit Mercier,Géraldine Westerkamp,",
                outputs.get(XslScenario.MAIN_OUTPUT_KEY));

        sc.setName("Basic scenario for test purposes");
        sc.setParameter("nonexistentParameter", null);
        System.out.println("Test toString() - end of test with " + sc);

        return sc;

    }

    @Test
    public void outputNameTest() throws IOException {

        XslScenario sc = new XslScenario(testResources.get("test01-xslt2.xsl").getURL());
        assertEquals("test01-xslt2.output", sc.getMainOutputName());

        File xmlFile = new File(testResources.get("test01.xml").getURI());
        sc.setSaveOutputOnDisk(true);

        sc.apply(xmlFile);
        RunReport lastRunReport = sc.getLastRunReport();
        assertNotNull("lastRunReport cannot be null", lastRunReport);
        assertNotNull("mainOutputFile cannot be null is resultsPersistedOnDisk == true", lastRunReport.mainOutputFile);
        assertTrue("mainOuputFile MUST exists is resultsPersistedOnDisk == true", lastRunReport.mainOutputFile.exists());

    }

    /*
     * We should test:
     *
     * - xsl with 2+ outputs: with params for href result-document, with and
     * without SL MultipleOutputURIResolver (are files saved as XSL wish?) -
     * with outputDir
     * test output on disk
     * sc.setSaveOutputOnDisk(true);
     */

}
