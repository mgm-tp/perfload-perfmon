/*
 * Copyright (c) 2014 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.perfload.perfmon.io;

/**
 * {@link OutputHandler} implementation for writing to the console.
 * 
 * @author rnaegele
 */
public class ConsoleOutputHandler implements OutputHandler {

	/**
	 * Does nothing.
	 */
	@Override
	public void open() {
		// no-op
	}

	/**
	 * Delegates to {@code System.out.println}
	 */
	@Override
	public void writeln(final String output) {
		System.out.println(output);
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void close() {
		// no-op
	}

}
