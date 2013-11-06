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
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.Swap;

/**
 * @author rnaegele
 */
public class PerfMonSwap extends BasePerfMonCommand {

	private static final String TYPE_SWAP = "swap";

	public PerfMonSwap(final String separator) {
		super(separator);
	}

	@Override
	public String executeCommand(final SigarProxy sigar) throws SigarException {
		StrBuilder sb = new StrBuilder(200);

		Swap swap = sigar.getSwap();

		sb.append(TYPE_SWAP);
		sb.append(separator);
		sb.append(swap.getTotal() / 1024L);
		sb.append(separator);
		sb.append(swap.getUsed() / 1024L);
		sb.append(separator);
		sb.append(swap.getFree() / 1024L);

		return sb.toString();
	}
}
