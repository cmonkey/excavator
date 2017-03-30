package com.excavator.rpc.service.provider.impl;

import com.excavator.rpc.service.MathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cmonkey on 3/29/17.
 */
public class MathServiceImpl implements MathService{
    private static final Logger logger = LoggerFactory.getLogger(MathService.class);

    public int sum(int a, int b) {
        long startTime = System.nanoTime();

        logger.info("sum param = {}, {}", a, b);

        int result =  a + b;

        logger.info("sum result = {}, time = {}", result, (System.nanoTime() - startTime));

        return result;
    }

    public int max(Integer a, Integer b) {
        long startTime = System.nanoTime();

        logger.info("max param = {}, {}", a, b);

        int result =  a <= b ? b : a;

        logger.info("max result = {}, time = {}", result, (System.nanoTime() - startTime));

        return result;
    }
}
