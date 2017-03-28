package com.excavator.rpc.core.client;

import com.excavator.rpc.core.protocol.Response;

import java.lang.reflect.Method;

/**
 * Created by cmonkey on 3/28/17.
 */
public interface Client {

    Response sendMessage(Class<?> clazz, Method method , Object[] args);
    <T> T proxyInterface(Class<T> serviceInterface);
    void close();
}
