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
package com.mgmtp.perfload.perfmon.cmd;

import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for perfMon commands. A command encapsulates Sigar calls.
 * 
 * @author rnaegele
 */
public abstract class BasePerfMonCommand {
	protected Logger log = LoggerFactory.getLogger(getClass());

	protected final String separator;

	/**
	 * @param separator
	 *            the separator to use when multiple values are returned by the command
	 */
	public BasePerfMonCommand(final String separator) {
		this.separator = separator;
	}

	/**
	 * Executes the command and returns its output as string.
	 * 
	 * @param sigar
	 *            the SigarProxy instance to use for querying system information
	 * @return the commands' output
	 */
	public abstract String executeCommand(SigarProxy sigar) throws SigarException;
}
