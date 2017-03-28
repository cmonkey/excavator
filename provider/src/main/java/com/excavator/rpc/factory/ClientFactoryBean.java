package com.excavator.rpc.factory;

import com.excavator.rpc.core.bootstrap.ClientBuilder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by cmonkey on 3/29/17.
 */
@Data
public class ClientFactoryBean<T> implements FactoryBean<T>{

    private static final Logger logger = LoggerFactory.getLogger(ClientFactoryBean.class);
    private Class<T> serviceInterface;
    private String serviceName;
    private String zkConn;

    @Override
    public T getObject() throws Exception{
        return ClientBuilder
                .<T>builder()
                .zkConn(zkConn)
                .serviceName(serviceName)
                .serviceInterface(serviceInterface)
                .build();
    }

    @Override
    public Class<?> getObjectType(){
        return serviceInterface;
    }

    @Override
    public boolean isSingleton(){
        return true;
    }

}
