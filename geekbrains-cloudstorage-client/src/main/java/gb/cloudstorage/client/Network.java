package gb.cloudstorage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import gb.cloudstorage.utils.handlers.BytesToFilesAndMessagesDecoder;
import gb.cloudstorage.utils.handlers.FilesAndMessagesToBytesEncoder;
import gb.cloudstorage.utils.messages.AbstractMessage;


public class Network {
    private static String token = null;
    private static NioEventLoopGroup workerGroup;
    private static Bootstrap bootstrap;
    private static Channel channel;


    public static void start() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new BytesToFilesAndMessagesDecoder(""),
                        new FilesAndMessagesToBytesEncoder(),
                        new TokenOutboundHandler(),
                        new ClientMainHandler()

                );
            }
        });
        ChannelFuture channelFuture = bootstrap.connect("localhost", 8189);
        channel = channelFuture.channel();
    }

    public static void stop() {
        workerGroup.shutdownGracefully();
    }

    public static void sendMessage(AbstractMessage message) {
        channel.writeAndFlush(message);
    }

    public static void setToken(String tokenToBeSet) {
        token = tokenToBeSet;
    }

    protected static boolean hasToken() {
        return token != null;
    }

    protected static String getToken() {
        return token;
    }

}