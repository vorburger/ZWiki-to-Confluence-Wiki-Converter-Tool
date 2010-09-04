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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.NotImplementedException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Unit test for ReferenceParserSmart.
 * 
 * @author Michael Vorburger (mike@vorburger.ch)
 */
public class ReferenceParserSmartTest extends TestCase {

	/**
	 * Test method for {@link com.atlassian.uwc.converters.xml.jrst.ReferenceParser}.
	 */
	public void testStandardReferenceParser() throws Exception {
		ReferenceParser rp = new ReferenceParser();
		setupRP(rp);
		assertEquals("[GNU Linux  Mac OS X|UbuntuWARInstall#set-up-database]", rp.getOutput());
	}

	/**
	 * Test method for {@link com.atlassian.uwc.converters.xml.jrst.ReferenceParserSmart}.
	 */
	public void testSmartReferenceParser() throws Exception {
		ReferenceParser rp = new ReferenceParserSmart();
		setupRP(rp);
		assertEquals("[GNU Linux  Mac OS X|Ubuntu WAR Install#set-up-database]", rp.getOutput());
	}

	
	private void setupRP(ReferenceParser rp) throws SAXException {
		MockAttributes attributes = new MockAttributes();
		attributes.setValue(ReferenceParser.NAME, "GNU Linux  Mac OS X");
		attributes.setValue(ReferenceParser.LINK, "UbuntuWARInstall#set-up-database");
		attributes.setValue(ReferenceParser.REFURI, "GNU Linux  Mac OS X");
		rp.fullElement(null, null, null, attributes, "GNU Linux Mac OS X");
	}
	
	
	
	public class MockAttributes implements Attributes {

		Map<String, String> attributes = new HashMap<String, String>();

		void setValue(String key, String value) {
			attributes.put(key, value);
		}
		
		@Override
		public String getValue(String arg0) {
			return attributes.get(arg0);
		}

		@Override
		public int getIndex(String arg0) {
			throw new NotImplementedException();
		}

		@Override
		public int getIndex(String arg0, String arg1) {
			throw new NotImplementedException();
		}

		@Override
		public int getLength() {
			throw new NotImplementedException();
		}

		@Override
		public String getLocalName(int arg0) {
			throw new NotImplementedException();
		}

		@Override
		public String getQName(int arg0) {
			throw new NotImplementedException();
		}

		@Override
		public String getType(int arg0) {
			throw new NotImplementedException();
		}

		@Override
		public String getType(String arg0) {
			throw new NotImplementedException();
		}

		@Override
		public String getType(String arg0, String arg1) {
			throw new NotImplementedException();
		}

		@Override
		public String getURI(int arg0) {
			throw new NotImplementedException();
		}

		@Override
		public String getValue(int arg0) {
			throw new NotImplementedException();
		}

		@Override
		public String getValue(String arg0, String arg1) {
			throw new NotImplementedException();
		}
	}

}
