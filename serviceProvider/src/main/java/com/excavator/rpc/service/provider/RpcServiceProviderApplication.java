package com.excavator.rpc.service.provider;

import com.excavator.rpc.service.SayService;
import com.excavator.rpc.service.provider.impl.SayServiceImpl;
import com.excavator.rpc.factory.ServerFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by cmonkey on 3/29/17.
 */
@SpringBootApplication
public class RpcServiceProviderApplication {

    @Value("${zkConnection}")
    private String zkConnection;

    @Value("${serviceName}")
    private String serviceName;

    @Bean
    public SayService sayService(){
        return new SayServiceImpl();
    }
    @Bean
    public ServerFactoryBean serverFactoryBean(){

        final ServerFactoryBean serverFactoryBean = new ServerFactoryBean();
        serverFactoryBean.setPort(9090);
        serverFactoryBean.setServiceInterface(SayService.class);
        serverFactoryBean.setServiceName(serviceName);
        serverFactoryBean.setServiceImpl(sayService());
        serverFactoryBean.setZkConn(zkConnection);

        new Thread(() -> {

            serverFactoryBean.start();;
        }, "RpcServer").start();

        return serverFactoryBean;
    }

    public static void main(String[] args){
        SpringApplication.run(RpcServiceProviderApplication.class);
    }
}
