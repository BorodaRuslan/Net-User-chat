package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Client {
    static final String HOST = "localhost";
    static final int PORT = 8080;

    public static void main(String[] args) throws Exception{
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());

                            pipeline.addLast(new ClientHandler());

                        }

                    });
            ChannelFuture future = bootstrap.bind(HOST,PORT).sync();

            String input = "Ашот";
            Channel channel = future.sync().channel();
            channel.writeAndFlush(input);
            channel.flush();

            future.channel().closeFuture().sync();

        } finally {
            worker.shutdownGracefully();
        }

    }
}
