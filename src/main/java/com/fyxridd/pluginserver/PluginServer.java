package com.fyxridd.pluginserver;

import com.fyxridd.netty.common.message.MessageDecoder;
import com.fyxridd.netty.common.message.MessageEncoder;
import com.fyxridd.netty.common.message.MessageExtra;
import com.fyxridd.netty.common.message.debug.MessageDebugDecoder;
import com.fyxridd.netty.common.message.debug.MessageDebugEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;

public class PluginServer {
    @Autowired
    private Config config;

    public PluginServer() {
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
                                protected void handle(MessageExtra msg) {

                                }
                            })
                                    //Out
                            .addLast(new MessageEncoder(), new MessageDebugEncoder());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            try {
                ChannelFuture f = b.bind(config.getPort()).sync();

                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
