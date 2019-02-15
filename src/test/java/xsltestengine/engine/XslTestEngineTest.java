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

import com.servicelibre.jxsl.dstest.DocumentId;
import com.servicelibre.jxsl.dstest.XslDataSetJUnitTest;
import com.servicelibre.jxsl.dstest.XslDataSetRunner;
import com.servicelibre.jxsl.dstest.sources.DocumentSource;
import com.servicelibre.jxsl.dstest.sources.FolderDocumentSource;
import com.servicelibre.jxsl.dstest.validations.*;
import com.servicelibre.jxsl.scenario.XslScenario;
import com.servicelibre.jxsl.scenario.test.xspec.XspecTestScenarioRunner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class XslTestEngineTest {

    private static File rootFolder;
    private static File xspecXslGeneratorFile;
    private static File xspecFile;

    @BeforeClass
    public static void init() {

        URL rootFolderUrl = ClassLoader.getSystemResource("xsltestengine-data");
        URL xspecXslUrl = ClassLoader.getSystemResource("xspec/generate-xspec-tests.xsl");
        //URL xspecFileUrl = ClassLoader.getSystemResource("xspec/tutorial/encoding.xspec");
        URL xspecFileUrl = ClassLoader.getSystemResource("xspec/tutorial/testHref.xspec");
        try {
            rootFolder = new File(rootFolderUrl.toURI());
            xspecXslGeneratorFile = new File(xspecXslUrl.toURI());
            xspecFile = new File(xspecFileUrl.toURI());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void basicDocumentSourceTest() {

        DocumentSource docSource = getDocumentSource();
        List<DocumentId> documentIds = docSource.getDocumentIds();

        assertNotNull("documentIds cannot be null", documentIds);
        assertTrue("There must be at least one file in " + rootFolder, documentIds.size() > 0);

        for (DocumentId id : documentIds) {
            System.out.println(id);
        }

    }

    /**
     * Test the engine with on single OutputValidation (Java)
     */
    @Test
    public void basicXslTestEngine() {

        XslDataSetRunner engine = new XslDataSetRunner(getDocumentSource(), getJavaXslOutputValidation());

        XslDataSetJUnitTest.assertEngineRun(engine.runAll());

    }

    /**
     * Test the engine with several OutputValidation (Java + Xspec)
     */
    @Test
    public void completeXslTestEngine() {
        List<XslValidation> outputValidations = new ArrayList<>();

        outputValidations.add(getJavaXslOutputValidation());
        outputValidations.add(getXspecValidation());

        XslDataSetRunner engine = new XslDataSetRunner(getDocumentSource(), outputValidations);

        XslDataSetJUnitTest.assertEngineRun(engine.runAll());

    }

    public DocumentSource getDocumentSource() {
        // Create a new DocumentSource
        return new FolderDocumentSource(rootFolder, new String[]{"xml"}, true);

    }

    private XslValidation getXspecValidation() {

        XspecTestScenarioRunner xspecRunner = new XspecTestScenarioRunner(xspecXslGeneratorFile);

        return new XspecValidation(xspecRunner, xspecFile);
    }

    public XslValidation getJavaXslOutputValidation() {

        URL xslUrl = ClassLoader.getSystemResource("xsltestengine-data/toHtmlWithIds.xsl");

        XslScenario scenario = new XslScenario(xslUrl);

        List<OutputValidator> outputValidators = new ArrayList<>();
        outputValidators.add(new AlwaysTrueValidator());
        outputValidators.add(new AlwaysFalseValidator());

        return new JavaXslValidation(scenario, outputValidators);
    }

}
