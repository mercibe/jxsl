# Introduction #

jxsl goals are :

  * to provide a very simple API to run XSL transformation from Java code, on top of [JAXP](http://jaxp.java.net/);
  * to foster the adoption of XSL Unit testing and integration into Continuous Integration processes, on top of [XSpec](http://code.google.com/p/xspec/).

# Simple API to run XSLT from Java code #

Applying an XSL on XML content might be as simple as

```
// Create a transformation scenario
XslScenario sc = new XslScenario("path_to_your_xsl.xsl");

// Run this scenario on XML content
 Map<String, String> outputs = sc.apply("xml_content");

// Display transformation result
System.out.println(outputs.get(XslScenario.MAIN_OUTPUT_KEY));
```

where:

  * `"path_to_your_xsl.xsl"` can be a `File`, `URL` or `String`
  * `"xml_content"` can be `byte[]`, `File` or `String`

XslScenario object lets you easily manage XSL parameters and multiple outputs.  See XslScenario page for more information.

# XSL Unit testing #

Once you have written [XSpec](http://code.google.com/p/xspec/) test scenarios, jxsl allows you to quickly integrate them into your build or continous integration processes.

jxsl comes with a [Maven archetype](http://maven.apache.org/archetype/maven-archetype-plugin/) to help you start quickly.  [Ant](http://ant.apache.org/) support is coming soon.  To make it short:

  1. First, you need to install [Maven](http://maven.apache.org/) and have [Saxon](http://saxon.sourceforge.net/) [9.3.0.2j](https://sourceforge.net/projects/saxon/files/Saxon-HE/9.3/saxonhe9-3-0-2j.zip/download) library deployed in your local Maven repository.
  1. Generate the archetype with the following code
```
mvn archetype:generate -DarchetypeGroupId=com.servicelibre -DarchetypeArtifactId=xspec-test -DarchetypeVersion=0.1.11 -DarchetypeCatalog=http://jxsl.googlecode.com/svn/trunk/archetypes
```
  1. Test your new XSL test project.
```
mvn test
```
  1. Specify the location of your XSpec files by editing **src/test/resources/xspec-context.xml**
  1. Run your own tests
```
mvn test
```
  1. Add your new project to your source control system ([Subversion](http://subversion.tigris.org/), [Git](http://git-scm.com/), [Mercurial](http://mercurial.selenic.com/), etc.) and link it to your Continuous Integration server ([Hudson](http://hudson-ci.org/), [TeamCity](http://www.jetbrains.com/teamcity/), [CruiseControl](http://cruisecontrol.sourceforge.net/), [Mike](http://mikeci.com/), etc. )

For detailed setup instructions, read XslUnitTesting page.

Happy XSL testing!