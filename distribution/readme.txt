===============================================================================
 perfMon ${project.version}
===============================================================================

perfMon is a small utility based on sigar to measure all different 
kinds of system data like cpu, memory, IO, etc.

Start and stop scripts are distributed with it.
 
The following options are available:

 -c,--csv                  Write all data in one line for easier import in
                           tools such as Excel.
 -f,--file <FILE>          The output file the information is written to.
                           Stdout is used if no file is specified.
 -h,--help                 Prints usage information.
 -i,--interval <SECONDS>   The interval for printing the system
                           information (default 5 sec).
 -in,--normalize-io        Normalize IO information against an initial
                           offset.
 -j,--java                 Write information on Java processes.
 -n,--netstat              Write network statistics
 -s,--shutdown             Shutdown all running perfMon processes on this
                           machine.
 -t,--tcp                  Write TCP connection information.
 -tn,--normalize-tcp       Normalize TCP connection information against an
                           initial offset.
                           
The standard output format can be processed by the perfLoad reporting. One line
for each measured data is printed. All lines with the same timestamp belong 
to one measurement. The "csv" format outputs a header and the prints all 
information in one line, which is more suitable for use in a spread sheet.

Use the following environment variable to make the cpu_list functions return 
the number of cores as you'd see in /proc/cpuinfo and elsewhere:
export SIGAR_CPU_CORES=1 


perfMon's standard output format
================================

Values are separated by TAB characters.

META (only printed once)
  - SystemTime
  - perfMon version info
  - HostName
  - Local IP addresses (comma-delimited)
  
CPU_n (n = index of CPU, CPU_X is for total CPU statistics)
  - SystemTime
  - "cpu"_n
  - Combined%
  - User%
  - Nice%
  - Sys%
  - Wait%
  - Idle%

MEM
  - SystemTime
  - "mem"
  - Total (in KB)
  - Used (in KB)
  - Free (in KB)
  - ActualUsed (in KB)
  - ActualFree (in KB)

SWAP
  - SystemTime
  - "swap"
  - Total (in KB)
  - Used (in KB)
  - Free (in KB)

IO (n = index of IO)
  - SystemTime
  - "io"_n
  - DiskReads
  - DiskWrites
  - DiskReadBytes
  - DiskWriteBytes
  - DeviceName
  - DirectoryMounted

JAVA (n = index of Java process)
  - SystemTime
  - "java"_n
  - ProcessID
  - Username
  - ProcessStartTime
  - TotalProcessVirtualMemory
  - TotalProcessResidentMemory
  - TotalProcessSharedMemory
  - ProcessState ((I)dle, (R)un, (S)leep, (S)top, (Z)ombie)
  - TotalProcessCPUTime (min:sec)
  - CPU%
  - ProcessName

NET IO TCP
  - SystemTime
  - "net"
  - ActiveOpens
  - PassiveOpens
  - AttemptFails
  - EstablishedResets
  - CurrentlyEstablished
  - InSegments
  - OutSegments
  - RetransmissionSegments
  - InErrors
  - OutResets