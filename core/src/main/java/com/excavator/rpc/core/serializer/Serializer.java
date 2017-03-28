package com.excavator.rpc.core.serializer;

/**
 * Created by cmonkey on 3/28/17.
 */
public interface Serializer {
    byte[] serialize(Object object);
    <T> T deserialize(byte[] bytes);
}
