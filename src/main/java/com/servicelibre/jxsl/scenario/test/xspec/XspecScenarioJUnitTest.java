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

package com.servicelibre.jxsl.scenario.test.xspec;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.servicelibre.jxsl.scenario.test.TestReport;

/**
 * JUnit test scenario for xspec files.
 * 
 * XspecScenarioJUnitTest is configured via a Spring application context file
 * that MUST be named <strong>xspec-context.xml</strong> and MUST be
 * located at the classpath root.
 * 
 * To enable XSL unit testing with Xspec in your own project (build with Ant,
 * Maven, etc.), simply create a new class in your test package that extends
 * XspecScenarioJUnitTest. Here is a complete and functional implementation.
 * 
 * <pre>
 *package com.mycompany.test.xspec;
 *
 *import com.servicelibre.jxsl.scenario.test.xspec.XspecScenarioJUnitTest;
 *import java.io.File;
 *
 *public class XspecUnitTesting extends XspecScenarioJUnitTest
 *{
 *   
 *  public XspecUnitTesting(File xspecFile)
 *  {
 *     super(xspecFile);
 *  }
 *   
 *}
 *
 * The easiest way to do is to use the Maven archetype provided by jxsl.  To learn how to
 * use the xspec-test Maven archetype please read http://code.google.com/p/jxsl/w/list  
 * 
 * </pre>
 * 
 * @author benoitm
 * 
 */
@RunWith(Parameterized.class)
public class XspecScenarioJUnitTest
{

    private static final String XSPEC_TEST_SUITE_RUNNER_BEAN_ID = "xspecTestSuiteRunner";

    private final static String SPRING_CONTEXT_FILENAME = "xspec-context.xml";

    private static XspecTestSuiteRunner xspecSuiteRunner;

    private static TestReport testReport;

    static
    {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(SPRING_CONTEXT_FILENAME);
        xspecSuiteRunner = (XspecTestSuiteRunner) ctx.getBean(XSPEC_TEST_SUITE_RUNNER_BEAN_ID);

    }

    private File xspecFile;

    public XspecScenarioJUnitTest(File xspecFile)
    {
        this.xspecFile = xspecFile;
    }

    @Test
    public void executeXspecTest()
    {
        testReport = xspecSuiteRunner.run(xspecFile);

        assertNotNull(testReport);

    }

    @Test
    public void assertXspecResults()
    {

        assertNotNull(testReport);

        if (!testReport.success)
        {
            System.err.println(testReport);
        }

        String sb = testReport.testFailedCount + " test(s) on " + testReport.testCount + " failed. " +
                "See detailed report at " + testReport.reportUrl;
        assertTrue(sb, testReport.success);

    }

    @Parameters
    public static Collection<Object[]> getXspecFiles()
    {

        List<Object[]> xspecFiles = new ArrayList<>(4);

        for (File file : xspecSuiteRunner.getTestFiles())
        {
            xspecFiles.add(new Object[] { file });
        }

        return xspecFiles;
    }

    public static XspecTestSuiteRunner getXspecSuiteRunner()
    {
        return XspecScenarioJUnitTest.xspecSuiteRunner;
    }

    public static void setXspecSuiteRunner(XspecTestSuiteRunner xspecSuiteRunner)
    {
        XspecScenarioJUnitTest.xspecSuiteRunner = xspecSuiteRunner;
    }

}
