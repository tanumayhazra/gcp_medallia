package com.papajohns.online.orderhistory.controller;

import com.papajohns.online.orderhistory.service.OrderHistoryWebFeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/medallia/order")
public class OrderHistoryController {

    @Inject
    private OrderHistoryWebFeedService orderHistoryWebFeedService;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity storeOrderDetail(@RequestBody String message){
        orderHistoryWebFeedService.postPayload(message);
        ResponseEntity response = new ResponseEntity(HttpStatus.OK);
        return response;
    }

    @RequestMapping(value = "/healthCheck", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String healthCheck(HttpServletRequest request, HttpServletResponse response){
        return "OKAY GOOGLE";
    }



}
