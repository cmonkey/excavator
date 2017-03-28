package com.excavator.rpc.core.client;

import com.excavator.rpc.core.utils.ConnectionObjectFactory;
import io.netty.channel.Channel;
import lombok.Data;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * Created by cmonkey on 3/28/17.
 */
@Data
public class ChannelConfig {
    private String connStr;
    private String host;
    private int port;
    private Channel channel;
    private ObjectPool<Channel> channelObjectPool;

    public ChannelConfig(String host, int port){
        this.host = host;
        this.port = port;
        this.connStr = host + ":" + port;
        this.channelObjectPool = new GenericObjectPool<Channel>(new ConnectionObjectFactory(host, port));

    }

    public void close(){
        channelObjectPool.close();
    }

    @Override
    public String toString(){
        final StringBuilder builder = new StringBuilder("ChannelConfig{");
        builder.append("conStri=").append(connStr).append("_");
        builder.append("host=").append(host).append("_");
        builder.append("port=").append(port).append("_");
        builder.append("channel=").append(channel).append("_");
        builder.append("channelObjectPool=").append(channelObjectPool).append("_");

        return builder.toString();
    }
}
