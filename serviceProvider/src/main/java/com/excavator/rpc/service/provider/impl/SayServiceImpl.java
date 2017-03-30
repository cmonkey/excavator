package com.excavator.rpc.service.provider.impl;

import com.excavator.rpc.service.SayService;

/**
 * Created by cmonkey on 3/29/17.
 */
public class SayServiceImpl implements SayService{
    public String say(String hello) {
        return "server: " + hello;
    }

    public int sum(int a, int b) {
        return a + b;
    }

    public int max(Integer a, Integer b) {
        return a <= b ? b : a;
    }
}
