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
import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;

/**
 * @author rnaegele
 */
public class PerfMonProc extends BasePerfMonCommand {

	private static final String TYPE_PROC = "proc";

	public PerfMonProc(final String separator) {
		super(separator);
	}

	@Override
	public String executeCommand(final SigarProxy sigar) throws SigarException {
		StrBuilder sb = new StrBuilder(200);

		ProcStat procStat = sigar.getProcStat();

		sb.append(TYPE_PROC);
		sb.append(separator);
		sb.append(procStat.getTotal());
		sb.append(separator);
		sb.append(procStat.getRunning());
		sb.append(separator);
		sb.append(procStat.getIdle());
		sb.append(separator);
		sb.append(procStat.getSleeping());
		sb.append(separator);
		sb.append(procStat.getStopped());
		sb.append(separator);
		sb.append(procStat.getZombie());
		sb.append(separator);
		sb.append(procStat.getThreads());

		return sb.toString();
	}
}
