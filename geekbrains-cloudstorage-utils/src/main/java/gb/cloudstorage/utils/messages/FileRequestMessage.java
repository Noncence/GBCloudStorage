package gb.cloudstorage.utils.messages;

import gb.cloudstorage.utils.processing.MessageProcessingContext;
import gb.cloudstorage.utils.processing.MessageProcessingResult;
import gb.cloudstorage.utils.processing.ProcessingMessage;

public class FileRequestMessage extends AbstractMessage implements ProcessingMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRequestMessage(String filename) {
        this.filename = filename;
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        return null;
    }


    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        return null;
    }
}