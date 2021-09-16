package io.basquiat.common.configuration;

import io.basquiat.common.listener.QueueCacheListener;
import io.basquiat.common.properties.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * rabbitMQ configuration
 * created by basquiat
 *
 */
@Configuration
@RequiredArgsConstructor
public class MQConfig {

    private final RabbitMqProperties rabbitmq;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitmq.getHost(), rabbitmq.getPort());
        connectionFactory.setUsername(rabbitmq.getUserId());
        connectionFactory.setPassword(rabbitmq.getPassword());
        connectionFactory.setVirtualHost(rabbitmq.getVirtualHost());
        return connectionFactory;
    }

    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(rabbitmq.getExchange());
    }

    @Bean
    public SimpleMessageListenerContainer cacheContainer(ConnectionFactory connectionFactory, @Qualifier("queueCacheListenerAdapter") MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(rabbitmq.getCache());
        container.setQueues(queueCache());
        container.setMessageListener(listenerAdapter);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        container.setForceCloseChannel(false);
        return container;
    }

    @Bean
    public MessageListenerAdapter queueCacheListenerAdapter(QueueCacheListener receiver) {
        return new MessageListenerAdapter(receiver, "queueCache");
    }

    @Bean
    public QueueCacheListener queueCacheListener() {
        return new QueueCacheListener();
    }

    @Bean
    public Queue queueCache() {
        return new Queue(rabbitmq.getCache());
    }

    @Bean
    public Binding queueCacheBinding() {
        return BindingBuilder.bind(queueCache()).to(exchange()).with(queueCache().getName());
    }

}
