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

package xsltestengine.source;

import com.servicelibre.jxsl.dstest.sources.FolderDocumentSource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the FolderDocumentSource class that verify
 * FolderDocumentSource behavior works in isolation.
 */
public class FolderDocumentSourceTest {
    private static File rootFolder;
    // The object to test
    private FolderDocumentSource source = new FolderDocumentSource(rootFolder, new String[]{"xml"}, true);

    @BeforeClass
    public static void init() {
        URL rootFolderUrl = ClassLoader.getSystemResource("xsltestengine");

        try {
            rootFolder = new File(rootFolderUrl.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sourceRootDirIsValid() {
        File dir = source.getrootDir();
        assertNotNull(dir);
        assertTrue(dir.exists());

    }

}
