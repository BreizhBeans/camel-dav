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

import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

/**
 * The Class DavProducerRecipientListTest.
 */
public class DavProducerRecipientListTest extends AbstractDavTest {

    /**
     * Gets the dav url.
     * 
     * @return the dav url
     */
    private String getDavUrl() {
	return DAV_URL + "/list";
    }

    /**
     * Test producer recipient list.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testProducerRecipientList() throws Exception {
	template.sendBodyAndHeader("direct:start", "Hello World", "foo",
		getDavUrl() + "?fileName=hello.txt");
	template.sendBodyAndHeader("direct:start", "Bye World", "foo",
		getDavUrl() + "?fileName=bye.txt");
	template.sendBodyAndHeader("direct:start", "Hi World", "foo",
		getDavUrl() + "?fileName=hi.txt");

	File file1 = new File(DAV_ROOT_DIR + "/list/hello.txt");
	assertTrue("File should exists " + file1, file1.exists());

	File file2 = new File(DAV_ROOT_DIR + "/list/bye.txt");
	assertTrue("File should exists " + file2, file1.exists());

	File file3 = new File(DAV_ROOT_DIR + "/list/hi.txt");
	assertTrue("File should exists " + file3, file1.exists());
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
		from("direct:start").recipientList(header("foo"));
	    }
	};
    }
}
