package ru.hse.guiapp.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventInfo {

    public static final Set<String> CATEGORY_LIST = Set.of(
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

    public static final Map<String, String> EVENT_NAME_MAP = Map.ofEntries(
            Map.entry("jdk.ActiveSetting", "Применение настроек для каждого типа событий при запуске пользовательского приложения."),
            Map.entry("jdk.ActiveRecording", "Информация о запущенной записи событий Java Flight Recorder"),
            Map.entry("jdk.JavaMonitorWait", "Ожидание потоком (объектом Thread) монитора"),
            Map.entry("jdk.JavaMonitorEnter", "Получение (блокировка) монитора потоком"),
            Map.entry("jdk.ThreadSleep", "Выполнение действий, связанных с Thread Sleep (приостановка потока)"),
            Map.entry("jdk.ThreadPark", "Выполнение действий, связанных с парковкой потока Thread Park"),
            Map.entry("jdk.JavaErrorThrow", "Создание объекта, наследуемому от java.lang.Error"),
            Map.entry("jdk.SockerRead", "Выполнение операции чтения из сокета"),
            Map.entry("jdk.SocketWrite", "Операция записи в сокет"),
            Map.entry("jdk.GCPhasePause", "Фаза паузы сборщика мусора"),
            Map.entry("jdk.GCPhasePauseLevel1", "Фаза паузы сборщика мусора Level 1"),
            Map.entry("jdk.GCPhasePauseLevel2", "Фаза паузы сборщика мусора Level 2"),
            Map.entry("jdk.GarbageCollection", "Сборка мусора, произведенная JVM"),
            Map.entry("jdk.YoungGarbageCollection", "Дополнительная информация о сборке мусора в молодом поколении"),
            Map.entry("jdk.G1GarbageCollection", "Дополнительная информация, относящаяся к сборке мусора в молодом поколении G1 сборщика"),
            Map.entry("jdk.GCPhaseParallel", "Параллельная фаза сборки мусора"),
            Map.entry("jdk.OldGarbageCollection", "Дополнительная информация, описывающая сборку мусора в старом поколении"),
            Map.entry("jdk.GCPhaseConcurrent", "Конкурентная фаза сборки мусора"),
            Map.entry("jdk.GCPhaseConcurrentLevel1", "Конкурентная фаза сборки мусора Level 1"),
            Map.entry("jdk.ExecuteVMOperation", "Выполнение операции виртуальной машины"),
            Map.entry("jdk.FileRead", "Операция чтения из файла"),
            Map.entry("jdk.FileWrite", "Операция записи в файл"),
            Map.entry("jdk.FileForce", "Принудительная запись обновлений в файл"),
            Map.entry("jdk.JavaExceptionThrow", "Объект, наследуемый от java.lang.Exception был инициализирован"),
            Map.entry("jdk.ProcessStart", "Старт процесса операционной системы"),
            Map.entry("jdk.SystemGC", "Сборка мусора, вызванная с помощью System.gc()"),
            Map.entry("jdk.Compilation", "Компиляция"),
            Map.entry("jdk.CompilerPhase", "Информация о фазе компилятора"),
            Map.entry("jdk.GCLocker", "Запуск GC Locker (механизм, предотвращающий запуск сборщика мусора)"),
            Map.entry("jdk.RedefineClasses", "Изменения определения класса во время выполнения программы без перезапуска JVM или приложения"),
            Map.entry("jdk.RetransformClasses", "Ретрансформация классов после их загрузки в JVM"),
            Map.entry("jdk.SafepointBegin", "Сообщение JVM всем рабочим потокам остановиться в безопасной точке"),
            Map.entry("jdk.SweepCodeCache", "Процесс очистки кэша скомпилированного кода"),
            Map.entry("jdk.ZAllocationStall", "Z Garbage Collector: поток приложения остановился, чтобы выделить память, потому что доступное пространство для выделения было исчерпано"),
            Map.entry("jdk.ZPageAllocation", "Выделение страниц памяти для кучи ZGC"),
            Map.entry("jdk.ZRelocationSetGroup", "Процесс группировки набора страниц памяти, которые должны быть очищены и переселены с целью оптимизации процесса очистки"),
            Map.entry("jdk.ZRelocationSet", "Начало обработки конкретного набора страниц, предназначенных для релокации"),
            Map.entry("jdk.ZUncommit", "ZGC освобождает (uncommit) ранее выделенные страницы памяти, которые больше не нужны"),
            Map.entry("jdk.ZUnmap", "Cобытие свидетельствует о том, что страницы больше не доступны для использования JVM и полностью возвращены системе.")
    );
}
