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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Reference to [ConfluenceLink].
 * 
 * @author Michael Vorburger (mike@vorburger.ch)
 */
public class ReferenceParser extends AbstractElementContentParser {

	static final String REFURI = "refuri";
	static final String LINK = "link";
	static final String NAME = "name";

	@Override
	protected void fullElement(String uri, String localName, String qName, Attributes attributes, String content) throws SAXException {
		String name = attributes.getValue(NAME);
		if (name == null) {
			name = content;
		}
		if (name.startsWith("!")) {
			// Plone zWiki RST links sometimes start with a '!'
			// (for {nolink} ?) which makes no sense in Confluence (links)
			name = name.substring(1);
		}
		
		String url = attributes.getValue(LINK);
		if (url == null) {
			url = attributes.getValue(REFURI);
		}

		appendOutput("[");
		if (url != null && !url.equals(name)) {
			appendOutput(name);
			appendOutput("|");
			url = checkAndFixLink(url);
			appendOutput(url);
		} else {
			name = checkAndFixLink(name);
			appendOutput(name);
		}
		appendOutput("]");
	}
	
	/**
	 * Hook for subclasses to check a link target, and fix it if necessary.
	 * 
	 * @param link original link as found in incoming XML
	 * @return same link, or fixed version of it
	 */
	protected String checkAndFixLink(String link) throws SAXException {
		return link;
	}
}
