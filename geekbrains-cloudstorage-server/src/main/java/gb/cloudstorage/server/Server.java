package gb.cloudstorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import gb.cloudstorage.utils.handlers.BytesToFilesAndMessagesDecoder;
import gb.cloudstorage.utils.handlers.FilesAndMessagesToBytesEncoder;

public class Server {

    private final MessageProcessor messageProcessor;
    private final DatabaseServer databaseServer;

    public Server(DatabaseServer databaseServer, MessageProcessor messageProcessor) {
        this.databaseServer = databaseServer;
        this.messageProcessor = messageProcessor;
    }

    public void run() throws Exception {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        databaseServer.connect();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new BytesToFilesAndMessagesDecoder(messageProcessor.getOperatingFolder(), databaseServer),
                                    new FilesAndMessagesToBytesEncoder(),
                                    new MainHandler(messageProcessor.getOperatingFolder(), databaseServer, messageProcessor)
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(8189).sync();
            future.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            databaseServer.disconnect();
        }
    }
}
