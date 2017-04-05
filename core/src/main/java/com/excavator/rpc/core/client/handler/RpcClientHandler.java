package com.excavator.rpc.core.client.handler;
import com.excavator.rpc.core.utils.ResponseMapHelper;
import com.excavator.rpc.core.protocol.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

/**
 * Created by cmonkey on 3/28/17.
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<Response>{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {

        BlockingQueue<Response> blockingQueue = ResponseMapHelper.responseMap.get(response.getRequestId());

        if(null != blockingQueue){
            blockingQueue.put(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception{
        log.error("Exception caught on {}", context.channel(), cause);
        context.channel().close();
    }
}
