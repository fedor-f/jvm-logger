**Usage:** jvm-logger [-hsvV] [-d=\<recordingDuration\>] -i=\<input\>
                      [-jfr=\<jfrOutput\>] [--jfr-settings=\<jfrSettings\>]
                      [-o=\<output\>] [-a=\<args\>...]... [COMMAND]
collects JVM events <br><br>
**Options:** 
```
-a, --args=<args>...                    String arguments for .jar
-d, --duration=<recordingDuration>      Event recording duration
-h, --help                              Show this help message and exit.
-i, --input=<input>     		        Input .jar file path
-jfr, --jfr-output=<jfrOutput>		    JFR events output file path
--jfr-settings=<jfrSettings>		    Path to .jfc file with JFR Settings
-o, --output=<output>			        File output path
-s, --stat              			    Display statistics of collected events
-v, --verbose           			    Log events verbose?
-V, --version           			    Print version information and exit.
```

**Commands:**
```
filter-by-categories  	Enable collection of JVM events filtered by categories of events
filter-by-names       	Enable collection of JVM events filtered by names of event types
```