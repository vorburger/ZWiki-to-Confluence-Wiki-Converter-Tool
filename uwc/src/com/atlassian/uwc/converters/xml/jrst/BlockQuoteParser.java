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
 * XML Blockquote to {quote}.
 * @author Michael Vorburger (mike@vorburger.ch)
 */
public class BlockQuoteParser extends DefaultXmlParser {

	// TODO JRst emits "nested" <block_quote>, they should be 'flattened' here...
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		appendOutput("{quote}");
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		appendOutput("{quote}\n");
	}
}
