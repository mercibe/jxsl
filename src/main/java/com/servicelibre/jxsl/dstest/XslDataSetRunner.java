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

package com.servicelibre.jxsl.dstest;

import com.servicelibre.jxsl.dstest.sources.DocumentSource;
import com.servicelibre.jxsl.dstest.validations.ValidationReport;
import com.servicelibre.jxsl.dstest.validations.XslValidation;
import com.servicelibre.jxsl.scenario.XslScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class XslDataSetRunner {

    static Logger logger = LoggerFactory.getLogger(XslScenario.class);

    private DocumentSource docSource;

    private List<XslValidation> xslOutputValidations;

    public XslDataSetRunner() {
        super();
    }

    public XslDataSetRunner(DocumentSource docSource, XslValidation xslOutputValidation) {
        super();
        this.docSource = docSource;
        xslOutputValidations = new ArrayList<>(1);
        xslOutputValidations.add(xslOutputValidation);
    }

    public XslDataSetRunner(DocumentSource docSource, List<XslValidation> xslOutputValidations) {
        super();
        this.docSource = docSource;
        this.xslOutputValidations = xslOutputValidations;
    }

    public DocumentSource getDocSource() {
        return docSource;
    }

    public void setDocSource(DocumentSource docSource) {
        this.docSource = docSource;
    }

    public List<ValidationReport> run(DocumentId documentId) {

        List<ValidationReport> validationReports = new ArrayList<>();

        if (docSource != null) {

            if (xslOutputValidations != null) {

                for (XslValidation outputValidation : xslOutputValidations) {
                    // TODO Save document if requested (saveDocuments)

                    validationReports.add(outputValidation.run(docSource.getDocument(documentId)));

                    // TODO append reports to csv output report (saveOutputs - All/ErrorOnly)
                }
            } else {
                logger.warn("There is no outputValidator available for this XslOutputValidation.");
            }

        } else {
            logger.error("DocumentSource is NULL!");
        }

        return validationReports;

    }

    public List<ValidationReport> runAll() {
        List<ValidationReport> validationReports = new ArrayList<>();

        // Get all files Ids to process
        if (docSource != null) {

            if (xslOutputValidations != null) {

                for (DocumentId documentId : docSource.getDocumentIds()) {

                    validationReports.addAll(run(documentId));

                }
            } else {
                logger.warn("There is no outputValidator available for this XslOutputValidation.");
            }

        } else {
            logger.error("DocumentSource is NULL!");
        }

        return validationReports;
    }

    public List<XslValidation> getXslOutputValidations() {
        return xslOutputValidations;
    }

    public void setXslOutputValidations(List<XslValidation> xslOutputValidations) {
        this.xslOutputValidations = xslOutputValidations;
    }

}
