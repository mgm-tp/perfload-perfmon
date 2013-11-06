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

import static com.mgmtp.perfload.perfmon.util.PerfMonUtils.appendLineBreak;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.cmd.Ps;
import org.hyperic.sigar.cmd.Shell;

/**
 * @author rnaegele
 */
public class PerfMonJava extends BasePerfMonCommand {

	private static final String TYPE_JAVA_X = "java_";

	public PerfMonJava(final String separator) {
		super(separator);
	}

	@Override
	public String executeCommand(final SigarProxy sigar) throws SigarException {
		StrBuilder sb = new StrBuilder(200);

		String[] type = new String[] { "State.Name.sw=java" };
		long[] pids = Shell.getPids(sigar, type);

		for (int i = 0; i < pids.length; i++) {
			appendLineBreak(sb, i);

			sb.append(TYPE_JAVA_X + i);
			sb.append(separator);
			long pid = pids[i];

			String cpuPerc = "?";
			@SuppressWarnings("unchecked")
			List<Object> info = new ArrayList<Object>(Ps.getInfo(sigar, pid));
			ProcCpu cpu = sigar.getProcCpu(pid);
			cpuPerc = CpuPerc.format(cpu.getPercent());
			info.add(info.size() - 1, cpuPerc);
			sb.append(Ps.join(info));
		}

		return sb.toString();
	}
}
