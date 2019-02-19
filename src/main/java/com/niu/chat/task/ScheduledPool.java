package com.niu.chat.task;

import com.niu.chat.common.pool.Scheduled;
import com.niu.chat.common.properties.InitNetty;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * @author 牛贞昊（niuzhenhao@58.com）
 * @date 2019/2/18 14:23
 * @desc
 */
@Service
public class ScheduledPool implements Scheduled {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);

    private final InitNetty serverBean;

    public ScheduledPool(InitNetty serverBean) {
        this.serverBean = serverBean;
    }

    @Override
    public ScheduledFuture<?> submit(Runnable runnable) {
        int initalDelay = serverBean.getInitalDelay();
        int period = serverBean.getPeriod();
        return scheduledExecutorService.scheduleAtFixedRate(runnable, initalDelay, period, TimeUnit.SECONDS);
    }
}
