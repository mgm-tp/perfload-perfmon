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

import static com.mgmtp.perfload.perfmon.util.PerfMonUtils.appendLineBreakIfNotEmpty;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrBuilder;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;

/**
 * @author rnaegele
 */
public class PerfMonIo extends BasePerfMonCommand {

	private static final String TYPE_IO_X = "io_";

	private final boolean normalize;
	private final Map<String, Offset> offsets = new HashMap<String, Offset>();

	public PerfMonIo(final String separator, final boolean normalize) {
		super(separator);
		this.normalize = normalize;
	}

	@Override
	public String executeCommand(final SigarProxy sigar) {
		StrBuilder sb = new StrBuilder(200);

		try {
			FileSystem[] fileSystems = sigar.getFileSystemList();
			if (normalize && offsets.isEmpty()) {
				for (FileSystem fileSystem : fileSystems) {
					if (fileSystem.getType() == FileSystem.TYPE_LOCAL_DISK) {
						String dirName = fileSystem.getDirName();
						FileSystemUsage usage = sigar.getFileSystemUsage(dirName);

						Offset offset = new Offset();
						offset.diskWrites = usage.getDiskWrites();
						offset.diskWriteBytes = usage.getDiskWriteBytes();
						offset.diskReads = usage.getDiskReads();
						offset.diskReadBytes = usage.getDiskReadBytes();
						offsets.put(dirName, offset);
					}
				}
			}

			for (int i = 0; i < fileSystems.length; ++i) {
				FileSystem fileSystem = fileSystems[i];
				if (fileSystem.getType() == FileSystem.TYPE_LOCAL_DISK) {
					appendLineBreakIfNotEmpty(sb);

					String dirName = fileSystem.getDirName();
					FileSystemUsage usage = sigar.getFileSystemUsage(dirName);
					sb.append(TYPE_IO_X + i);
					sb.append(separator);

					if (normalize) {
						Offset offset = offsets.get(dirName);
						sb.append(usage.getDiskReads() - offset.diskReads);
						sb.append(separator);
						sb.append(usage.getDiskWrites() - offset.diskWrites);
						sb.append(separator);
						sb.append(usage.getDiskReadBytes() - offset.diskReadBytes);
						sb.append(separator);
						sb.append(usage.getDiskWriteBytes() - offset.diskWriteBytes);
					} else {
						sb.append(usage.getDiskReads());
						sb.append(separator);
						sb.append(usage.getDiskWrites());
						sb.append(separator);
						sb.append(usage.getDiskReadBytes());
						sb.append(separator);
						sb.append(usage.getDiskWriteBytes());
					}

					sb.append(separator);
					sb.append(fileSystem.getDevName());
					sb.append(separator);
					sb.append(fileSystem.getDirName());
				}
			}
		} catch (SigarException ex) {
			log.error("Error reading IO information: " + ex.getMessage(), ex);
		}

		return sb.toString();
	}

	private static class Offset {
		private long diskReads;
		private long diskWrites;
		private long diskReadBytes;
		private long diskWriteBytes;
	}
}
