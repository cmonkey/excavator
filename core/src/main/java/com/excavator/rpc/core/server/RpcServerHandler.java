package com.excavator.rpc.core.server;

import com.excavator.rpc.core.protocol.Request;
import com.excavator.rpc.core.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Created by cmonkey on 3/28/17.
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<Request>{
    private Object service;

    public RpcServerHandler(Object service){
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        String methodName = request.getMethod();
        Object[] params = request.getParams();
        Class<?>[] parameterType = request.getParameterTypes();
        long requestId = request.getRequestId();

        // 通过反射来获取客户端所要掉哟给你的方法并执行
        Method method = service.getClass().getDeclaredMethod(methodName, parameterType);
        method.setAccessible(true);
        Object invoke = method.invoke(service, params);
        Response response = new Response();
        response.setRequestId(requestId);
        response.setResponse(invoke);

        channelHandlerContext.pipeline().writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause)throws Exception{
        log.error("Exception caught on {}", context.channel(), cause);
        context.channel().close();
    }
}
