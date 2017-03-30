package com.excavator.rpc.server.provider.service;

/**
 * Created by cmonkey on 3/29/17.
 */
public interface SayService {

    String say(String hello);

    int sum(int a, int b);

    int max(Integer a, Integer b);
}
