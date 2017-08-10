package com.papajohns.online.orderhistory.config;

import com.papajohns.online.orderhistory.service.OrderHistoryWebFeedService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean(name = "orderHistoryWebFeedService")
    public OrderHistoryWebFeedService orderHistoryService() {
        OrderHistoryWebFeedService orderHistoryWebFeedService = new OrderHistoryWebFeedService();
        return orderHistoryWebFeedService;
    }

}
