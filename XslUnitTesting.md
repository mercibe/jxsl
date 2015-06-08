# Introduction #
jxsl offers convenient ways to run XSpec tests from Java code in order to easily integrate XSL unit testing into your Java Continuous Integration process.

Provided helper classes could be run standalone but... (to be continued)



<a href='Hidden comment: 
To enable XSL unit testing with Xspec and JUnit in your own project (build with Ant, Maven, etc.), simply create a new class in your test package that extends XspecScenarioJUnitTest. Here is a complete and functional implementation.  For other options (TestNG implementation, standalone project, etc.), please read XslUnitTesting wiki page.

```
package com.mycompany.test.xspec;

import com.servicelibre.jxsl.scenario.test.xspec.XspecScenarioJUnitTest;
import java.io.File;

public class XspecUnitTesting extends XspecScenarioJUnitTest
{
   
  public XspecUnitTesting(File xspecFile)
  {
     super(xspecFile);
  }
   
}
```
'></a>


XspecScenarioJUnitTest is configured via a Spring application context file that MUST be named **xspec-context.xml** and MUST be located at the classpath root.  This is the place where you specify the location of your XSpec files and other useful configuration options. Here is a complete configuration file.

```

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:util="http://www.springframework.org/schema/util"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
		http://www.springframework.org/schema/util
		http://www.springframework.org/schema/util/spring-util-3.0.xsd
		http://www.springframework.org/schema/context
		http://www.springframework.org/schema/context/spring-context-3.0.xsd">
<!--

  Main Spring congiguration file for XSpec unit testing 

-->

	<!-- Xspec files to process-->
	<util:list id="xspecFiles" value-type="java.io.File"
		list-class="java.util.ArrayList">
		<value>/opt/xspec/tutorial/escape-for-regex.xspec</value>
		<value>/opt/xspec/tutorial/escape-for-regex-success.xspec</value>
	</util:list>

	<!-- Xspec directories in which xspec test files will be search recursively -->
	<util:list id="xspecDirectories" value-type="java.io.File"
		list-class="java.util.ArrayList">
		<value>/opt/xspec/tutorial</value>
	</util:list>

	<!-- Main output directory -->
	<bean id="outputDir" class="java.io.File">
		<constructor-arg value="${java.io.tmpdir}/jxsl" />
	</bean>

	<!-- Xspec main XSL -->
	<bean id="xspecMainXsl" class="java.io.File">
		<constructor-arg value="/opt/xspec/generate-xspec-tests.xsl" />
	</bean>

	<!-- Xspec test report to HTML XSL -->
	<bean id="xspecReportToHTMLXsl" class="java.io.File">
		<constructor-arg value="/opt/xspec/format-xspec-report.xsl" />
	</bean>


	<!-- Mandatory plumbing -->

	<bean id="xspecTestSuiteRunner"
		class="com.servicelibre.jxsl.scenario.test.xspec.XspecTestSuiteRunner">
		<constructor-arg ref="xspecTestScenarioRunner" />
		<property name="files" ref="xspecFiles" />
		<property name="directories" ref="xspecDirectories" />
	</bean>

	<bean id="xspecTestScenarioRunner"
		class="com.servicelibre.jxsl.scenario.test.xspec.XspecTestScenarioRunner">
		<constructor-arg ref="xspecMainXsl" />
		<property name="xspecResultHtmlConvertorScenario" ref="xspecReportToHTMLScenario" />
		<property name="outputDir" ref="outputDir"/>
	</bean>

	<bean id="xspecReportToHTMLScenario" class="com.servicelibre.jxsl.scenario.XslScenario">
		<constructor-arg ref="xspecReportToHTMLXsl" />
	</bean>
	
	<!--${properties}-->
	<context:property-placeholder/>
	
</beans>


```



# Use cases #

## Junit test embedded in a Java project ##
## TestNG test embedded in a Java project ##
## No Java project - dedicated maven project ##
## No Java project - dedicated ant project ##

# Continuous Integration #