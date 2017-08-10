package com.papajohns.online.orderhistory.service;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Service
public class OrderHistoryWebFeedService {

    private static final Logger logger = LoggerFactory.getLogger(OrderHistoryWebFeedService.class);

    @Value("#{environment.projectID}")
    private String projectId;

    @Value("#{environment.subscriptionName}")
    private String subscriptionId;

    @Value("#{environment.medalliaHost}")
    private String medalliaHost;

    @Value("#{environment.medalliaUser}")
    private String medalliaUserName;

    @Value("#{environment.medalliaPassword}")
    private String medalliaPassword;

    @Scheduled(fixedDelayString = "${delayInterval}")
    public void subscriberCheck(){
        logger.info("Checking for subscriber ");
        try{
            readSubscriber();
        }catch (Exception e){
            logger.error("Pulling from subscriber failed");
        }
    }

    public void readSubscriber()throws Exception {
        logger.info("In readSubscriber "+projectId+"   "+ subscriptionId);
        SubscriptionName subscriptionName = SubscriptionName.create(projectId, subscriptionId);
        MessageReceiver receiver = new MessageReceiver() {
            @Override
            public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
                logger.info("Received Data : " + message.getData().toStringUtf8());
                postPayload(message.getData().toStringUtf8());
                consumer.ack();
            }
        };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.defaultBuilder(subscriptionName, receiver).build();
            subscriber.startAsync();
            Thread.sleep(20000);
       }finally {
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
    }

    public void postPayload(String payLoad){
        HttpEntity<String> request = new HttpEntity<>(payLoad,createHeaders(medalliaUserName, medalliaPassword));
        RestTemplate rt = new RestTemplate();
        //rt.getInterceptors().add(new BasicAuthorizationInterceptor(medalliaUserName, medalliaPassword));
        ResponseEntity<HttpStatus> status = rt.exchange(medalliaHost, HttpMethod.POST,  request, HttpStatus.class);
        logger.info("Returned status :"+status);
    }

    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

}
