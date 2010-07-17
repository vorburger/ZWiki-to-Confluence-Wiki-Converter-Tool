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

import com.atlassian.uwc.converters.xml.DefaultXmlParser;

/**
 * Abstract base class for XmlParser implementation which need to treat element content.
 * 
 * @author Michael Vorburger (mike@vorburger.ch)
 */
public abstract class AbstractElementContentParser extends DefaultXmlParser {

	private Attributes attributes;
	private String content;
	
	protected void clearFields() {
		content = null;
		attributes = null;
	}
	
	abstract protected void fullElement(String uri, String localName, String qName, Attributes attributes, String content, StringBuilder confluenceMarkup);
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		this.attributes = attributes;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		StringBuilder confluenceMarkup = new StringBuilder();
		fullElement(uri, localName, qName, attributes, content, confluenceMarkup);
		appendOutput(confluenceMarkup.toString());
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content = String.copyValueOf(ch, start, length);
	}
}
