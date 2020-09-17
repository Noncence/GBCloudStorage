package gb.cloudstorage.utils.messages;

import gb.cloudstorage.utils.processing.MessageProcessingContext;
import gb.cloudstorage.utils.processing.MessageProcessingResult;
import gb.cloudstorage.utils.processing.ProcessingMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileRemoveRequestMessage extends AbstractMessage implements ProcessingMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRemoveRequestMessage(String filename) {
        this.filename = filename;
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        try {
            Files.deleteIfExists(Paths.get(messageProcessingContext.getOperatingFolder() + "/" + this.getFilename()));
        } catch (IOException e) {
            e.printStackTrace();
            return new MessageProcessingResult(new LogMessage("Unexpected error"));
        }
        return new MessageProcessingResult(new LogMessage("The file was removed: " + this.getFilename()));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        throw new RuntimeException("Method not supported exception");
    }
}
