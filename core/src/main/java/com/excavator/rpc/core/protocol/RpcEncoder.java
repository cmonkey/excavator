package com.excavator.rpc.core.protocol;

import com.excavator.rpc.core.serializer.Serializer;
import com.excavator.rpc.core.serializer.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by cmonkey on 3/28/17.
 */
public class RpcEncoder extends MessageToByteEncoder<Object>{
    private Serializer serializer = new KryoSerializer();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {

        byte[] bytes = serializer.serialize(msg);
        int length = bytes.length;
        out.writeInt(length);
        out.writeBytes(bytes);
    }
}
