/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright
 * ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.giwi.camel.dav;

import org.apache.camel.Exchange;
import org.apache.camel.ServicePoolAware;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.apache.camel.component.file.GenericFileProducer;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.URISupport;

/**
 * Generic remote file producer for all the FTP variations.
 */
public class RemoteFileProducer<T> extends GenericFileProducer<T> implements ServicePoolAware {

	private boolean loggedIn;

	protected RemoteFileProducer(RemoteFileEndpoint<T> endpoint, RemoteFileOperations<T> operations) {
		super(endpoint, operations);
	}

	@Override
	public String getFileSeparator() {
		return "/";
	}

	@Override
	public String normalizePath(String name) {
		return name;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		String target = createFileName(exchange);
		processExchange(exchange, target);
	}

	protected RemoteFileOperations<T> getOperations() {
		return (RemoteFileOperations<T>) operations;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RemoteFileEndpoint<T> getEndpoint() {
		return (RemoteFileEndpoint<T>) super.getEndpoint();
	}

	/**
	 * The file could not be written. We need to disconnect from the remote server.
	 */
	@Override
	public void handleFailedWrite(Exchange exchange, Exception exception) throws Exception {
		loggedIn = false;
		if (isStopping() || isStopped()) {
			// if we are stopping then ignore any exception during a poll
			log.debug("Exception occurred during stopping: " + exception.getMessage());
		} else {
			log.warn("Writing file failed with: " + exception.getMessage());
			try {
				disconnect();
			} catch (Exception e) {
				// ignore exception
				log.debug("Ignored exception during disconnect: " + e.getMessage());
			}
			// rethrow the original exception*/
			throw exception;
		}
	}

	public void disconnect() throws GenericFileOperationFailedException {
		// noop
	}

	@Override
	public void preWriteCheck() throws Exception {
		// noop
	}

	@Override
	public void postWriteCheck() {
		try {
			if (getEndpoint().isDisconnect()) {
				log.trace("postWriteCheck disconnect from: {}", getEndpoint());
				disconnect();
			}
		} catch (GenericFileOperationFailedException e) {
			// ignore just log a warning
			log.warn("Exception occurred during disconnecting from: " + getEndpoint() + " " + e.getMessage());
		}
	}

	@Override
	protected void doStart() throws Exception {
		log.debug("Starting");
		// do not connect when component starts, just wait until we process as we will
		// connect at that time if needed
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		try {
			disconnect();
		} catch (Exception e) {
			log.debug("Exception occurred during disconnecting from: " + getEndpoint() + " " + e.getMessage());
		}
		super.doStop();
	}

	protected void recoverableConnectIfNecessary() throws Exception {
		try {
			connectIfNecessary();
		} catch (Exception e) {
			loggedIn = false;

			// are we interrupted
			InterruptedException ie = ObjectHelper.getException(InterruptedException.class, e);
			if (ie != null) {
				if (log.isDebugEnabled()) {
					log.debug("Interrupted during connect to: " + getEndpoint(), ie);
				}
				throw ie;
			}

			if (log.isDebugEnabled()) {
				log.debug("Could not connect to: " + getEndpoint() + ". Will try to recover.", e);
			}
		}

		// recover by re-creating operations which should most likely be able to recover
		if (!loggedIn) {
			log.debug("Trying to recover connection to: {} with a fresh client.", getEndpoint());
			setOperations(getEndpoint().createRemoteFileOperations());
			connectIfNecessary();
		}
	}

	protected void connectIfNecessary() throws GenericFileOperationFailedException {
		// noop
	}

	@Override
	public boolean isSingleton() {
		// this producer is stateful because the remote file operations is not thread safe
		return false;
	}

	@Override
	public String toString() {
		return "RemoteFileProducer[" + URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
	}
}