package com.excavator.rpc.service.provider.impl;

import com.excavator.rpc.service.MathService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by cmonkey on 3/29/17.
 */
@Slf4j
public class MathServiceImpl implements MathService{

    public int sum(int a, int b) {
        long startTime = System.nanoTime();

        log.info("sum param = {}, {}", a, b);

        int result =  a + b;

        log.info("sum result = {}, time = {}", result, (System.nanoTime() - startTime));

        return result;
    }

    public int max(Integer a, Integer b) {
        long startTime = System.nanoTime();

        log.info("max param = {}, {}", a, b);

        int result =  a <= b ? b : a;

        log.info("max result = {}, time = {}", result, (System.nanoTime() - startTime));

        return result;
    }
}
