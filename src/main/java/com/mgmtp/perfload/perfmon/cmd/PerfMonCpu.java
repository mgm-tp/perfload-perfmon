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
import static com.mgmtp.perfload.perfmon.util.PerfMonUtils.appendPercent;

import org.apache.commons.lang.text.StrBuilder;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;

/**
 * @author rnaegele
 */
public class PerfMonCpu extends BasePerfMonCommand {

	private static final String TYPE_CPU_AVG = "cpu_X";
	private static final String TYPE_CPU_X = "cpu_";

	private final String csvHeader =
			"cpus<SEP>cpu_avg_combined<SEP>cpu_avg_user<SEP>cpu_avg_nice<SEP>cpu_avg_sys<SEP>cpu_avg_wait<SEP>cpu_avg_idle"
					.replace("<SEP>", separator);

	public PerfMonCpu(final String separator, final boolean csv) {
		super(separator, csv);
	}

	@Override
	public String getCsvHeader() {
		return csvHeader;
	}

	@Override
	public String executeCommand(final SigarProxy sigar) {
		StrBuilder sb = new StrBuilder(200);

		try {
			CpuPerc[] cpus = sigar.getCpuPercList();
			CpuPerc cpuAll = sigar.getCpuPerc();

			if (csv) {
				sb.append(cpus.length);
				sb.append(separator);
				writeCpu(sb, cpuAll);
			} else {
				sb.append(TYPE_CPU_AVG);
				sb.append(separator);
				writeCpu(sb, cpuAll);

				if (cpus.length > 1) { // more than one CPU
					appendLineBreak(sb);
					for (int i = 0; i < cpus.length; i++) {
						appendLineBreak(sb, i);
						sb.append(TYPE_CPU_X + i);
						sb.append(separator);
						writeCpu(sb, cpus[i]);
					}
				}
			}
		} catch (SigarException ex) {
			log.error("Error reading CPU information: " + ex.getMessage(), ex);
		}

		return sb.toString();
	}

	private void writeCpu(final StrBuilder sb, final CpuPerc cpuPerc) {
		appendPercent(sb, cpuPerc.getCombined());
		sb.append(separator);
		appendPercent(sb, cpuPerc.getUser());
		sb.append(separator);
		appendPercent(sb, cpuPerc.getNice());
		sb.append(separator);
		appendPercent(sb, cpuPerc.getSys());
		sb.append(separator);
		appendPercent(sb, cpuPerc.getWait());
		sb.append(separator);
		appendPercent(sb, cpuPerc.getIdle());
	}
}
