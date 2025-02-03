package com.sap.periziafacile.pfgateway.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class OrderMessageConfig {

    public static String EXCHANGE_NAME = "orderservice.exchange";

    public static String ROUTING_KEY = "orderservice.routingKey";

    public static String QUEUE_NAME = "orderservice.queue";

    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ROUTING_KEY); // Binding tra la coda e l'exchange
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_NAME); // Dichiarazione dell'exchange
    }

}
