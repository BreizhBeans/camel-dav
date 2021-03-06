/**
 *  Copyright 2013 Giwi Softwares (http://giwi.free.fr)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.giwi.camel.dav.test;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DavProducerFileExistFailTest.
 * 
 * @version
 */
public class DavProducerFileExistFailTest extends AbstractDavTest {

	/**
	 * Gets the dav url.
	 * 
	 * @return the dav url
	 */
	protected String getDavUrl() {
		return DAV_URL + "/exist?delay=2000&noop=true&fileExist=Fail";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.test.junit4.CamelTestSupport#setUp()
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		deleteDirectory("tmpOut/exist");

		template.sendBodyAndHeader(getDavUrl(), "Hello World", Exchange.FILE_NAME, "hello.txt");
	}

	/**
	 * Test fail.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testFail() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedBodiesReceived("Hello World");
		mock.expectedFileExists(DAV_ROOT_DIR + "/exist/hello.txt", "Hello World");

		try {
			template.sendBodyAndHeader(getDavUrl(), "Bye World", Exchange.FILE_NAME, "hello.txt");
			fail("Should have thrown an exception");
		} catch (CamelExecutionException e) {
			GenericFileOperationFailedException cause = assertIsInstanceOf(GenericFileOperationFailedException.class, e.getCause());
			assertEquals("File already exist: webdav/exist/hello.txt. Cannot write new file.", cause.getMessage());
		}

		assertMockEndpointsSatisfied();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.test.junit4.CamelTestSupport#createRouteBuilder()
	 */
	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(getDavUrl()).to("mock:result");
			}
		};
	}
}