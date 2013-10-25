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

import org.apache.commons.lang.text.StrBuilder;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;

/**
 * @author rnaegele
 */
public class PerfMonMem extends BasePerfMonCommand {

	private static final String TYPE_MEM = "mem";

	public PerfMonMem(final String separator) {
		super(separator);
	}

	@Override
	public String executeCommand(final SigarProxy sigar) {
		StrBuilder sb = new StrBuilder(200);

		try {
			Mem mem = sigar.getMem();

			sb.append(TYPE_MEM);
			sb.append(separator);
			sb.append(mem.getTotal() / 1024L);
			sb.append(separator);
			sb.append(mem.getUsed() / 1024L);
			sb.append(separator);
			sb.append(mem.getFree() / 1024L);
			sb.append(separator);
			sb.append(mem.getActualUsed() / 1024L);
			sb.append(separator);
			sb.append(mem.getActualFree() / 1024L);
		} catch (SigarException ex) {
			log.error("Error reading memory information: " + ex.getMessage(), ex);
		}

		return sb.toString();
	}
}
