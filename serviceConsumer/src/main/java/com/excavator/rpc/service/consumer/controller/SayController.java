package com.excavator.rpc.service.consumer.controller;

import com.excavator.rpc.service.SayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by cmonkey on 17-3-30.
 */
@RestController
public class SayController {

    private static final Logger logger = LoggerFactory.getLogger(SayController.class);

    @Resource
    private SayService sayService;

    @GetMapping("/hello/{msg}")
    public String hello(@PathVariable String msg){
        String result = sayService.say(msg);
        logger.info("hello result = {}", result);

        return result;
    }
}
