package com.bird.eventbus.kafka.handler;

import com.bird.eventbus.arg.EventArg;
import com.bird.eventbus.handler.EventHandlerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * kafka事件监听器
 * @author liuxx
 */
public class KafkaEventArgListener implements AcknowledgingMessageListener<String,EventArg> {

    /**
     * 消费者拿到消息时就提交offset,消费失败时在消费者内部重试
     * 避免消费者服务中多个事件处理程序情况下，一个处理程序失败，导致其他处理程序重试的问题
     *
     * @param data
     * @param acknowledgment
     */
    @Override
    public void onMessage(ConsumerRecord<String, EventArg> data, Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();

        if (data == null) return;

        //超过24小时的事件，不进行处理
        EventArg value = data.value();
        Integer span = 24 * 60 * 60 * 1000;
        if (System.currentTimeMillis() - value.getEventTime().getTime() <= span) {
            EventHandlerFactory.handleEvent(value);
        }
    }
}
