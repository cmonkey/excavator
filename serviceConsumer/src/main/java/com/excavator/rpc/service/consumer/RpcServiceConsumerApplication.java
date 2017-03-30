package com.excavator.rpc.service.consumer;

import com.excavator.rpc.factory.ClientFactoryBean;
import com.excavator.rpc.service.MathService;
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
    public MathService clientFactoryBean() throws Exception{
        ClientFactoryBean<MathService> clientFactoryBean = new ClientFactoryBean<>();
        clientFactoryBean.setZkConn(zkConnection);
        clientFactoryBean.setServiceName(serviceName);
        clientFactoryBean.setServiceInterface(MathService.class);

        return clientFactoryBean.getObject();
    }



    public static void main (String [] args) {
        SpringApplication.run(RpcServiceConsumerApplication.class);
    }
}
