package com.excavator.rpc.core.protocol;

import lombok.Data;

/**
 * Created by cmonkey on 3/28/17.
 */
@Data
public class Request {

    private long requestId;

    private Class<?> clazz;

    private String method;

    private Class<?>[] parameterTypes;

    private Object[] params;

    private long requestTime;

}
