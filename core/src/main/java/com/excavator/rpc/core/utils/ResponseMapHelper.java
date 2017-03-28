package com.excavator.rpc.core.utils;

import com.excavator.rpc.core.protocol.Response;
import com.google.common.collect.Maps;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by cmonkey on 3/28/17.
 */
public class ResponseMapHelper {

    public static ConcurrentMap<Long, BlockingQueue<Response>> responseMap = Maps.newConcurrentMap();
}
