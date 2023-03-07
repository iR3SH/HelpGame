package org.starloco.locos.util;

import java.util.concurrent.*;
import java.util.*;

public class TimerWaiter {

    private static final ThreadFactory mainFactory = r -> new Thread(r) {{ setName("Custom Thread");setDaemon(true);}};

    private static final ScheduledThreadPoolExecutor mapScheduler = new ScheduledThreadPoolExecutor(15, mainFactory);
    private static final ScheduledThreadPoolExecutor clientScheduler = new ScheduledThreadPoolExecutor(30, mainFactory);
    private static final ScheduledThreadPoolExecutor fightScheduler = new ScheduledThreadPoolExecutor(40, mainFactory);

    private final static Map<DataType,ScheduledThreadPoolExecutor> schedulerPools = new HashMap<DataType,ScheduledThreadPoolExecutor>() {
		private static final long serialVersionUID = 1L;
	{
        put(DataType.MAP, mapScheduler);
        put(DataType.CLIENT, clientScheduler);
        put(DataType.FIGHT, fightScheduler);
    }};

    public static void addNext(Runnable run, long time, TimeUnit unit, DataType scheduler) {
        schedulerPools.get(scheduler).schedule(run, time, unit);
    }

    public static void addNext(Runnable run, long time, DataType scheduler) {
        addNext(run, time, TimeUnit.MILLISECONDS, scheduler);
    }

    public static void purge() {
        System.out.println("Purge of schedulers...");
        try {
            mapScheduler.purge();
            clientScheduler.purge();
            fightScheduler.purge();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Purge of schedulers OK !");
        }
    }

    public enum DataType {
        MAP,
        CLIENT,
        FIGHT
    }
}