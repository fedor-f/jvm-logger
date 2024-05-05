# CLI application

**Usage:** jvm-logger [-hsvV] [-d=\<recordingDuration\>] -i=\<input\> <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-jfr=\<jfrOutput\>] [--jfr-settings=\<jfrSettings\>] <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-o=\<output\>] [-a=\<args\>...]... [COMMAND] <br>
collects JVM events

**Options:** 
```
-a, --args=<args>...                    String arguments for .jar
-d, --duration=<recordingDuration>      Event recording duration
-gc, --garbage-collector=<gc>           Garbage collector implementation. 
                                        Could be -XX:+UseSerialGC, -XX:+UseParallelGC, -XX:+UseG1GC, -XX:+UseZGC. 
                                        Default option is -XX:+UseG1GC
-h, --help                              Show this help message and exit.
-i, --input=<input>                     Input .jar file path
-jfr, --jfr-output=<jfrOutput>          JFR events output file path
-o, --output=<output>                   File output path
-s, --stat                              Display statistics of collected events
-v, --verbose                           Log events verbose?
-V, --version                           Print version information and exit.
```

**Commands:**
```
filter-by-categories  	Enable collection of JVM events filtered by categories of events
filter-by-names       	Enable collection of JVM events filtered by names of event types
get-event-docs          Gets a link to Event documentation
```
