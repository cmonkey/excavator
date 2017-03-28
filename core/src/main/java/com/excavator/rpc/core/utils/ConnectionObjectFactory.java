package com.excavator.rpc.core.utils;

import com.excavator.rpc.core.client.handler.RpcClientHandler;
import com.excavator.rpc.core.protocol.RpcDecoder;
import com.excavator.rpc.core.protocol.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cmonkey on 3/28/17.
 */
public class ConnectionObjectFactory extends BasePooledObjectFactory<Channel>{
    private static final Logger logger = LoggerFactory.getLogger(ConnectionObjectFactory.class);
    private String ip;
    private int port;

    public ConnectionObjectFactory(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    private Channel createNewConChannel(){
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup(1))
                .handler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new RpcDecoder(10* 1024 * 1024))
                                .addLast(new RpcEncoder())
                                .addLast(new RpcClientHandler());
                    }
                });

        try{

            final ChannelFuture f = bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .connect(ip, port).sync();
            f.addListener((ChannelFutureListener) future -> {
                if(future.isSuccess()){
                    logger.info("connect success = {}", f);
                }
            });

            final Channel channel = f.channel();
            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                logger.info("channel close = {} {}",ip, port);
            });

            return channel;
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Channel create()throws Exception{
        return createNewConChannel();
    }

    @Override
    public PooledObject<Channel> wrap(Channel obj){
        return new DefaultPooledObject<Channel>(obj);
    }

    @Override
    public void destroyObject(PooledObject<Channel> p) throws Exception{
        p.getObject().close().addListener((ChannelFutureListener)future -> {
            logger.info("close finish");
        });
    }

    @Override
    public boolean validateObject(PooledObject<Channel> p){
        Channel object = p.getObject();
        return object.isActive();
    }
}
