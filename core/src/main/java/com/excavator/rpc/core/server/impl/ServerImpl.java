package com.excavator.rpc.core.server.impl;

import com.excavator.rpc.core.protocol.RpcDecoder;
import com.excavator.rpc.core.protocol.RpcEncoder;
import com.excavator.rpc.core.server.RpcServerHandler;
import com.excavator.rpc.core.server.Server;
import com.excavator.rpc.core.utils.NetUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

import static com.excavator.rpc.core.utils.Constant.ZK_DATA_PATH;

/**
 * Created by cmonkey on 3/28/17.
 */
@Slf4j
public class ServerImpl implements Server{
    private String localIp;
    private int port;
    private boolean started = false;
    private Channel channel;
    private Object serviceImpl;
    private String serviceName;
    private String zkConn;
    private String serviceRegisterPath;
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private CuratorFramework curatorFramework;

    public ServerImpl(int port, Object serviceImpl, String serviceName){
        this.port = port;
        this.serviceImpl = serviceImpl;
        this.serviceName = serviceName;
    }

    public ServerImpl(int port, Object serviceImpl, String serviceName, String zkConn){
        this.port  = port;
        this.serviceImpl = serviceImpl;
        this.serviceName = serviceName;
        this.zkConn = zkConn;
    }
    @Override
    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new RpcDecoder(10 * 1024 * 1024))
                                .addLast(new RpcEncoder())
                                .addLast(new RpcServerHandler(serviceImpl));
                    }
                });

        // 调用bind 等待客户端链接
        try {
            ChannelFuture future = serverBootstrap.bind(port).sync();
            registerService();
            log.info("Server started at {}",port);
            started = true;
            this.channel = future.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private void registerService(){
        zkConn = getZkConn();
        localIp = NetUtils.getLocalIp();
        String serviceIp = localIp + ":" + port;

        curatorFramework = CuratorFrameworkFactory.newClient(zkConn,
                new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();

        String serviceBasePath = ZK_DATA_PATH + serviceName;

        try {
            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .forPath(serviceBasePath);
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().contains("NodeExist")){
                log.error("this path service has already exist");
            }else{
                log.error("Create Path error = {}", e);
                throw new RuntimeException("Register error");
            }
        }

        boolean registerSuccess = false;

        serviceRegisterPath = serviceBasePath + "/" + serviceIp;

        while(!registerSuccess){

            try {
                curatorFramework.create()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(serviceRegisterPath);
                registerSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                log.info("retry register zk = {}", e.getMessage());

                try {
                    curatorFramework.delete().forPath(serviceRegisterPath);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    @Override
    public void shutdown() {
        log.info("shutting down server = {}", serviceName);
        unRegister();

        if(null != curatorFramework){
            curatorFramework.close();
        }

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private void unRegister(){
        log.info("unRegister zookeeper curator = {}", curatorFramework);
        try {
            curatorFramework.delete().forPath(serviceRegisterPath);
        } catch (Exception e) {
            log.error("unRegister zookeeper curator Exception = {}", e);
            e.printStackTrace();
        }
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setZkConn(String zkConn) {
        this.zkConn = zkConn;
    }

    public String getZkConn(){
        return zkConn;
    }
}
