package gb.cloudstorage.utils.handlers;

import gb.cloudstorage.utils.AuthMaker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import gb.cloudstorage.utils.messages.FileMessage;

import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BytesToFilesAndMessagesDecoder extends ByteToMessageDecoder {
    private String operatingFolder;
    private AuthMaker authMaker = null;

    public BytesToFilesAndMessagesDecoder(String operatingFolder) {
        this.operatingFolder = operatingFolder;
    }

    public BytesToFilesAndMessagesDecoder(String operatingFolder, AuthMaker authMaker) {
        this.operatingFolder = operatingFolder;
        this.authMaker = authMaker;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 8) {
            in.markReaderIndex();
            int messageSize = in.readInt();
            int fileSize = in.readInt();
            if (in.readableBytes() >= messageSize + fileSize) {
                byte[] data = new byte[messageSize];
                in.readBytes(data);
                Object message = (new ObjectDecoderInputStream(new ByteArrayInputStream(data))).readObject();
                if (fileSize > 0) {
                    FileMessage fileMessage = (FileMessage) message;
                    String filePath;
                    if (authMaker != null) {
                        filePath = operatingFolder + authMaker.getFolderNameForToken(fileMessage.getToken()) + "/" + fileMessage.getFilename();
                    } else {
                        filePath = operatingFolder + fileMessage.getFilename();
                    }
                    if (fileMessage.getPosition() == 0) {
                        Files.deleteIfExists(Paths.get(filePath));
                    }
                    try (RandomAccessFile file = new RandomAccessFile(filePath, "rw")) {
                        FileChannel fileChannel = file.getChannel();
                        fileChannel.position(fileMessage.getPosition());
                        in.readBytes(fileChannel, fileSize);
                    }
                }
                out.add(message);
                in.discardReadBytes();
            } else {
                in.resetReaderIndex();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        channelHandlerContext.close();
    }

}
