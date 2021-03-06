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

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

/**
 * The Class DavConsumerDoneFileNameFixedTest.
 * 
 * @version
 */
public class DavConsumerDoneFileNameFixedTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    protected String getDavUrl() {
	return DAV_URL + "/done?initialDelay=0&delay=100";
    }

    /**
     * Test done file name.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDoneFileName() throws Exception {
	getMockEndpoint("mock:result").expectedMessageCount(0);

	template.sendBodyAndHeader(getDavUrl(), "Hello World",
		Exchange.FILE_NAME, "hello.txt");

	// wait a bit and it should not pickup the written file as there are no
	// done file
	Thread.sleep(1000);

	assertMockEndpointsSatisfied();

	resetMocks();

	getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");

	// write the done file
	template.sendBodyAndHeader(getDavUrl(), "", Exchange.FILE_NAME,
		"fin.dat");

	assertMockEndpointsSatisfied();

	// give time for done file to be deleted
	Thread.sleep(1000);

	// done file should be deleted now
	File file = new File(DAV_ROOT_DIR + "done/fin.dat");
	assertFalse("Done file should be deleted: " + file, file.exists());
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
		from(getDavUrl() + "&doneFileName=fin.dat").convertBodyTo(
			String.class).to("mock:result");
	    }
	};
    }

}
