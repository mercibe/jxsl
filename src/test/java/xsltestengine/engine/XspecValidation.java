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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicelibre.jxsl.dstest.Document;
import com.servicelibre.jxsl.dstest.validations.ValidationReport;
import com.servicelibre.jxsl.dstest.validations.XslValidation;
import com.servicelibre.jxsl.scenario.test.TestReport;
import com.servicelibre.jxsl.scenario.test.xspec.XspecTestScenarioRunner;

/**
 * @author benoitm
 * 
 */
public class XspecValidation implements XslValidation {

    static Logger logger = LoggerFactory.getLogger(XspecValidation.class);

    private XspecTestScenarioRunner xspecRunner;
    private File xspecFile;

    public XspecValidation(XspecTestScenarioRunner xspecRunner, File xspecFile) {
	super();
	this.xspecRunner = xspecRunner;
	this.xspecFile = xspecFile;
    }

    @Override
    public ValidationReport run(Document xmlDoc) {

	ValidationReport validationReport = new ValidationReport(this, xmlDoc.id);

	logger.debug("Going to run Xspec test on {} [{}]", xmlDoc.id, xspecFile);

	TestReport testReport = xspecRunner.run(xspecFile, xmlDoc);

	logger.debug("Validation result: {}",testReport.success?"SUCCESS":"FAILURE");

	return validationReport;
    }

    public XspecTestScenarioRunner getXspecRunner() {
	return xspecRunner;
    }

    public void setXspecRunner(XspecTestScenarioRunner xspecRunner) {
	this.xspecRunner = xspecRunner;
    }

}
