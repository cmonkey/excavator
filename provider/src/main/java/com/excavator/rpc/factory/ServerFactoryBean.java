package com.excavator.rpc.factory;

import com.excavator.rpc.core.bootstrap.ServerBuilder;
import com.excavator.rpc.core.server.Server;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.PreDestroy;

/**
 * Created by cmonkey on 3/29/17.
 */
@Data
@Slf4j
public class ServerFactoryBean implements FactoryBean<Object>{
    private Class<?> serviceInterface;
    private Object serviceImpl;
    private String ip;
    private int port;
    private String serviceName;
    private String zkConn;

    private Server rpcServer;

    public void start(){
        rpcServer = ServerBuilder
                .builder()
                .serviceImpl(serviceImpl)
                .serviceName(serviceName)
                .zkConn(zkConn)
                .port(port)
                .build();
        rpcServer.start();
    }

    @PreDestroy
    public void serviceOffline(){
        log.info("serviceOffline for rpcServer = {}", rpcServer);
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
