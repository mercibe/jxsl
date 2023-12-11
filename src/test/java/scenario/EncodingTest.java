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

package scenario;

import com.servicelibre.jxsl.scenario.XslScenario;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Formatter;
import java.util.Map;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath:ScenarioTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class EncodingTest {

    @jakarta.annotation.Resource
    private Map<String, Resource> testResources;

    @Test
    public void utf16EncodingFromFileTest() throws IOException {

        System.setProperty("javax.xml.transform.TransformerFactory", XslScenario.SAXON_TRANSFORMER_FACTORY_FQCN);

        File xmlFile = new File(testResources.get("utf16-doc.xml").getURI());
        File xslFile = new File(testResources.get("encoding.xsl").getURI());

        XslScenario sc = new XslScenario(xslFile);

        Map<String, String> outputs = sc.apply(xmlFile);
        assertNotNull("outputs object cannot be null.", outputs);
        assertTrue("Output cannot be empty.", outputs.get(XslScenario.MAIN_OUTPUT_KEY).length() > 0);

    }

    @Test
    public void utf16EncodingFromBytesTest() throws IOException {

        System.setProperty("javax.xml.transform.TransformerFactory", XslScenario.SAXON_TRANSFORMER_FACTORY_FQCN);

        File xmlFile = new File(testResources.get("utf16-doc.xml").getURI());
        File xslFile = new File(testResources.get("encoding.xsl").getURI());

        XslScenario sc = new XslScenario(xslFile);

        byte[] bytes = FileUtils.readFileToByteArray(xmlFile);
        for (byte b : bytes) {
            System.out.format("%02x ", b);
        }

        System.out.println();

        // BOM UTF-16LE
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);

        formatter.format("%02x", bytes[0]);
        assertEquals("ff", sb.toString());

        sb.delete(0, 2);

        formatter.format("%02x", bytes[1]);
        assertEquals("fe", sb.toString());

        Map<String, String> outputs = sc.apply(bytes);
        assertNotNull("outputs object cannot be null.", outputs);

    }

    @Test
    public void utf16EncodingFromBytes2Test() throws IOException {

        System.setProperty("javax.xml.transform.TransformerFactory", XslScenario.SAXON_TRANSFORMER_FACTORY_FQCN);

        File xmlFile = new File(testResources.get("utf16-doc.xml").getURI());
        File xslFile = new File(testResources.get("encoding.xsl").getURI());

        XslScenario sc = new XslScenario(xslFile);

        byte[] bytes = FileUtils.readFileToByteArray(xmlFile);
        Map<String, String> outputs = sc.apply(bytes);
        assertNotNull("outputs object cannot be null.", outputs);

        // 2e exécution
        outputs = sc.apply(bytes);
        assertNotNull("outputs object cannot be null.", outputs);

    }

    @Test
    public void filenameWithSpecialChar() throws IOException {

        File xmlFile = new File(testResources.get("hôtel.xml").getURI());
        File xslFile = new File(testResources.get("hôtel.xsl").getURI());

        assertNotNull(xmlFile);
        assertTrue(xmlFile.exists());

        XslScenario sc = new XslScenario(xslFile.getCanonicalPath());
        Map<String, String> outputs = sc.apply(xmlFile);
        assertNotNull("outputs object cannot be null.", outputs);
        assertTrue("Output cannot be empty.", outputs.get(XslScenario.MAIN_OUTPUT_KEY).length() > 0);
        System.err.println(outputs.get(XslScenario.MAIN_OUTPUT_KEY));

    }

    public void encodingExploration() {

        final String bomChar = "\uFEFF";

        // Unicode encodings
        String[] unicodeEncodings = {"UTF-8", "UTF-16BE", "UTF-16LE", "UTF-32BE", "UTF-32LE"};

        // Print the byte order marks
        for (String encName : unicodeEncodings) {
            Charset charset = Charset.forName(encName);
            byte[] byteOrderMark = bomChar.getBytes(charset);
            System.out.format("%10s BOM: ", charset.toString());
            for (byte b : byteOrderMark) {
                System.out.format("%02x ", b);
            }
            System.out.println();
        }

        String string = "hello";
        Charset charset = Charset.forName("UTF-16");
        byte[] encodedBytes = string.getBytes(charset);

        System.out.format("%s %10s encoded: ", string, charset.toString());
        for (byte b : encodedBytes) {
            System.out.format("%02x ", b);
        }
        System.out.println("\n\n");

        System.out.println("byte[] bytesUTF8 = {0x65, (byte) 0xc3, (byte) 0xa9}; => eé\n\n");
        byte[] bytesUTF8 = {0x65, (byte) 0xc3, (byte) 0xa9};
        try {
            String stringUTF8 = new String(bytesUTF8, "UTF-8");
            System.out.println("write UTF8 bytes to string - read UTF8 bytes from string: " + stringUTF8);
            printBytes(stringUTF8.getBytes("UTF-8"));
            System.out.println("write UTF8 bytes to string - read UTF16 bytes from string: " + stringUTF8);
            printBytes(stringUTF8.getBytes("UTF-16"));

            String wrongStringUTF16 = new String(bytesUTF8, "UTF-16");

            // FF FD: REPLACEMENT CHARACTER: used to replace an incoming
            // character whose value is unknown or unrepresentable in Unicode.
            System.out.println("write wrong UTF-16 bytes to string - read UTF-16 bytes from string: " + wrongStringUTF16);
            printBytes(wrongStringUTF16.getBytes("UTF-16"));

            // EF BF BD = UTF-8 encoding of code point U+FFFD (REPLACEMENT
            // CHARACTER)
            System.out.println("write wrong UTF-16 bytes to string - read UTF-8 bytes from string: " + wrongStringUTF16);
            printBytes(wrongStringUTF16.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("\n\nbyte[] bytesUTF16 = {0x00, 0x65, 0x00,(byte) 0xe9}; => eé\n\n");
        byte[] bytesUTF16 = {0x00, 0x65, 0x00, (byte) 0xe9};
        try {
            String stringUTF16 = new String(bytesUTF16, "UTF-16");
            System.out.println("write UTF-16 bytes to string - read UTF16 bytes from string: [" + stringUTF16 + "] length=" + stringUTF16.length());
            printBytes(stringUTF16.getBytes("UTF-16"));
            System.out.println("write UTF-16 bytes to string - read UTF-8 bytes from string: [" + stringUTF16 + "] length=" + stringUTF16.length());
            printBytes(stringUTF16.getBytes("UTF-8"));

            String wrongStringUTF8 = new String(bytesUTF16, "UTF-8");
            System.out.println("write wrong UTF-8 bytes to string - read UTF-16 bytes from string: [CTRL CHAR] length=" + wrongStringUTF8.length());
            printBytes(wrongStringUTF8.getBytes("UTF-16"));
            System.out.println("write wrong UTF-8 bytes to string - read UTF-8 bytes from string: [CTRL CHAR] length=" + wrongStringUTF8.length());
            printBytes(wrongStringUTF8.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void printBytes(byte[] bytes) {
        for (byte b : bytes) {
            System.out.format("%02x ", b);
        }
        System.out.println();
    }

}
