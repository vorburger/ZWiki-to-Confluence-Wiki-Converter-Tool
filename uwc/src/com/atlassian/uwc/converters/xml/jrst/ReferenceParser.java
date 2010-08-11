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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.xml.sax.Attributes;

/**
 * Reference to [ConfluenceLink].
 * 
 * @author Michael Vorburger (mike@vorburger.ch)
 */
public class ReferenceParser extends AbstractElementContentParser {

	@Override
	protected void fullElement(String uri, String localName, String qName, Attributes attributes, String content) {
		String name = attributes.getValue("name");
		if (name == null) {
			name = content;
		}
		if (name.startsWith("!")) {
			// Plone zWiki RST links sometimes start with a '!'
			// (for {nolink} ?) which makes no sense in Confluence (links)
			name = name.substring(1);
		}
		
		String url = attributes.getValue("link");
		if (url == null) {
			url = attributes.getValue("refuri");
		}

		appendOutput("[");
		appendOutput(name);
		if (url != null && !url.equals(name)) {
			appendOutput("|");
			appendOutput(url);
		}
		appendOutput("]");
		
	}
}
