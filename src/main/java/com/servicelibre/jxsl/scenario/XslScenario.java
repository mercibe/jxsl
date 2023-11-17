/**
 * Java XSL code library
 * <p>
 * Copyright (C) 2010 Benoit Mercier <info@servicelibre.com> — All rights reserved.
 * <p>
 * This file is part of jxsl.
 * <p>
 * jxsl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * jxsl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with jxsl.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.servicelibre.jxsl.scenario;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.lib.OutputURIResolver;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XsltTransformer;
import net.sf.saxon.trans.XsltController;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * An XSL transformation scenario, summarized as:
 *
 * <ul>
 * <li>xsl URL;</li>
 * <li>xsl parameters (optional);</li>
 * <li>xsl ouput location : where to optionaly save xsl outputs (optional,
 * default to OS temp directory);</li>
 * <li>TransformerFactory class name (optional, default to
 * net.sf.saxon.TransformerFactoryImpl).</li>
 * </ul>
 * <p>
 * XslScenario is built on top of JAXP.
 */
public class XslScenario {
    public static final String SAXON_TRANSFORMER_FACTORY_FQCN = "net.sf.saxon.TransformerFactoryImpl";
    public static final String DEFAULT_TRANSFORMER_FACTORY = SAXON_TRANSFORMER_FACTORY_FQCN;
    public static final String MAIN_OUTPUT_KEY = "output";
    private static final String OUTPUT_FILE_EXT = ".output";
    private static final String XMLSOURCE_FILENAME = "source.xml";
    private static Logger logger = LoggerFactory.getLogger(XslScenario.class);
    public int executionCount = 0;
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private String name = String.valueOf(System.identityHashCode(this));
    private String description;
    /**
     * XSL URI as string
     */
    private String xslPath;
    private Map<String, Object> parameters = new HashMap<>();
    private MultipleOutputURIResolver multipleOutputs = new MultipleOutputURIResolverImpl();
    private String mainOutputKey = MAIN_OUTPUT_KEY;
    private XMLReader reader;
    private Transformer transformer;
    private boolean saveOutputOnDisk;
    private File mainOutputDir = new File(System.getProperty("java.io.tmpdir"));
    private TransformerFactory transformerFactory;
    private RunReport lastRunReport;
    private String mainOutputName;
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss-S");
    private boolean resultsSubDirWithTimeStamp = true;
    private String timestamp;
    private DocumentBuilder xmlBuilder;

    private boolean saveRunReport;

    private boolean saveXmlSource;

    private boolean storeResultsInSubDir;

    /* Constructors */
    public XslScenario() {
        super();
        init();
    }

    /**
     * @param xslPath valid xsl URI as String
     */
    public XslScenario(String xslPath) {
        super();
        setXslPath(xslPath);
        init();
    }

    public XslScenario(URL xslUrl) {
        this(xslUrl.toString());
    }

    public XslScenario(File xslFile) {
        this(xslFile.toURI().toASCIIString());
    }

    private static String getImplementationInfo(Class<?> componentClass) {

        // TODO if Saxon, add info from productTitle. Otherwise check jar
        // manifest for Implementation-Version, Implementation-Vendor at least.
        /*
         * Name: org/apache/xalan/
         * Comment: Main Xalan engine implementing
         * TrAX/JAXP Specification-Title: Java API for XML Processing
         * Specification-Vendor: Sun Microsystems Inc.
         * Specification-Version: 1.3
         * Implementation-Title: org.apache.xalan
         * Implementation-Version: 2.7.1
         * Implementation-Vendor: Apache Software Foundation
         * Implementation-URL: http://xml.apache.org/xalan-j/
         */

        CodeSource source = componentClass.getProtectionDomain().getCodeSource();
        return MessageFormat.format("{0} [{1}]", componentClass.getName(), source == null ? "Java Runtime" : source.getLocation());
    }

    /* Business methods */
    protected void init() {

        try {
            reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            factory.setNamespaceAware(true);
            this.xmlBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Error while creating XML DocumentBuilder.", e);
        } catch (SAXException e) {
            logger.error("Error while creating XMLReader from SAXParser.", e);
        }

    }

    public Map<String, String> apply(File xmlFile) {
        try {
            return apply(FileUtils.readFileToByteArray(xmlFile), xmlFile.getAbsolutePath() + "." + System.currentTimeMillis());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return new HashMap<>();
    }

    public Map<String, String> apply(String xmlString) {
        return apply(xmlString.getBytes(), String.valueOf(System.currentTimeMillis()));
    }

    public Map<String, String> apply(byte[] xmlBytes) {
        return apply(xmlBytes, String.valueOf(System.currentTimeMillis()));
    }

    public Map<String, String> apply(String xmlString, String charsetName) {
        Map<String, String> outputs = new HashMap<>();
        try {
            outputs = apply(xmlString.getBytes(charsetName), "");
        } catch (UnsupportedEncodingException e) {
            logger.error("Error while getting bytes from xmlString supposed to be encoded in {} : {}.", charsetName, xmlString);
        }
        return outputs;
    }

    /**
     * Apply XSL transformation on XML bytes.
     * <a href="https://www.saxonica.com/html/documentation12/functions/fn/document-uri.html">document-uri</a>
     * @param xmlBytes xml bytes array
     * @param systemId xml SystemId
     * @return a Map with transformation result
     */
    public Map<String, String> apply(byte[] xmlBytes, String systemId) {

        Map<String, String> xslOutputs = new HashMap<>(1);

        Transformer transformer;

        try {
            transformer = getTransformer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (transformer == null) {
            logger.warn("No transformer available for xslPath {}", xslPath);
            return xslOutputs;
        }


        multipleOutputs.clearResults();

        xslOutputs.put(mainOutputKey, "");

        // TODO can do better, but as fast?
        if (xmlBytes.length <= 10) {
            logger.warn("No enough bytes to apply XSL on...");
            return xslOutputs;
        }

        try {

            setTimestamp(df.format(new Date()));

            logger.debug("Going to execute [{}], on a document of {} bytes", this.xslPath, xmlBytes.length);

            // Pass parameters to XSL
            for (String paramName : parameters.keySet()) {
                logger.debug("Setting up parameter {} to {}", paramName, parameters.get(paramName));
                transformer.setParameter(paramName, parameters.get(paramName));
            }

            String xmlSourceFilename = "";
            // save XML source if requested
            if (saveXmlSource) {
                File xmlSourceFile = saveXmlSourceFile(xmlBytes);
                xmlSourceFilename = xmlSourceFile.getAbsolutePath();
            }

            InputSource inputSource = new InputSource(new ByteArrayInputStream(xmlBytes));

            // To prevent error such FORG0002 (Base URI {} is not an absolute
            // URI), etc.
            inputSource.setSystemId(systemId);

            SAXSource saxSource = new SAXSource(reader, inputSource);

            StringWriter xslMainStringOutput = new StringWriter();

            logger.debug("Start execution of [{}]", this.xslPath);
            Date startDate = new Date();
            long startTime = System.nanoTime();
            transformer.transform(saxSource, new StreamResult(xslMainStringOutput));

            long executionTime = System.nanoTime() - startTime;
            logger.debug("Stop execution of [{}] ({}ms)", this.xslPath, (double) executionTime / 1000000);

            executionCount++;

            // Add main result output
            logger.debug("Storing main output (key={})", mainOutputKey);
            xslOutputs.put(mainOutputKey, xslMainStringOutput.toString());

            // Add potential other result outputs
            Map<String, StringWriter> outputs = multipleOutputs.getOutputs();
            for (String outputName : outputs.keySet()) {
                logger.debug("Storing additional output (key={})", outputName);
                xslOutputs.put(outputName, outputs.get(outputName).toString());
            }

            setLastRunReport(createRunReport(xmlSourceFilename, xslOutputs, startDate, executionTime));

        } catch (TransformerException | IllegalArgumentException e) {
            logger.error("Error during XSL transformation.", e);

        }

        return xslOutputs;

    }

    private File saveXmlSourceFile(byte[] xmlBytes) {

        File xmlSourceFile = new File(this.getCurrentOutputDir(), getName() + "_" + XMLSOURCE_FILENAME);
        try {
            FileUtils.writeByteArrayToFile(xmlSourceFile, xmlBytes);
        } catch (IOException e) {
            logger.error("Error while saving XML source file.", e);
        }

        return xmlSourceFile;
    }

    /**
     * Creation of the RunReport
     */
    private RunReport createRunReport(String xmlSourceFilename, Map<String, String> xslOutputs, Date startDate, long executionTime) {

        RunReport runReport = new RunReport();

        runReport.xslSourceUrl = this.xslPath;
        runReport.xmlSourceUrl = xmlSourceFilename;

        runReport.transformer = getTransformerFactoryFQCN();
        // Watch out! Not a deep copy... Will only work safely if all values are
        // String
        runReport.parameters = new HashMap<>(parameters);
        runReport.outputProperties = transformer.getOutputProperties();
        runReport.transformerInfo = getImplementationInfo(transformer.getClass());
        runReport.SIUnit = "ns";
        runReport.executionTime = executionTime;
        runReport.executionDate = startDate;

        File currentOuputDir = getCurrentOutputDir();

        if (saveOutputOnDisk) {
            List<File> savedOutputs = saveOutputs(currentOuputDir, xslOutputs);
            runReport.mainOutputFile = savedOutputs.get(0);
            runReport.otherOutputFiles = savedOutputs.subList(1, savedOutputs.size());
        }

        if (saveRunReport) {
            saveRunReport(currentOuputDir, runReport);
        }

        return runReport;
    }

    private List<File> saveOutputs(File currentOuputDir, Map<String, String> xslOutputs) {

        File mainOutputFile;
        File outputFile;
        List<File> outputFiles = new ArrayList<>(1);

        if (isValidOutputDir()) {

            for (String key : xslOutputs.keySet()) {
                String outputContent = xslOutputs.get(key);

                if (key.equals(getMainOutputKey())) {
                    outputFile = new File(currentOuputDir, getMainOutputName());
                    mainOutputFile = outputFile;
                    // Insert the main output at the first position
                    outputFiles.add(0, mainOutputFile);
                } else {
                    outputFile = new File(currentOuputDir, key);
                    outputFiles.add(outputFile);
                }

                logger.info("Saving output [{}] in file {}", key, outputFile.getAbsolutePath());

                try {
                    String encoding = transformer.getOutputProperty("encoding");
                    encoding = encoding == null ? "UTF-8" : encoding;

                    FileUtils.writeStringToFile(outputFile, outputContent, encoding);
                } catch (IOException e) {
                    logger.error("Error while saving XSL output {} to {}.\n" + e.getMessage(), key, outputFile);
                }
            }
        }

        return outputFiles;

    }

    private boolean isValidOutputDir() {
        if (mainOutputDir == null) {
            logger.error("outputDir is NULL. Unable to save xslOutputs created by {}", this);
            return false;
        }

        if (!mainOutputDir.isDirectory()) {
            logger.error("outputDir is not a directory : {}", mainOutputDir);
            return false;
        }
        return true;
    }

    /**
     * Save RunReport - scenario XML config file (xslPath, transformer,
     * parameters, execution time, etc.)
     */
    private void saveRunReport(File currentOuputDir, RunReport runReport) {
        if (isValidOutputDir()) {
            saveXmlDocToFile(asXml(runReport), new File(currentOuputDir, this.getName() + "_runReport.xml"));
        }
    }

    private Document asXml(RunReport runReport) {
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(runReport);
        Document reportDoc = null;

        try {
            reportDoc = xmlBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
            reportDoc.normalizeDocument();
        } catch (SAXException | IOException e) {
            logger.error("Error while converting runReport to XML", e);
        }
        return reportDoc;
    }

    private void saveXmlDocToFile(Document xmlDoc, File runReportFile) {

        TransformerFactory tf = TransformerFactory.newInstance();

        try {
            tf.newTransformer().transform(new DOMSource(xmlDoc), new StreamResult(runReportFile));
        } catch (TransformerException e) {
            logger.error("Error while saving runReport {runReportFile}", runReportFile, e);
        }

    }

    private File getCurrentOutputDir() {

        File currentOuputDir;

        if (storeResultsInSubDir) {
            if (resultsSubDirWithTimeStamp) {
                currentOuputDir = new File(mainOutputDir, getTimestamp() + "-" + getName());
            } else {
                currentOuputDir = new File(mainOutputDir, getName());
            }
        } else {
            currentOuputDir = mainOutputDir;
        }

        if (!currentOuputDir.exists() && !currentOuputDir.mkdirs()) {
            logger.error("Unable to create output directory {}.", currentOuputDir);
        }
        return currentOuputDir;
    }

    /**
     * Returns the default transformer
     *
     * @return the current Transformer
     */
    public Transformer getTransformer() {

        if (this.transformer == null) {

            if (getXslPath() == null || getXslPath().trim().isEmpty()) {
                logger.error("xslPath is NULL or empty.  Cannot create Transformer.");
                return null;
            }

            try {

                Templates compiledXsl = getCompiledXsl();
                if (compiledXsl == null) {
                    return null;
                }

                this.transformer = compiledXsl.newTransformer();

                // Saxon specific
                if ((this.transformer instanceof TransformerImpl saxonTransformer)) {

                    XsltTransformer xsltTransformer = saxonTransformer.getUnderlyingXsltTransformer();
                    XsltController saxonController = xsltTransformer.getUnderlyingController();
                    saxonController.setBaseOutputURI("");
                    saxonController.setOutputURIResolver(multipleOutputs.newInstance());
//                    // FIXME
//                    // https://www.saxonica.com/html/documentation12/using-xsl/stylesheet-output/secondary-output.html
//                     //saxonController.setOutputURIResolver(this.multipleOutputs);

                    logger.debug("Transformer used by this scenario: {}", saxonController.getConfiguration().getProductTitle());
                } else {
                    logger.debug("Transformer used by this scenario: {}", this.transformer.getClass().getName());
                }

            } catch (TransformerConfigurationException e) {
                logger.error(e.getMessage(), e);
            }
            //catch (IOException e) {
//                throw new RuntimeException(e);
//            }

        }

        return this.transformer;
    }

    public void setInitialTemplate(String prefix, String suffix, String localName) {
        Transformer transformer = getTransformer();
        if (transformer instanceof TransformerImpl saxonTransformer) {
            XsltTransformer xsltTransformer = saxonTransformer.getUnderlyingXsltTransformer();
            xsltTransformer.setInitialTemplate(new QName(prefix, suffix, localName));
        } else {
            throw new UnsupportedOperationException("Only Saxon support initialTemplate");
        }
    }

    /* Getters and Setters */

    private Templates getCompiledXsl() {
        try {
            return getTransformerFactory().newTemplates(new StreamSource(getXslPath()));
        } catch (TransformerConfigurationException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String getXslPath() {
        return xslPath;
    }

    public void setXslPath(String xslPath) {
        try {
            // Conversion to URI is done for validation purposes in order to detect problems (bad URI, encoding of special chars, etc.)
            // before the XML parser is called
            this.xslPath = new URI(xslPath).toASCIIString();
        } catch (URISyntaxException e) {
            logger.error(xslPath + " (xslPath) is not a valid URI.", e);
            return;
        }

        setMainOutputName(FilenameUtils.getBaseName(this.xslPath) + OUTPUT_FILE_EXT);

        // To force new transformer creation
        this.transformer = null;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public OutputURIResolver getOutputURIResolver() {
        return multipleOutputs;
    }

    public void setOutputURIResolver(MultipleOutputURIResolver outputURIResolver) {
        this.multipleOutputs = outputURIResolver;
    }

    public String getMainOutputKey() {
        return mainOutputKey;
    }

    public void setMainOutputKey(String mainOutputKey) {
        this.mainOutputKey = mainOutputKey;
    }

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public String getTransformerFactoryFQCN() {
        return System.getProperty("javax.xml.transform.TransformerFactory", DEFAULT_TRANSFORMER_FACTORY);
    }

    public void setTransformerFactoryFQCN(String transformerFactoryFQCN) {
        System.setProperty("javax.xml.transform.TransformerFactory", transformerFactoryFQCN);

        // To force new transformer factory creation
        transformerFactory = null;
        transformer = null;

    }

    /**
     * Return or create the TransformerFactory
     * <ol>
     * <li>via setter (Spring)</li>
     * <li>via system property</li>
     * <li>default (DEFAULT_TRANSFORMER_FACTORY)</li>
     * </ol>
     *
     * @return the current TransformerFactory
     */
    public TransformerFactory getTransformerFactory() {

        if (transformerFactory == null) {
            try {
                transformerFactory = (TransformerFactory) Class.forName(getTransformerFactoryFQCN()).getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return transformerFactory;
    }

    public void setTransformerFactory(TransformerFactory transformerFactory) {
        this.transformerFactory = transformerFactory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        // name=>xslPath(parameters)
        return name + "=>" + xslPath + "(" + parameters + ")";
    }

    public File getMainOutputDir() {
        return mainOutputDir;
    }

    public void setMainOutputDir(File mainOutputDir) {
        if (mainOutputDir == null) {
            logger.error("outputDir is null");
            return;
        } else if (!mainOutputDir.exists()) {
            logger.info("Creation of output directory {}.", mainOutputDir);
            mainOutputDir.mkdirs();
        }

        this.mainOutputDir = mainOutputDir;
    }

    public boolean getSaveOutputOnDisk() {
        return saveOutputOnDisk;
    }

    public void setSaveOutputOnDisk(boolean saveOutputOnDisk) {
        this.saveOutputOnDisk = saveOutputOnDisk;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    public String getMainOutputName() {
        return mainOutputName;
    }

    public void setMainOutputName(String mainOutputName) {
        this.mainOutputName = mainOutputName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public RunReport getLastRunReport() {
        return lastRunReport;
    }

    public void setLastRunReport(RunReport lastRunReport) {
        this.lastRunReport = lastRunReport;
    }

    public boolean getSaveRunReport() {
        return saveRunReport;
    }

    public void setSaveRunReport(boolean saveRunReport) {
        this.saveRunReport = saveRunReport;
    }

    public boolean getStoreResultsInSubDir() {
        return storeResultsInSubDir;
    }

    public void setStoreResultsInSubDir(boolean storeResultsInSubDir) {
        this.storeResultsInSubDir = storeResultsInSubDir;
    }

    public boolean isSaveXmlSource() {
        return saveXmlSource;
    }

    public void setSaveXmlSource(boolean saveXmlSource) {
        this.saveXmlSource = saveXmlSource;
    }

    public boolean isResultsSubDirWithTimeStamp() {
        return resultsSubDirWithTimeStamp;
    }

    public void setResultsSubDirWithTimeStamp(boolean resultsSubDirWithTimeStamp) {
        this.resultsSubDirWithTimeStamp = resultsSubDirWithTimeStamp;
    }

}