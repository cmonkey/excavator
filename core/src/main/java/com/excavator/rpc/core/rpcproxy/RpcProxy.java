package com.excavator.rpc.core.rpcproxy;

import com.excavator.rpc.core.client.Client;

/**
 * Created by cmonkey on 3/28/17.
 */
public interface RpcProxy {
    <T> T proxyInterface(Client client, final Class<T> serviceInterface);
}
