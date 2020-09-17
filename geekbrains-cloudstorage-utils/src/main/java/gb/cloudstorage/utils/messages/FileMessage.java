package gb.cloudstorage.utils.messages;

import gb.cloudstorage.utils.processing.MessageProcessingContext;
import gb.cloudstorage.utils.processing.MessageProcessingResult;
import gb.cloudstorage.utils.processing.ProcessingMessage;

import java.io.IOException;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage implements ProcessingMessage {
    private String filename;
    private long position;
    private long partLength;
    private long fileSize;
    private String operatingFolder;

    public String getFilename() {
        return filename;
    }

    public FileMessage(Path path, long position, long partLength, long fileSize) throws IOException {
        filename = path.getFileName().toString();
        this.position = position;
        this.partLength = partLength;
        this.fileSize = fileSize;
    }

    public FileMessage(Path path, long position, long partLength, long fileSize, String operatingFolder) throws IOException {
        filename = path.getFileName().toString();
        this.position = position;
        this.partLength = partLength;
        this.fileSize = fileSize;
        this.operatingFolder = operatingFolder;
    }

    public long getPosition() {
        return position;
    }

    public long getLength() {
        return partLength;
    }

    public String getOperatingFolder() {
        return operatingFolder;
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        String message = this.getFilename() + ": " + ((this.position + this.partLength) * 100 / this.fileSize) + "%";
        return new MessageProcessingResult(new LogMessage(message));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        String message = this.getFilename() + ": " + ((this.position + this.partLength) * 100 / this.fileSize) + "%";
        System.out.println(message);
        return new MessageProcessingResult(new LogMessage(message));
    }
}