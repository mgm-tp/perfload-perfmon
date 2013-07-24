/*
 * Copyright (c) 2013 mgm technology partners GmbH
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

import java.io.IOException;
import java.io.PrintWriter;

/**
 * {@link OutputHandler} implementation for writing to a file.
 * 
 * @author rnaegele
 */
public class FileOutputHandler implements OutputHandler {

	private final String fileName;
	private PrintWriter writer;

	/**
	 * @param fileName
	 *            the name of the file output should be written to
	 */
	public FileOutputHandler(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Opens a {@link PrintWriter} to the output file using UTF-8 encoding.
	 */
	@Override
	public void open() throws IOException {
		writer = new PrintWriter(fileName, "UTF-8");
	}

	/**
	 * Writes and flushes the output to the internal {@link PrintWriter} using
	 * {@link PrintWriter#println(String)} and {@link PrintWriter#flush()}.
	 */
	@Override
	public void writeln(final String output) {
		writer.println(output);
		writer.flush();
	}

	/**
	 * Closes the internal {@link PrintWriter}.
	 */
	@Override
	public void close() {
		writer.close();
	}

}
