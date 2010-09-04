/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.atlassian.uwc.converters.xml.jrst;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.xml.sax.SAXException;

/**
 * Smarter ReferenceParser which handles Plone zWiki "schizophrenic" links
 * (because a page effectively has pages IDs, the Page name which typically has
 * spaces and an internal automatically constructed version without spaces, and
 * both are allowed in links!) better.
 * 
 * @see http://mifosforge.jira.com/browse/MIFOSADMIN-55
 * 
 * @author Michael Vorburger (mike@vorburger.ch)
 */
public class ReferenceParserSmart extends ReferenceParser {

	private static final String BADLINK_LABEL = "tbd_checklink";
	private Properties pageId2Name;
	private Properties pageName2Id;
	
	public ReferenceParserSmart() throws SAXException {
		super();
		loadPageMappings();
	}

	private File getPropertiesDir() {
		// TODO Should not be hard-coded but configured in e.g. UWC's converter.zwiki-mifos.properties, but how?
		return new File("/home/vorburger/dev/WikiStuff/ZWiki-to-Confluence-Wiki-Converter-Tool.git/zwikiscraper/target/wikiContent");
	}

	
	@Override
	protected String checkAndFixLink(final String link) throws SAXException {
		if (link.contains("://") || link.startsWith("mailto:")) {
			// Aha, it's an absolute external link (e.g. http://...), or email, so let's buzz off
			return link;
		}
		
		if (link.contains("/")) {
			getPage().addLabel(BADLINK_LABEL);
			log.warn("Page '" + getPage().getName() + "' contains probably invalid non-Wiki Plone CMS link '" + link + "'... a label '" + BADLINK_LABEL + " ' has been added to it for you to manually fix it");
			return link;
		}
		
		String linkWithoutAnchor;
		String anchor;
		int idx = link.indexOf('#');
		if (idx > -1) {
			linkWithoutAnchor = link.substring(0, idx);
			anchor = link.substring(idx);
		} else {
			linkWithoutAnchor = link;
			anchor = "";
		}
		
		if (getPageName2IdMapping().containsKey(linkWithoutAnchor)) {
			return link; // OK so all iz wehl" here...
		} else if (getPageId2NameMapping().containsKey(linkWithoutAnchor)) {
			// Duh, so it's actually a Page ID not a Name, let's swap
			return getPageId2NameMapping().getProperty(linkWithoutAnchor) + anchor;
		} else {
			getPage().addLabel(BADLINK_LABEL);
			// Hm, it's neither... something is fishy?
			log.warn("Page '" + getPage().getName() + "' contains a broken link '" + link + "' (which is neither a Plone Wiki  Page Name nor a Page ID!) ... a label '" + BADLINK_LABEL + " ' has been added to it for you to manually fix it");
			return link;
		}
	}

	
	
	private void loadPageMappings() throws SAXException {
		try {
			this.pageId2Name = loadProperties("pageId2Name.properties");
			this.pageName2Id = loadProperties("pageName2Id.properties");
		} catch (IOException e) {
			throw new SAXException("Could not load Plone zWiki Page Name/ID mapping properties", e);
		}
	}
	
	private Properties loadProperties(String fileName) throws IOException {
		Properties p = new Properties();
	    Reader reader = new FileReader(new File(getPropertiesDir(), fileName));
		p.load(reader);
		reader.close();
		return p;
	}

	private Properties getPageId2NameMapping() throws SAXException {
		if (pageId2Name == null) {
			loadPageMappings();
		}
		return pageId2Name;
	}

	private Properties getPageName2IdMapping() throws SAXException  {
		if (pageName2Id == null) {
			loadPageMappings();
		}
		return pageName2Id;
	}

}
