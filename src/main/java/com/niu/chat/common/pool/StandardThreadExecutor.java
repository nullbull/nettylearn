package com.niu.chat.common.pool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: justinniu
 * @date: 2018-11-27 15:44
 * @desc:
 **/
public class StandardThreadExecutor extends ThreadPoolExecutor {

    public static final int DEFAULT_MIN_THREADS = 20;
    public static final int DEFAULT_MAX_THREADS = 200;
    public static final long DEFAULT_MAX_IDLE_TIME = 60 * 1000;

    protected AtomicInteger submittedTasksCount;
    private int maxSubmittedTaskCount;

    public StandardThreadExecutor() {
        this(DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
    }

    public StandardThreadExecutor(int coreThread, int maxThreads) {
        this(coreThread, maxThreads, maxThreads);
    }

    public StandardThreadExecutor(int coreThread, int maxThreads, long keepAliveTime, TimeUnit unit) {
        this(coreThread, maxThreads, keepAliveTime, unit, maxThreads);
    }

    public StandardThreadExecutor(int coreThreads, int maxThreads, int queueCapacity) {
        this(coreThreads, maxThreads, queueCapacity, Executors.defaultThreadFactory());
    }


    public StandardThreadExecutor(int corePoolSize, int maximumPoolSize, int queueCapactiy, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, DEFAULT_MAX_IDLE_TIME,TimeUnit.MILLISECONDS, queueCapactiy, threadFactory);
    }


    public StandardThreadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, int queueCapactiy) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapactiy, Executors.defaultThreadFactory());
    }

    public StandardThreadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, int queueCapactiy, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapactiy, threadFactory, new AbortPolicy());
    }

    public StandardThreadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, int queueCapactiy, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ExecutorQueue(), threadFactory, handler);
        ((ExecutorQueue)getQueue()).setStandardThreadExecutor(this);

        submittedTasksCount = new AtomicInteger(0);
        maxSubmittedTaskCount = queueCapactiy + maximumPoolSize;
    }

    @Override
    public void execute(Runnable command) {
        int count = submittedTasksCount.incrementAndGet();

        if (count > maxSubmittedTaskCount) {
            submittedTasksCount.decrementAndGet();
            getRejectedExecutionHandler().rejectedExecution(command, this);
        }

        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            if (!((ExecutorQueue)getQueue()).force(command)) {
                submittedTasksCount.decrementAndGet();
                getRejectedExecutionHandler().rejectedExecution(command, this);
            }
            rx.printStackTrace();
        }
    }

    public int getSubmittedTasksCount() {
        return submittedTasksCount.get();
    }

    protected void afterExecutor(Runnable r, Throwable t) {
        submittedTasksCount.decrementAndGet();
    }
}
