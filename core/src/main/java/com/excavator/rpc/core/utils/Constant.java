package com.excavator.rpc.core.utils;

/**
 * Created by cmonkey on 3/29/17.
 */
public interface Constant {
    int ZK_SESSION_TIMEOUT = 5000;

    int MAX_FRAME_LENGTH = 1024 * 1024; // 1MB

    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/services/";
}
