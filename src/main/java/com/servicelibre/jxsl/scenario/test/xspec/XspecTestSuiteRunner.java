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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicelibre.jxsl.scenario.test.TestReport;
import com.servicelibre.jxsl.scenario.test.XslTestSuiteRunner;

/**
 * * All XslScenario constructed from files/directories are using the same
 * XslScenario : only xslPath will change
 * 
 */
public class XspecTestSuiteRunner implements XslTestSuiteRunner

{
	private static final String DEFAULT_RESULTS_DIR_NAME = "XspecTestSuiteRunner";

	private static final Logger logger = LoggerFactory.getLogger(XspecTestSuiteRunner.class);

	private List<File> testFiles = new ArrayList<>();
	private List<File> testDirectoryFiles = new ArrayList<>();

	private IOFileFilter fileFilter = TrueFileFilter.TRUE;
	private IOFileFilter directoryFilter = TrueFileFilter.TRUE;

	private boolean storeResultsInSubDir = true;
	private boolean resultsSubDirWithTimeStamp = false;

	XspecTestScenarioRunner xspecRunner;

	private String resultsDirName = DEFAULT_RESULTS_DIR_NAME;
	private File outputDir = new File(System.getProperty("java.io.tmpdir"));
	private String timeStamp;
	private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss-S");

	public XspecTestSuiteRunner(File xspecTestsGeneratorFile) {
		this(new XspecTestScenarioRunner(xspecTestsGeneratorFile));
	}

	public XspecTestSuiteRunner(XspecTestScenarioRunner xspecTestRunner) {
		this.xspecRunner = xspecTestRunner;
		init();
	}

	public void init() {

		this.timeStamp = df.format(new Date());

		setFileFilter(FileFilterUtils.suffixFileFilter("xspec"));

	}

	public TestReport run(File xspecTestFile) {
		return xspecRunner.run(xspecTestFile, getOutputDir());
	}

	private File getOutputDir() {

		if (storeResultsInSubDir) {
			// All xspec results will be under a common timestamped directory
			if (resultsSubDirWithTimeStamp) {
				return new File(outputDir, this.timeStamp + "-" + resultsDirName);
			} else {
				return new File(outputDir, resultsDirName);
			}
		} else {
			// Xspec results will be stored directly under outputdir
			return outputDir;
		}
	}

	public List<TestReport> runAll() {

		List<TestReport> testReports = new ArrayList<>();

		logger.info("Starting the execution of Xspec tests...");

		for (File xspecFile : getTestFiles()) {
			testReports.add(run(xspecFile));
		}

		return testReports;
	}

	public void setFiles(List<File> files) {
		testFiles.clear();
		testFiles.addAll(files);
	}

	/**
	 * Recursively load all *.xspec files under each given directories.
	 * 
	 * @param directories
	 */
	public void setDirectories(List<File> directories) {
		testDirectoryFiles.clear();

		for (File dir : directories) {
			testDirectoryFiles.addAll(FileUtils.listFiles(dir, fileFilter, directoryFilter));
		}
	}

	public List<File> getTestFiles() {

		List<File> allFiles = new ArrayList<>(testFiles.size() + testDirectoryFiles.size());
		allFiles.addAll(testFiles);
		allFiles.addAll(testDirectoryFiles);
		return allFiles;
	}

	public IOFileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(IOFileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public IOFileFilter getDirectoryFilter() {
		return directoryFilter;
	}

	public void setDirectoryFilter(IOFileFilter directoryFilter) {
		this.directoryFilter = directoryFilter;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public String getDefaultResultsDirName() {
		return resultsDirName;
	}

	public void setDefaultResultsDirName(String resultsDirName) {
		this.resultsDirName = resultsDirName;
	}

	public boolean isResultsSubDirWithTimeStamp() {
		return resultsSubDirWithTimeStamp;
	}

	public void setResultsSubDirWithTimeStamp(boolean resultsSubDirWithTimeStamp) {
		this.resultsSubDirWithTimeStamp = resultsSubDirWithTimeStamp;
	}

	public boolean isStoreResultsInSubDir() {
		return storeResultsInSubDir;
	}

	public void setStoreResultsInSubDir(boolean storeResultsInSubDir) {
		this.storeResultsInSubDir = storeResultsInSubDir;
	}

}
