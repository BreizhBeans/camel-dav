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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;

/**
 * The Class DavIllegalOptionsTest.
 */
public class DavIllegalOptionsTest extends AbstractDavTest {

    /**
     * Test illegal options.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void testIllegalOptions() throws Exception {
	try {
	    context.getEndpoint(
		    DAV_URL + "/tmpOut?move=../done/${file:name}&delete=true")
		    .createConsumer(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
			}
		    });
	    fail("Should have thrown an exception");
	} catch (IllegalArgumentException e) {
	    // ok
	}

	try {
	    context.getEndpoint(
		    "file://tmpOut?move=../done/${file:name}&delete=true")
		    .createConsumer(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
			}
		    });
	    fail("Should have thrown an exception");
	} catch (IllegalArgumentException e) {
	    // ok
	}
    }
}