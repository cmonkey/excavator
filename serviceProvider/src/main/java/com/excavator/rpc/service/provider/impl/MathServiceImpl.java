package com.excavator.rpc.service.provider.impl;

import com.excavator.rpc.service.MathService;

/**
 * Created by cmonkey on 3/29/17.
 */
public class MathServiceImpl implements MathService{

    public int sum(int a, int b) {
        return a + b;
    }

    public int max(Integer a, Integer b) {
        return a <= b ? b : a;
    }
}
