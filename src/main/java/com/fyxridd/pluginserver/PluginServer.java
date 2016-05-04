package com.fyxridd.pluginserver;

import com.fyxridd.netty.common.message.coder.MessageDecoder;
import com.fyxridd.netty.common.message.coder.MessageEncoder;
import com.fyxridd.netty.common.message.v1.Ver1Message;
import com.fyxridd.netty.common.message.debug.MessageDebugDecoder;
import com.fyxridd.netty.common.message.debug.MessageDebugEncoder;
import com.fyxridd.netty.common.util.Util;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class PluginServer implements InitializingBean {
    @Autowired
    private Config config;

    @Override
    public void afterPropertiesSet() throws Exception {
        Util.log(">>>PluginServer");

        assert config == null;

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //In
                                    .addLast(new MessageDebugDecoder(), new MessageDecoder() {
                                        @Override
                                        protected void handle(Ver1Message msg) {

                                        }
                                    })
                                    //Out
                                    .addLast(new MessageEncoder(), new MessageDebugEncoder());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            b.bind(config.getPort()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    future.channel().closeFuture();
                }
            });
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
