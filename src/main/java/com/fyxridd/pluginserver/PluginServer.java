package com.fyxridd.pluginserver;

import com.fyxridd.netty.common.coder.ByteBufToPacketContextDecoder;
import com.fyxridd.netty.common.coder.PacketContextToByteBufEncoder;
import com.fyxridd.netty.common.debug.MessageDebugDecoder;
import com.fyxridd.netty.common.debug.MessageDebugEncoder;
import com.fyxridd.netty.common.packet.PacketContext;
import com.fyxridd.netty.common.util.Util;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class PluginServer implements InitializingBean {
    private static final String NAME_SPACE = "PluginServer";
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
                                    .addLast(new MessageDebugDecoder(), new JdkZlibDecoder(), new ByteBufToPacketContextDecoder(), new SimpleChannelInboundHandler<PacketContext>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, PacketContext msg) throws Exception {

                                        }
                                    })
                                    //Out
                                    .addLast(new MessageDebugEncoder(), new JdkZlibEncoder(), new PacketContextToByteBufEncoder());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            b.bind(config.getPort()).sync().channel().closeFuture().sync();
//                    .addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture future) throws Exception {
//                    future.channel().closeFuture();
//                }
//            });
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
