package com.niu.chat.bootstrap.scan;

import com.niu.chat.bootstrap.bean.SendMqttMessage;
import com.niu.chat.common.enums.ConfirmStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: justinniu
 * @date: 2018-11-30 10:11
 * @desc:
 **/
@Slf4j
public abstract class ScanRunnable implements Runnable{

    LinkedBlockingQueue<SendMqttMessage> queue =new LinkedBlockingQueue();

    public  boolean addQueue(SendMqttMessage t){
        return queue.add(t);
    }

    public  boolean addQueues(List<SendMqttMessage> ts){
        return queue.addAll(ts);
    }

    @Override
    public void run() {
        for(;;){
            try {
                SendMqttMessage  poll= queue.take();
                if(poll.getConfirmStatus()!= ConfirmStatus.COMPLETE){
                    doInfo(poll);
                    queue.offer(poll);
                }
            } catch (InterruptedException e) {
                log.error("scan InterruptedException",e);
            }
        }
    }
    public  abstract  void  doInfo( SendMqttMessage poll);
}
