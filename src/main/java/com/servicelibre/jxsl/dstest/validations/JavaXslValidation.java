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

package com.servicelibre.jxsl.dstest.validations;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicelibre.jxsl.dstest.Document;
import com.servicelibre.jxsl.scenario.XslScenario;

public class JavaXslValidation implements XslValidation {

    static Logger logger = LoggerFactory.getLogger(JavaXslValidation.class);

    private XslScenario xslScenario;
    private List<OutputValidator> outputValidators;
    private String ouputNameToValidate;

    public JavaXslValidation(XslScenario scenario, List<OutputValidator> outputValidators) {

	this(scenario, XslScenario.MAIN_OUTPUT_KEY, outputValidators);
    }

    public JavaXslValidation(XslScenario scenario, String ouputNameToValidate, List<OutputValidator> outputValidators) {

	this.xslScenario = scenario;
	this.ouputNameToValidate = ouputNameToValidate;
	this.outputValidators = outputValidators;
    }

    public XslScenario getXslScenario() {
	return xslScenario;
    }

    public void setXslScenario(XslScenario xslScenario) {
	this.xslScenario = xslScenario;

    }

    @Override
    public ValidationReport run(Document xmlDoc) {

	ValidationReport validationReport = new ValidationReport(this, xmlDoc.id);

	logger.debug("Going to validate the transformation output of {} through {} validators...", xmlDoc.id + "[" + xslScenario + "]", outputValidators.size());

	Map<String, String> result = xslScenario.apply(xmlDoc.getFile());
	String output = result.get(XslScenario.MAIN_OUTPUT_KEY);

	for (OutputValidator outputValidator : outputValidators) {
	    logger.debug("Running validaton [{}]...", outputValidator.getName());
	    if (!outputValidator.isValid(output)) {
		logger.warn("{} - validation failed: {}", outputValidator.getName(), outputValidator.getMessage());
		ValidationFailure failure = new ValidationFailure(outputValidator.getName(), outputValidator.getMessage());
		validationReport.addValidationFailure(failure);
	    }
	}

	return validationReport;
    }

    public List<OutputValidator> getOutputValidators() {
	return outputValidators;
    }

    public void setOutputValidators(List<OutputValidator> outputValidators) {
	this.outputValidators = outputValidators;
    }

    public String getOuputNameToValidate() {
	return ouputNameToValidate;
    }

    public void setOuputNameToValidate(String ouputNameToValidate) {
	this.ouputNameToValidate = ouputNameToValidate;
    }

}
