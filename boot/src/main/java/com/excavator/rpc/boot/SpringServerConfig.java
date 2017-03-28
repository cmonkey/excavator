package com.excavator.rpc.boot;

import com.excavator.rpc.boot.service.SayService;
import com.excavator.rpc.boot.service.impl.SayServiceImpl;
import com.excavator.rpc.factory.ServerFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by cmonkey on 3/29/17.
 */
@SpringBootApplication
public class SpringServerConfig {

    @Bean
    public SayService hello(){
        return new SayServiceImpl();
    }
    @Bean
    public ServerFactoryBean serverFactoryBean(){

        final ServerFactoryBean serverFactoryBean = new ServerFactoryBean();
        serverFactoryBean.setPort(9090);
        serverFactoryBean.setServiceInterface(SayService.class);
        serverFactoryBean.setServiceName("hello");
        serverFactoryBean.setServiceImpl(hello());
        serverFactoryBean.setZkConn("127.0.0.1:2181");

        new Thread(() -> {

            serverFactoryBean.start();;
        }, "RpcServer").start();

        return serverFactoryBean;
    }

    public static void main(String[] args){
        SpringApplication.run(SpringServerConfig.class, "--server.port=8082");
    }
}
