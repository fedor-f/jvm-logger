package ru.hse.guiapp.config;

import java.util.List;

public class EventInfo {

    public static final List<String> CATEGORY_LIST = List.of(
            "Flight Recorder",
            "Java Application",
            "Java Development Kit",
            "Security",
            "Serialization",
            "Java Virtual Machine",
            "Class Loading",
            "Code Sweeper",
            "Compiler",
            "GC",
            "Collector",
            "Detailed",
            "Phases",
            "Runtime",
            "Safepoint",
            "Operating System"
    );

    public static final List<String> EVENT_NAME_LIST = List.of(
            "jdk.ActiveSetting",
            "jdk.ActiveRecording",
            "jdk.JavaMonitorWait",
            "jdk.JavaMonitorEnter",
            "jdk.ThreadSleep",
            "jdk.ThreadPark",
            "jdk.JavaErrorThrow",
            "jdk.SockerRead",
            "jdk.SocketWrite",
            "jdk.GCPhasePause",
            "jdk.GCPhasePauseLevel1",
            "jdk.GCPhasePauseLevel2",
            "jdk.GarbageCollection",
            "jdk.YoungGarbageCollection",
            "jdk.G1GarbageCollection",
            "jdk.GCPhaseParallel",
            "jdk.OldGarbageCollection",
            "jdk.GCPhaseConcurrent",
            "jdk.GCPhaseConcurrentLevel1",
            "jdk.ExecuteVMOperation",
            "jdk.FileRead",
            "jdk.FileWrite",
            "jdk.FileForce",
            "jdk.JavaExceptionThrow",
            "jdk.ProcessStart",
            "jdk.SystemGC",
            "jdk.Compilation",
            "jdk.CompilerPhase",
            "jdk.GCLocker",
            "jdk.RedefineClasses",
            "jdk.RetransformClasses",
            "jdk.SafepointBegin",
            "jdk.SweepCodeCache",
            "jdk.ZAllocationStall",
            "jdk.ZPageAllocation",
            "jdk.ZRelocationSetGroup",
            "jdk.ZRelocationSet",
            "jdk.ZUncommit",
            "jdk.ZUnmap"
    );
}
