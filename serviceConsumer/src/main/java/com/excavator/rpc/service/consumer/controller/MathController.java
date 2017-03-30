package com.excavator.rpc.service.consumer.controller;

import com.excavator.rpc.service.MathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by cmonkey on 17-3-30.
 */
@RestController
public class MathController {

    private static final Logger logger = LoggerFactory.getLogger(MathController.class);

    @Resource
    private MathService mathService;

    @GetMapping("/math/sum/{a}/{b}")
    public String sum(@PathVariable int a , @PathVariable int b){
        int sum = mathService.sum(a, b);
        logger.info("math sum result = {}", sum);

        return String.valueOf(sum);
    }
}
