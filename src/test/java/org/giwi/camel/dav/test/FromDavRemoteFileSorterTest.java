/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.giwi.camel.dav.test;

import java.util.Comparator;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.giwi.camel.dav.RemoteFile;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test to verify remotefile sorter option.
 */
public class FromDavRemoteFileSorterTest extends AbstractDavTest {

	private String getDavUrl() {
		return DAV_URL + "/sorter?sorter=#mySorter";
	}

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		JndiRegistry jndi = super.createRegistry();
		jndi.bind("mySorter", new MyRemoteFileSorter());
		return jndi;
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		prepareDavServer();
	}

	@Test
	public void testDavSorter() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMessageCount(3);
		mock.expectedBodiesReceived("Hello Copenhagen", "Hello London", "Hello Paris");
		mock.assertIsSatisfied();
	}

	private void prepareDavServer() throws Exception {
		// prepares the FTP Server by creating files on the server that we want
		// to unit
		// test that we can pool
		sendFile(getDavUrl(), "Hello Paris", "paris.txt");
		sendFile(getDavUrl(), "Hello London", "london.txt");
		sendFile(getDavUrl(), "Hello Copenhagen", "copenhagen.txt");
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(getDavUrl()).to("mock:result");
			}
		};
	}

	// START SNIPPET: e1
	public class MyRemoteFileSorter implements Comparator<RemoteFile<?>> {

		public int compare(RemoteFile<?> o1, RemoteFile<?> o2) {
			return o1.getFileNameOnly().compareToIgnoreCase(o2.getFileNameOnly());
		}
	}
	// END SNIPPET: e1
}