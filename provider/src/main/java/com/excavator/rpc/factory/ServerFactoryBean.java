package com.excavator.rpc.factory;

import com.excavator.rpc.core.bootstrap.ServerBuilder;
import com.excavator.rpc.core.server.Server;
import com.excavator.rpc.core.server.impl.ServerImpl;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by cmonkey on 3/29/17.
 */
@Data
public class ServerFactoryBean implements FactoryBean<Object>{
    private Class<?> serviceInterface;
    private Object serviceImpl;
    private String ip;
    private int port;
    private String serviceName;
    private String zkConn;

    private ServerImpl rpcServer;

    public void start(){
        Server rpcServer = ServerBuilder
                .builder()
                .serviceImpl(serviceImpl)
                .serviceName(serviceName)
                .zkConn(zkConn)
                .port(port)
                .build();
        rpcServer.start();
    }

    public void serviceOffline(){
        rpcServer.shutdown();
    }

    @Override
    public Object getObject()throws Exception{
        return this;
    }

    @Override
    public Class<?> getObjectType(){
        return this.getClass();
    }

    @Override
    public boolean isSingleton(){
        return true;
    }
}
