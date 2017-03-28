package com.excavator.rpc.core.protocol;

import com.excavator.rpc.core.serializer.Serializer;
import com.excavator.rpc.core.serializer.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cmonkey on 3/28/17.
 */
public class RpcDecoder extends LengthFieldBasedFrameDecoder{
    private static final Logger logger = LoggerFactory.getLogger(RpcDecoder.class);
    private Serializer serializer = new KryoSerializer();
    public RpcDecoder(int maxFrameLength){
        super(maxFrameLength, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext context, ByteBuf in)throws Exception{
        ByteBuf decode = (ByteBuf)super.decode(context, in);

        if(null != decode){
            int byteLength = decode.readableBytes();
            byte[] byteHolder = new byte[byteLength];
            decode.readBytes(byteHolder);
            Object deserialize = serializer.deserialize(byteHolder);
            return deserialize;
        }

        logger.info("Decoder result is null");

        return null;
    }
}
