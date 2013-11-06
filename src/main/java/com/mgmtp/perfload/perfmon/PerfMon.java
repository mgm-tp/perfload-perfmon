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
package com.mgmtp.perfload.perfmon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang.time.FastDateFormat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;
import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.cmd.Uptime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.perfload.perfmon.cmd.BasePerfMonCommand;
import com.mgmtp.perfload.perfmon.cmd.PerfMonCpu;
import com.mgmtp.perfload.perfmon.cmd.PerfMonIo;
import com.mgmtp.perfload.perfmon.cmd.PerfMonJava;
import com.mgmtp.perfload.perfmon.cmd.PerfMonMem;
import com.mgmtp.perfload.perfmon.cmd.PerfMonNetStat;
import com.mgmtp.perfload.perfmon.cmd.PerfMonProc;
import com.mgmtp.perfload.perfmon.cmd.PerfMonSwap;
import com.mgmtp.perfload.perfmon.cmd.PerfMonTcp;
import com.mgmtp.perfload.perfmon.io.ConsoleOutputHandler;
import com.mgmtp.perfload.perfmon.io.FileOutputHandler;
import com.mgmtp.perfload.perfmon.io.OutputHandler;
import com.mgmtp.perfload.perfmon.util.PerfMonUtils;

/**
 * Utility program for logging system information using <a
 * href="http://www.hyperic.com/products/sigar">Sigar</a>.
 * 
 * @author rnaegele
 */
class PerfMon {
	private static final Logger LOG = LoggerFactory.getLogger(PerfMon.class);
	private static final String SEPARATOR = "\t";
	private static final Pattern LINE_PATTERN = Pattern.compile("^(.*)$", Pattern.MULTILINE);
	private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

	private final long interval;
	private final List<BasePerfMonCommand> commands;
	private final OutputHandler outputHandler;

	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

	// It is important to use SigarProxyCache and to reuse the instance in order to avoid CPU spikes.
	private final SigarProxy sigar;

	PerfMon(final SigarProxy sigar, final long interval, final List<BasePerfMonCommand> commands,
			final OutputHandler outputHandler) {
		this.sigar = sigar;
		this.interval = interval;
		this.commands = commands;
		this.outputHandler = outputHandler;
	}

	void writeHeader() {
		String hostName = PerfMonUtils.getHostName();

		StringBuilder metaBuffer = new StringBuilder(200);
		metaBuffer.append(DATE_FORMAT.format(System.currentTimeMillis()));
		metaBuffer.append(SEPARATOR);
		metaBuffer.append("meta");
		metaBuffer.append(SEPARATOR);
		metaBuffer.append("perfMon ").append(PerfMonUtils.getPerfMonVersion());
		metaBuffer.append(SEPARATOR);
		metaBuffer.append(hostName);

		LOG.info("Hostname: {}", hostName);
		try {
			LOG.info("CPUs: {}", sigar.getCpuList().length);
			LOG.info("Total RAM: {} MB", sigar.getMem().getRam());
			LOG.info("Uptime: {}", Uptime.getInfo(sigar));
		} catch (SigarException ex) {
			LOG.error("Error retrieving system information from Sigar.", ex);
		}

		try {
			InetAddress[] networkIPAddresses = PerfMonUtils.getNetworkIPAddresses();
			if (networkIPAddresses.length > 0) {
				LOG.info("Local IP addresses:");
				metaBuffer.append(SEPARATOR);
				for (int i = 0; i < networkIPAddresses.length; ++i) {
					InetAddress ip = networkIPAddresses[i];
					if (i > 0) {
						metaBuffer.append(',');
					}
					metaBuffer.append(ip);
					LOG.info(ip.toString());
				}
			}
		} catch (UnknownHostException ex) {
			LOG.error("Error retrieving IP addresses of the localhost.", ex);
		}

		try {
			LOG.info("Full list of network interfaces:");
			List<NetworkInterface> networkInterfaces = PerfMonUtils.getNetworkInterfaces();
			for (NetworkInterface ni : networkInterfaces) {
				for (Enumeration<InetAddress> en = ni.getInetAddresses(); en.hasMoreElements();) {
					LOG.info("\t\t{}", en.nextElement().toString());
				}
			}
		} catch (SocketException ex) {
			LOG.error("Error retrieving network interfaces.", ex);
		}

		outputHandler.writeln(metaBuffer.toString());
	}

	void scheduleInformationGathering() {
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					StrBuilder buffer = new StrBuilder(2000);

					for (BasePerfMonCommand cmd : commands) {
						try {
							String result = cmd.executeCommand(sigar);
							buffer.appendSeparator(PerfMonUtils.LINE_SEP);
							buffer.append(result);
						} catch (SigarException ex) {
							LOG.error("Error executing command: {}" + cmd.getClass().getSimpleName(), ex);
						}
					}

					String output = buffer.toString();
					// insert timestamp at the beginning of each line
					String isoTimestamp = DATE_FORMAT.format(System.currentTimeMillis());
					output = LINE_PATTERN.matcher(output).replaceAll(isoTimestamp + SEPARATOR + "$1");
					outputHandler.writeln(output);
				} catch (Exception ex) {
					LOG.error(ex.getMessage(), ex);
				}
			}
		}, 0L, interval, TimeUnit.SECONDS);
	}

	void runShutdownHook() {
		try {
			LOG.info("Running shutdown hook...");
			executor.shutdownNow();
			executor.awaitTermination(interval, TimeUnit.SECONDS);
			outputHandler.close();
			LOG.info("Good bye.");
		} catch (InterruptedException ex) {
			// ignore
		}
	}

	public static void main(final String[] args) {
		CommandLine cmd = parseArgs(args);

		if (cmd != null) {
			final Sigar sigar = new Sigar();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					sigar.close();
				}
			});

			if (cmd.hasOption('s')) {
				shutdown(sigar);
				return;
			}

			long interval = Long.parseLong(cmd.getOptionValue('i', "5"));
			String fileName = cmd.getOptionValue('f');
			boolean java = cmd.hasOption('j');
			boolean tcp = cmd.hasOption('t');
			boolean netstat = cmd.hasOption('n');

			OutputHandler outputHandler = fileName != null ? new FileOutputHandler(fileName) : new ConsoleOutputHandler();
			try {
				outputHandler.open();

				// Initialize Sigar libraries in order to fail fast.
				// Lazy initialization would cause perfMon to keep running logging errors all the time.
				// See https://github.com/mgm-tp/perfload/issues/3
				Sigar.load();

				SigarProxy sigarProxy = SigarProxyCache.newInstance(sigar);
				List<BasePerfMonCommand> commands = createCommandsList(java, tcp, netstat);
				final PerfMon perfMon = new PerfMon(sigarProxy, interval, commands, outputHandler);

				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						perfMon.runShutdownHook();
					}
				});

				perfMon.writeHeader();
				perfMon.scheduleInformationGathering();
			} catch (IOException ex) {
				LOG.error("Error opening output file: " + fileName, ex);
			} catch (SigarException ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}
	}

	static void shutdown(final Sigar sigar) {
		LOG.info("Shutting down perfMon...");

		String[] type = new String[] { "State.Name.sw=java,Args.*.re=perf(m|M)on" };

		try {
			long ownPid = sigar.getPid();
			long[] pids = Shell.getPids(sigar, type);
			for (long pid : pids) {
				if (pid != ownPid) {
					LOG.info("Sending SIGTERM signal to perfMon process with PID {}", pid);
					sigar.kill(pid, "TERM");
				}
			}
		} catch (SigarException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	static List<BasePerfMonCommand> createCommandsList(final boolean java, final boolean tcp, final boolean netStat) {

		List<BasePerfMonCommand> commands = new ArrayList<BasePerfMonCommand>();
		commands.add(new PerfMonCpu(SEPARATOR));
		commands.add(new PerfMonMem(SEPARATOR));
		commands.add(new PerfMonSwap(SEPARATOR));
		if (tcp) {
			commands.add(new PerfMonTcp(SEPARATOR));
		}
		if (netStat) {
			commands.add(new PerfMonNetStat(SEPARATOR));
		}
		commands.add(new PerfMonIo(SEPARATOR));
		commands.add(new PerfMonProc(SEPARATOR));
		if (java) {
			commands.add(new PerfMonJava(SEPARATOR));
		}
		return commands;
	}

	static CommandLine parseArgs(final String[] args) {
		Options options = new Options();
		try {
			Option option = new Option("i", "interval", true, "The interval for printing the system information (default 5 sec).");
			option.setArgName("SECONDS");
			options.addOption(option);

			option = new Option("f", "file", true,
					"The output file the information is written to. Stdout is used if no file is specified.");
			option.setArgName("FILE");
			options.addOption(option);

			options.addOption("c", "csv", false, "Write all data in one line for easier import in tools such as Excel.");
			options.addOption("j", "java", false, "Write information on Java processes.");
			options.addOption("t", "tcp", false, "Write TCP connection information.");
			options.addOption("n", "netstat", false, "Write network statistics");
			options.addOption("s", "shutdown", false, "Shutdown all running perfMon processes on this machine.");
			options.addOption("h", "help", false, "Prints usage information.");

			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);
			if (!cmd.hasOption('h')) {
				return cmd;
			}
		} catch (ParseException ex) {
			LOG.error(ex.getMessage(), ex);
		}

		printUsage(options);
		return null;
	}

	static void printUsage(final Options options) {
		HelpFormatter help = new HelpFormatter();
		help.printHelp("perfMon", options);
	}
}
