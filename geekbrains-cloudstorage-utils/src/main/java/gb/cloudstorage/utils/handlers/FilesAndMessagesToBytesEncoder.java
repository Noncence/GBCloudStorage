package gb.cloudstorage.utils.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import gb.cloudstorage.utils.messages.FileMessage;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class FilesAndMessagesToBytesEncoder extends MessageToByteEncoder {

    private final static byte[] ZERO = ByteBuffer.allocate(4).putInt(0).array();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectEncoderOutputStream oos = new ObjectEncoderOutputStream(bos);
        oos.writeObject(message);
        oos.flush();
        byte[] data = bos.toByteArray();
        byte[] size = ByteBuffer.allocate(4).putInt(data.length).array();
        out.writeBytes(size);
        if (message instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) message;
            String filePath = "";
            if (fileMessage.getOperatingFolder() != null && !fileMessage.getOperatingFolder().equals("")) {
                filePath = fileMessage.getOperatingFolder() + "/";
            }
            filePath = filePath + fileMessage.getFilename();
            try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
                file.seek(fileMessage.getPosition());
                byte[] fileSize = ByteBuffer.allocate(4).putInt((int) (fileMessage.getLength())).array();
                out.writeBytes(fileSize);
                out.writeBytes(data);
                for (int i = 0; i < fileMessage.getLength(); i++) {
                    out.writeByte(file.readByte());
                }
            }
        } else {
            out.writeBytes(ZERO);
            out.writeBytes(data);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        channelHandlerContext.close();
    }

}
