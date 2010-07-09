/*
 * Copyright 2010 Michael Vorburger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.vorburger.mifos.wiki;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.nuiton.jrst.JRST;
import org.nuiton.jrst.JRST.Overwrite;

/**
 * Converts Plone ZWiki reStructured Text file dump (produced by the ZWikiScraper) into XML, using JRst.
 * 
 * @see http://mifosforge.jira.com/wiki/display/MIFOSADMIN/ZWiki+to+Confluence+Wiki+Converter+Tool
 * @author Michael Vorburger
 */
public class ZWikiJRstConverter {

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		ZWikiJRstConverter conv = new ZWikiJRstConverter();
		conv.convertPages(new File("target/wikiContent/"));
		long endTime = System.currentTimeMillis();
		System.out.println("\n\nConversion skipped (not attempted) for the following input pages :");
		for (File skippedPageName : conv.skippedPageNames) {
			System.out.println(skippedPageName);
		}
		System.out.println("\n\nConversion failed for the following input pages (see above for detailed exceptions) :");
		for (File failedPageName : conv.failedPageNames) {
			System.out.println(failedPageName);
		}
		System.out.println("\n\nConversion from reStructuredText to XML took " + ((endTime - startTime) / 1000) + "s"); 
		System.out.println("\n\nConverted " + conv.successfulPages
				+ " sucessfully, but skipped " + conv.skippedPageNames.size() + " and failed " + conv.failedPages 
				+ " (total " + (conv.successfulPages + conv.failedPages + conv.skippedPageNames.size() ) + ")");
	}
	
	int successfulPages;
	int failedPages;
	List<File> failedPageNames = new LinkedList<File>();
	List<File> skippedPageNames = new LinkedList<File>();
	
	public void convertPages(File rootDir) throws Exception {
		File[] files = rootDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				convertPages(file);
			} else if (file.isFile()) {
				if (file.getName().endsWith(".rst")) {
					System.out.print((successfulPages + failedPages + 1) + ". ");
					System.out.println(file.getCanonicalPath());
					convertPage(file);
				} else if (file.getName().endsWith(".xml")) {
					// Ignore
				} else if (file.getName().endsWith(".properties")) {
					// Ignore
				} else if (file.getName().startsWith("SKIP--")) {
					// Ignore
					skippedPageNames.add(file);
				} else {
					throw new IllegalArgumentException(file.getCanonicalPath() + " is not a content$[xml], what is it and what am I supposed to do with it?!");
				}
			}
		}
	}

	public boolean convertPage(File inputFile) throws Exception {
		File outputFile = new File(inputFile.getParentFile(), inputFile.getName().replace(".rst", "") + ".xml");
		try {
			JRST.generate("xml", inputFile, "UTF-8", outputFile, "UTF-8", Overwrite.ALLTIME);
			++successfulPages;
			return true;
		} catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
			++failedPages;
			failedPageNames.add(inputFile);
			//
			// TODO Save SOME *.xml anyways, may be just something which will lead to {code}...rst...{code} in Confluence?  Or simply do that manually?? 
			//
			return false;
		}
	}

}
