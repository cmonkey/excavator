package com.excavator.rpc.core.client.impl;

import com.excavator.rpc.core.client.ChannelConfig;
import com.excavator.rpc.core.client.Client;
import com.excavator.rpc.core.exception.RequestTimeoutException;
import com.excavator.rpc.core.protocol.Request;
import com.excavator.rpc.core.protocol.Response;
import com.excavator.rpc.core.rpcproxy.RpcProxy;
import com.excavator.rpc.core.rpcproxy.impl.CglibRpcProxy;
import com.excavator.rpc.core.utils.ResponseMapHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.Collections3;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by cmonkey on 3/28/17.
 */
public class ClientImpl implements Client{
    private static final Logger logger = LoggerFactory.getLogger(ClientImpl.class);
    private static AtomicLong atomicLong = new AtomicLong();

    private String serviceName;
    private int requestTimeoutMillis = 10 * 1000;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
    private String zkConn;
    private CuratorFramework curatorFramework;
    private Class<? extends RpcProxy> clientProxyClass;
    private RpcProxy rpcProxy;

    public static CopyOnWriteArrayList<ChannelConfig> channelWrappers = Lists.newCopyOnWriteArrayList();

    public ClientImpl(String serviceName){
        this.serviceName = serviceName;
    }

    private String getZkConn(){
        return "";
    }

    private static final String ZK_DATA_PATH = "";

    public void init(){
        curatorFramework = CuratorFrameworkFactory.newClient(getZkConn(), new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();
        final GetChildrenBuilder childrenBuilder = curatorFramework.getChildren();

        final String serviceZKPath = ZK_DATA_PATH + serviceName;

        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, serviceZKPath, true);

        try {
            pathChildrenCache.start();
            pathChildrenCache.getListenable().addListener((client, event) -> {

                logger.info("Listen event = {}", event);
                List<String> newServiceData = childrenBuilder.forPath(serviceZKPath);
                logger.info("Server = {} list change = {}", serviceName, newServiceData);

                for(ChannelConfig config : channelWrappers){
                    String connectStr = config.getConnStr();
                    if(!newServiceData.contains(connectStr)){
                        config.close();
                        logger.info("remove channel = {}", connectStr);
                        channelWrappers.remove(config);
                    }
                }

                for(String connStr : newServiceData){
                    boolean containThis = false;
                    for(ChannelConfig config : channelWrappers){
                        if(null != connStr && connStr.equals(config.getConnStr())){
                            containThis = true;
                        }
                    }

                    if(!containThis){
                        addNewChannel(connStr);
                    }
                }
            });

            List<String> strings = childrenBuilder.forPath(serviceZKPath);
            if(Collections3.isEmpty(strings)){
                throw new RuntimeException("no service available for " + serviceName);
            }

            logger.info("found server = {} list = {}", serviceName, strings);

            for(String conStr : strings){
                addNewChannel(conStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNewChannel(String connStr){
        List<String> strings = Splitter.on(":").splitToList(connStr);

        if(strings.size() != 2){
            throw new RuntimeException("error connection str = " +  connStr);
        }

        String host = strings.get(0);
        int port = Integer.parseInt(strings.get(1));
        ChannelConfig channelWrapper = new ChannelConfig(host, port);
        channelWrappers.add(channelWrapper);
        logger.info("add new channel = {}, {}", connStr, channelWrapper);
    }

    private ChannelConfig selectChannel(){
        Random random = new Random();
        int size = channelWrappers.size();

        if(size < 1){
            return null;
        }

        int i = random.nextInt(size);
        return channelWrappers.get(i);
    }
    public Response sendMessage(Class<?> clazz, Method method, Object[] args) {
        Request request = new Request();
        request.setRequestId(atomicLong.incrementAndGet());
        request.setMethod(method.getName());
        request.setParams(args);
        request.setClazz(clazz);
        request.setParameterTypes(method.getParameterTypes());

        ChannelConfig channelWrapper = selectChannel();

        if(null == channelWrapper){
            Response response = new Response();
            RuntimeException runtimeException = new RuntimeException("Channel is not active now");
            response.setThrowable(runtimeException);
            return response;
        }
        Channel channel = null;
        try {
            channel = channelWrapper.getChannelObjectPool().borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(null == channel){
            Response response = new Response();
            RuntimeException runtimeException = new RuntimeException("Channel is avaliable now");
            response.setThrowable(runtimeException);
            return response;
        }

        channel.writeAndFlush(request);
        BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue<Response>(1) ;
        ResponseMapHelper.responseMap.put(request.getRequestId(), blockingQueue);
        try {
            return blockingQueue.poll(requestTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RequestTimeoutException("service" + serviceName  + " method " + method + " timeout");
        }finally {
            try {
                channelWrapper.getChannelObjectPool().returnObject(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ResponseMapHelper.responseMap.remove(request.getRequestId());
        }
    }

    public <T> T proxyInterface(Class<T> serviceInterface) {
        if(clientProxyClass == null){
            clientProxyClass = CglibRpcProxy.class;
        }

        try {
            rpcProxy = clientProxyClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return rpcProxy.proxyInterface(this,serviceInterface);
    }

    @Override
    public void close() {
        if(null != curatorFramework){
            curatorFramework.close();
        }

        try {
            for (ChannelConfig config : channelWrappers) {
                config.close();
            }
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public void setZkConn(String zkConn) {
        this.zkConn = zkConn;
    }
}
