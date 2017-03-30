package com.excavator.rpc.service.consumer;

import com.excavator.rpc.factory.ClientFactoryBean;
import com.excavator.rpc.service.SayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@SpringBootApplication
public class RpcServiceConsumerApplication {

    @Value("${zkConnection}")
    private String zkConnection;

    @Value("${serviceName}")
    private String serviceName;

    @Bean
    public SayService clientFactoryBean() throws Exception{
        ClientFactoryBean<SayService> clientFactoryBean = new ClientFactoryBean<>();
        clientFactoryBean.setZkConn(zkConnection);
        clientFactoryBean.setServiceName(serviceName);
        clientFactoryBean.setServiceInterface(SayService.class);

        return clientFactoryBean.getObject();
    }

    @Resource
    private SayService sayService;
    
    @RequestMapping("/hello")
    public String hello(String msg){
        return sayService.say(msg);
    }

    public static void main (String [] args) {
        SpringApplication.run(RpcServiceConsumerApplication.class, );
    }
}
