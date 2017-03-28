package com.excavator.rpc.core.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by cmonkey on 3/28/17.
 */
@Setter
@Getter
public class Response {
    private long requestId;
    private Object response;
    private Throwable throwable;
}
