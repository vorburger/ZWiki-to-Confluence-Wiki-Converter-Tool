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

import java.util.regex.Matcher;

import org.xml.sax.Attributes;

import com.atlassian.uwc.converters.xml.DefaultXmlParser;

/**
 * Uses a tag's attribute named 'realLevel' to set the h1. / h2. etc. headers.
 * @author Michael Vorburger (mike@vorburger.ch)
 */
public class HeaderParser extends DefaultXmlParser {

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		appendOutput("h" + attributes.getValue("realLevel") + ".");
	}
}
