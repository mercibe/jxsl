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

package com.servicelibre.jxsl.dstest.sources;

import com.servicelibre.jxsl.dstest.Document;
import com.servicelibre.jxsl.dstest.DocumentId;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FolderDocumentSource  implements DocumentSource {

	private File rootDir;
	private boolean recursive;
	private String[] extensions;
	
	
	public FolderDocumentSource(File rootFolder) {
		this(rootFolder, null, false);
	}
	

	public FolderDocumentSource(File rootFolder, String[] extensions, boolean recursive) {
		this.rootDir = rootFolder;
		this.extensions = extensions;
		this.recursive = recursive;
	}


	@Override
	public List<DocumentId> getDocumentIds() {
		
		List<DocumentId> documentIds = new ArrayList<>();
		
		Collection<File> files = FileUtils.listFiles(rootDir, extensions, recursive);

		for (File file : files) {

			documentIds.add(new DocumentId(file.getAbsolutePath()));
		}
		
		return documentIds;
	}

	@Override
	public Document getDocument(DocumentId docId) {
		return new Document(docId, new File(docId.getId()));
	}

	public File getrootDir() {
		return rootDir;
	}

	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}


	public String[] getExtensions() {
		return extensions;
	}


	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}
	
	

}
