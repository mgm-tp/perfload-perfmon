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
package com.mgmtp.perfload.perfmon.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrBuilder;

/**
 * Collection of utility methods for perfMon.
 * 
 * @author rnaegele
 */
public class PerfMonUtils {

	/**
	 * Constant for the line separator of the system
	 */
	public static final String LINE_SEP = System.getProperty("line.separator");

	/**
	 * Appends a double as percental value.
	 * 
	 * @param buffer
	 *            the buffer to append to
	 * @param val
	 *            the double value to transform and append
	 */
	public static void appendPercent(final StrBuilder buffer, final double val) {
		String p = String.valueOf(val * 100.0);
		int ix = p.indexOf(".") + 1;
		buffer.append(p.substring(0, ix)).append(p.substring(ix, ix + 1));
	}

	/**
	 * Appends a line break if the specified index is greater than 0.
	 * 
	 * @param buffer
	 *            the buffer to append to
	 * @param index
	 *            the index
	 * @see StrBuilder#appendSeparator(String, int)
	 */
	public static void appendLineBreak(final StrBuilder buffer, final int index) {
		buffer.appendSeparator(LINE_SEP, index);
	}

	/**
	 * Appends a line break if the buffer is not empty.
	 * 
	 * @param buffer
	 *            the buffer to append to
	 */
	public static void appendLineBreakIfNotEmpty(final StrBuilder buffer) {
		buffer.appendSeparator(LINE_SEP);
	}

	/**
	 * Appends a line break.
	 * 
	 * @param buffer
	 *            the buffer to append to
	 */
	public static void appendLineBreak(final StrBuilder buffer) {
		buffer.append(LINE_SEP);
	}

	/**
	 * @return the name of the localhost, or unknown, if it cannot be retrieved.
	 */
	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "unknown";
		}
	}

	/**
	 * Converts a binary MAC address to a readable string.
	 * 
	 * @param mac
	 *            the MAC address
	 * @return the readable MAC address as string
	 */
	public static String toMACAddrString(final byte[] mac) {
		if (mac == null) {
			return "[]";
		}

		int iMax = mac.length - 1;
		if (iMax == -1) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0;; i++) {
			sb.append(String.format("%1$02x", mac[i]));
			if (i == iMax) {
				return sb.append(']').toString();
			}
			sb.append(":");
		}
	}

	/**
	 * Gets all IP addresses of the localhost.
	 * 
	 * @return an array of {@link InetAddress} objects
	 */
	public static InetAddress[] getNetworkIPAddresses() throws UnknownHostException {
		InetAddress localhost = InetAddress.getLocalHost();
		return InetAddress.getAllByName(localhost.getCanonicalHostName());
	}

	/**
	 * Gets all network interfaces of the localhost.
	 * 
	 * @return a list of {@link NetworkInterface} objects
	 */
	public static List<NetworkInterface> getNetworkInterfaces() throws SocketException {
		List<NetworkInterface> niList = new ArrayList<NetworkInterface>();
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			niList.add(intf);
		}
		return niList;
	}

	/**
	 * @return perfMon's version
	 */
	public static String getPerfMonVersion() {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("perfMon.version");
		try {
			return IOUtils.toString(is);
		} catch (IOException ex) {
			// should never happen
			return "(!!!version could not be retrieved!!!)";
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
