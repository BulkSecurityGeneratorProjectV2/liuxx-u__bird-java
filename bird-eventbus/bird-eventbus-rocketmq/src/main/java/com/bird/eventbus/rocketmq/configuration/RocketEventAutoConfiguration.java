package com.bird.eventbus.rocketmq.configuration;

import com.bird.eventbus.configuration.EventCoreAutoConfiguration;
import com.bird.eventbus.handler.EventMethodInvoker;
import com.bird.eventbus.log.IEventLogDispatcher;
import com.bird.eventbus.registry.IEventRegistry;
import com.bird.eventbus.rocketmq.consumer.EventListenerConcurrently;
import com.bird.eventbus.rocketmq.consumer.RocketEventConsumerContainer;
import com.bird.eventbus.rocketmq.consumer.RocketEventHandlerProperties;
import com.bird.eventbus.rocketmq.producer.RocketEventSender;
import com.bird.eventbus.sender.IEventSender;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author liuxx
 * @since 2020/11/25
 */
@Configuration
@EnableConfigurationProperties(RocketEventHandlerProperties.class)
@AutoConfigureAfter({RocketMQAutoConfiguration.class, EventCoreAutoConfiguration.class})
public class RocketEventAutoConfiguration {

    private final Environment environment;
    private final RocketEventHandlerProperties rocketEventHandlerProperties;

    public RocketEventAutoConfiguration(Environment environment, RocketEventHandlerProperties rocketEventHandlerProperties) {
        this.environment = environment;
        this.rocketEventHandlerProperties = rocketEventHandlerProperties;
    }

    @Bean
    @ConditionalOnBean(RocketMQTemplate.class)
    public IEventSender eventSender(RocketMQTemplate rocketTemplate, ObjectProvider<IEventLogDispatcher> logDispatcher) {
        return new RocketEventSender(rocketTemplate, logDispatcher.getIfAvailable());
    }

    @Bean
    public EventListenerConcurrently eventListenerConcurrently(EventMethodInvoker eventMethodInvoker, RocketMQMessageConverter rocketMQMessageConverter) {
        return new EventListenerConcurrently(this.rocketEventHandlerProperties, eventMethodInvoker, rocketMQMessageConverter.getMessageConverter());
    }

    @Bean
    public RocketEventConsumerContainer rocketEventConsumerContainer(EventListenerConcurrently eventListenerConcurrently, IEventRegistry eventRegistry) throws MQClientException {
        RocketEventConsumerContainer consumerContainer = new RocketEventConsumerContainer(environment.getProperty("spring.application.name"), this.rocketEventHandlerProperties, eventListenerConcurrently);
        consumerContainer.initialize(eventRegistry.getAllTopics());
        return consumerContainer;
    }
}
