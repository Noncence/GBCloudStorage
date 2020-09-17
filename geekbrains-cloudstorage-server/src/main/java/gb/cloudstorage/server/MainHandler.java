package gb.cloudstorage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import gb.cloudstorage.utils.messages.AbstractMessage;
import gb.cloudstorage.utils.messages.FileMessage;
import gb.cloudstorage.utils.messages.FileRequestMessage;
import gb.cloudstorage.utils.messages.LogMessage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainHandler extends ChannelInboundHandlerAdapter {

    private final String mainOperatingFolder;
    private final DatabaseServer databaseServer;
    private final MessageProcessor messageProcessor;

    public MainHandler(String mainOperatingFolder, DatabaseServer databaseServer, MessageProcessor messageProcessor) {
        this.mainOperatingFolder = mainOperatingFolder;
        this.databaseServer = databaseServer;
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        try {
            if (msg instanceof FileRequestMessage) {
                // не нашёл, как обрабатывать это сообщение вне хэндлера, так как нужна возможность отправлять в ответ много сообщений
                FileRequestMessage message = (FileRequestMessage) msg;
                String operatingFolder = mainOperatingFolder + databaseServer.getFolderNameForToken(message.getToken());
                String fileName = operatingFolder + "/" + message.getFilename();
                if (Files.exists(Paths.get(fileName))) {
                    try (RandomAccessFile file = new RandomAccessFile(fileName, "r")) {
                        FileMessage fileMessage;
                        long pos = 0;
                        while (pos < file.length()) {
                            long increment = Math.min(1024 * 1024, file.length() - pos);
                            fileMessage = new FileMessage(Paths.get(fileName), pos, increment, file.length(), operatingFolder);
                            channelHandlerContext.writeAndFlush(fileMessage);
                            pos += increment;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    channelHandlerContext.writeAndFlush(new LogMessage("No such file found"));
                }
            } else {
                channelHandlerContext.writeAndFlush(messageProcessor.process((AbstractMessage) msg));
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}