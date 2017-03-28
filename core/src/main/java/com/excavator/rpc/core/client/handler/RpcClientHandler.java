package com.excavator.rpc.core.client.handler;
import com.excavator.rpc.core.utils.ResponseMapHelper;
import com.excavator.rpc.core.protocol.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Created by cmonkey on 3/28/17.
 */
@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<Response>{

    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {

        BlockingQueue<Response> blockingQueue = ResponseMapHelper.responseMap.get(response.getRequestId());

        if(null != blockingQueue){
            blockingQueue.put(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception{
        logger.error("Exception caught on {}", context.channel(), cause);
        context.channel().close();
    }
}
