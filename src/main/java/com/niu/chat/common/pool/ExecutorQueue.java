package com.niu.chat.common.pool;


import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author: justinniu
 * @date: 2018-11-27 15:43
 * @desc:
 **/
public class ExecutorQueue extends LinkedTransferQueue<Runnable> {
    private static final long serialVersionUID = -265236426751004839L;

    private StandardThreadExecutor threadPoolExecutor;

    public ExecutorQueue() {
        super();
    }
    public void setStandardThreadExecutor(StandardThreadExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public boolean force(Runnable o) {
        if (threadPoolExecutor.isShutdown()) {
            throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
        }
       return super.offer(o);
    }

    /**
     * 先填充最大线程池，最大线程池满了
     *
     * ranh
     * @param o
     * @return
     */
    public boolean offer(Runnable o) {
        int poolSize = threadPoolExecutor.getPoolSize();

        if (poolSize == threadPoolExecutor.getMaximumPoolSize()) {
            return super.offer(o);
        }

        if (poolSize >= threadPoolExecutor.getSubmittedTasksCount()) {
            return super.offer(o);
        }
        if (poolSize < threadPoolExecutor.getMaximumPoolSize()) {
            return false;
        }
        return super.offer(o);
    }

}
