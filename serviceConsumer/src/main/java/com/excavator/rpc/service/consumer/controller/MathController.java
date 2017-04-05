package com.excavator.rpc.service.consumer.controller;

import com.excavator.rpc.service.MathService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by cmonkey on 17-3-30.
 */
@Slf4j
@RestController
public class MathController {


    @Resource
    private MathService mathService;

    @GetMapping("/math/sum/{a}/{b}")
    public int sum(@PathVariable int a , @PathVariable int b){
        log.info("sum param = {}, {}",a, b);

        int sum = mathService.sum(a, b);

        log.info("math sum result = {}", sum);

        return sum;
    }

    @GetMapping("/math/max/{a}/{b}")
    public int max(@PathVariable int a, @PathVariable int b){
        log.info("max param = {}, {}",a, b);

        int max = mathService.max(a, b);

        log.info("math max result = {}", max);

        return max;
    }
}
