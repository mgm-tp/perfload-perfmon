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
package com.mgmtp.perfload.perfmon.cmd;

import org.apache.commons.lang.text.StrBuilder;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.Tcp;

/**
 * @author rnaegele
 */
public class PerfMonTcp extends BasePerfMonCommand {

	private static final String TYPE_TCP = "tcp";

	// The tcp attributes (with the exception of 'currEstab') are strictly growing OS counters.
	// To measure the growth of those counters we need to store the most recent values as 'offsets'
	// for the next time interval
	private TcpData offset = new TcpData();

	public PerfMonTcp(final String separator) {
		super(separator);
	}

	@Override
	public String executeCommand(final SigarProxy sigar) throws SigarException {
		StrBuilder sb = new StrBuilder(200);

		// get counter data from sigar
		TcpData counterData = new TcpData(sigar.getTcp());
		// calculate the data to write to the report by subtracting the offset
		TcpData reportData = counterData.cloneAndSubtractOffset(offset);
		// store the original counter data as new offset
		offset = counterData;

		sb.append(TYPE_TCP);
		sb.append(separator);
		sb.append(reportData.activeOpens); // active connections openings
		sb.append(separator);
		sb.append(reportData.passiveOpens); // passive connection openings
		sb.append(separator);
		sb.append(reportData.attemptFails); // failed connection attempts
		sb.append(separator);
		sb.append(reportData.estabResets); // connection resets received
		sb.append(separator);
		sb.append(reportData.currEstab);// connections established
		sb.append(separator);
		sb.append(reportData.inSegs); // segments received
		sb.append(separator);
		sb.append(reportData.outSegs); // segments send out
		sb.append(separator);
		sb.append(reportData.retransSegs); // segments retransmitted
		sb.append(separator);
		sb.append(reportData.inErrs); // bad segments received
		sb.append(separator);
		sb.append(reportData.outRsts); // resets sent

		return sb.toString();
	}

	private static class TcpData {
		private boolean isEmpty=true;
		private long activeOpens=0;
		private long passiveOpens=0;
		private long attemptFails=0;
		private long estabResets=0;
		private long currEstab=0;
		private long inSegs=0;
		private long outSegs=0;
		private long retransSegs=0;
		private long inErrs=0;
		private long outRsts=0;

		//create empty with all values -1
		public TcpData() {};

		// create from sigar tcp object
		public TcpData(Tcp tcp) {
			this.activeOpens = tcp.getActiveOpens();
			this.passiveOpens = tcp.getPassiveOpens();
			this.attemptFails = tcp.getAttemptFails();
			this.estabResets = tcp.getEstabResets();
			this.currEstab = tcp.getCurrEstab();
			this.inSegs = tcp.getInSegs();
			this.outSegs = tcp.getOutSegs();
			this.retransSegs = tcp.getRetransSegs();
			this.inErrs = tcp.getInErrs();
			this.outRsts = tcp.getOutRsts();
			this.isEmpty=false;
		}

		/**
		 * clone data
		 */
		public TcpData(TcpData oData) {
			if( !oData.isEmpty ) {
				this.activeOpens = oData.activeOpens;
				this.passiveOpens = oData.passiveOpens;
				this.attemptFails = oData.attemptFails;
				this.estabResets = oData.estabResets;
				this.currEstab = oData.currEstab;
				this.inSegs = oData.inSegs;
				this.outSegs = oData.outSegs;
				this.retransSegs = oData.retransSegs;
				this.inErrs = oData.inErrs;
				this.outRsts = oData.outRsts;
				this.isEmpty=oData.isEmpty;
			}
		}

		/**
		 * clone the data and subtract the offset in a single op
		 *
		 * @return cloned data with offset subtracted
		 * @return 0 data if the offset is empty
		 * @throws IllegalStateException if the data to clone are empty
		 */
		public TcpData cloneAndSubtractOffset(TcpData offset) {
			if( !this.isEmpty ) {
				TcpData delta;
				if( offset.isEmpty ) {
					delta = new TcpData();
					delta.isEmpty=false;
					// it's not a counter:
					delta.currEstab=this.currEstab;
				} else {
					delta = new TcpData(this);
					delta.activeOpens -= offset.activeOpens;
					delta.passiveOpens -= offset.passiveOpens;
					delta.attemptFails -= offset.attemptFails;
					delta.estabResets -= offset.estabResets;
					//currEstab is not a counter: do not subtract from current value
					//delta.currEstab -= offset.currEstab;
					delta.inSegs -= offset.inSegs;
					delta.outSegs -= offset.outSegs;
					delta.retransSegs -= offset.retransSegs;
					delta.inErrs -= offset.inErrs;
					delta.outRsts -= offset.outRsts;
				}
				return delta;
			} else {
				throw new IllegalStateException("must not clone empty data");
			}
		}
	}
}
