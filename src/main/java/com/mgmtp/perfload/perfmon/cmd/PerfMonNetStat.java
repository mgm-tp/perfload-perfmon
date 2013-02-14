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
import org.hyperic.sigar.NetStat;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;

/**
 * @author rnaegele
 */
public class PerfMonNetStat extends BasePerfMonCommand {

	private static final String TYPE_NET = "net";

	private final String csvHeader = "ait<SEP>aot<SEP>bnd<SEP>cls<SEP>clsw<SEP>cli<SEP>est<SEP>fw1<SEP>fw2<SEP>idl<SEP>int<SEP>ack<SEP>lsn<SEP>ot<SEP>syr<SEP>sys<SEP>tiw"
			.replace("<SEP>", separator);

	public PerfMonNetStat(final String separator, final boolean csv) {
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
			NetStat net = sigar.getNetStat();

			int allInboundTotal = net.getAllInboundTotal();
			int allOutboundTotal = net.getAllOutboundTotal();
			int tcpBound = net.getTcpBound();
			int tcpClose = net.getTcpClose();
			int tcpCloseWait = net.getTcpCloseWait();
			int tcpClosing = net.getTcpClosing();
			int tcpEstablished = net.getTcpEstablished();
			int tcpFinWait1 = net.getTcpFinWait1();
			int tcpFinWait2 = net.getTcpFinWait2();
			int tcpIdle = net.getTcpIdle();
			int tcpInboundTotal = net.getTcpInboundTotal();
			int tcpLastAck = net.getTcpLastAck();
			int tcpListen = net.getTcpListen();
			int tcpOutboundTotal = net.getTcpOutboundTotal();
			int tcpSynRecv = net.getTcpSynRecv();
			int tcpSynSent = net.getTcpSynSent();
			int tcpTimeWait = net.getTcpTimeWait();

			if (!csv) {
				sb.append(TYPE_NET);
				sb.append(separator);
			}

			sb.append(allInboundTotal);
			sb.append(separator);
			sb.append(allOutboundTotal);
			sb.append(separator);
			sb.append(tcpBound);
			sb.append(separator);
			sb.append(tcpClose);
			sb.append(separator);
			sb.append(tcpCloseWait);
			sb.append(separator);
			sb.append(tcpClosing);
			sb.append(separator);
			sb.append(tcpEstablished);
			sb.append(separator);
			sb.append(tcpFinWait1);
			sb.append(separator);
			sb.append(tcpFinWait2);
			sb.append(separator);
			sb.append(tcpIdle);
			sb.append(separator);
			sb.append(tcpInboundTotal);
			sb.append(separator);
			sb.append(tcpLastAck);
			sb.append(separator);
			sb.append(tcpListen);
			sb.append(separator);
			sb.append(tcpOutboundTotal);
			sb.append(separator);
			sb.append(tcpSynRecv);
			sb.append(separator);
			sb.append(tcpSynSent);
			sb.append(separator);
			sb.append(tcpTimeWait);
		} catch (SigarException ex) {
			log.error("Error reading network statistics: " + ex.getMessage(), ex);
		}

		return sb.toString();
	}
}
