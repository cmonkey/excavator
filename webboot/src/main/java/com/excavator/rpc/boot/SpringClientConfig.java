package com.excavator.rpc.boot;

import com.excavator.rpc.boot.service.SayService;
import com.excavator.rpc.factory.ClientFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Configuration
@RestController
@SpringBootApplication
@RequestMapping("/test")
public class SpringClientConfig{

    String zkConn = "";

    @Bean
    public SayService clientFactoryBean() throws Exception{
        ClientFactoryBean<SayService> clientFactoryBean = new ClientFactoryBean<>();
        clientFactoryBean.setZkConn(zkConn);
        clientFactoryBean.setServiceName("hello");
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
        SpringApplication.run(SpringClientConfig.class, "--server.port=8081");
    }
}
