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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.servicelibre.jxsl.dstest.validations.ValidationFailure;
import com.servicelibre.jxsl.dstest.validations.ValidationReport;

@RunWith(Parameterized.class)
public class XslDataSetJUnitTest {
	private static final String XSL_DATASET_RUNNER_BEAN_ID = "xslDataSetRunner";

	public final static String SPRING_CONTEXT_FILENAME = "xsldataset-context.xml";

	private static XslDataSetRunner runner;

	static {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(SPRING_CONTEXT_FILENAME);
		runner = (XslDataSetRunner) ctx.getBean(XSL_DATASET_RUNNER_BEAN_ID);
	}

	protected DocumentId documentId;

	public XslDataSetJUnitTest(DocumentId documentId) {
		super();
		this.documentId = documentId;
	}

	@Parameters
	public static Collection<Object[]> getDocuments() {

		List<Object[]> documents = new ArrayList<>(4);

		for (DocumentId docId : runner.getDocSource().getDocumentIds()) {
			documents.add(new Object[] { docId });
		}

		return documents;
	}

	@Test
	public void dataSetValidationTest() {

		assertEngineRun(runner.run(documentId));

	}

	public static void assertEngineRun(List<ValidationReport> validationReports) {

		boolean errors = false;

		assertNotNull(validationReports);

		for (ValidationReport report : validationReports) {
			DocumentId documentId = report.getDocumentId();
			assertNotNull(documentId);
			List<ValidationFailure> failures = report.getFailures();
			assertNotNull(failures);
			if (failures.size() > 0) {
				System.out.println("Some output validators failed for " + documentId);

				for (ValidationFailure failure : failures) {
					System.out.println(documentId + " => " + failure.getValidatorName() + ": " + failure.getMessage());
					errors = true;
				}

			}
		}
		assertTrue(errors);

	}

}
