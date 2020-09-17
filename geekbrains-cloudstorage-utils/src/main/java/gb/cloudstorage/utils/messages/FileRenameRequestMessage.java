package gb.cloudstorage.utils.messages;

import gb.cloudstorage.utils.processing.MessageProcessingContext;
import gb.cloudstorage.utils.processing.MessageProcessingResult;
import gb.cloudstorage.utils.processing.ProcessingMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileRenameRequestMessage extends AbstractMessage implements ProcessingMessage {
    private String oldFileName;
    private String newFileName;

    public FileRenameRequestMessage(String oldFileName, String newFileName) {
        this.oldFileName = oldFileName;
        this.newFileName = newFileName;
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        Path newFilePath = Paths.get(messageProcessingContext.getOperatingFolder() + "/" + newFileName);
        Path oldFilePath = Paths.get(messageProcessingContext.getOperatingFolder() + "/" + oldFileName);
        if (!Files.exists(oldFilePath)) {
            return new MessageProcessingResult(new LogMessage("No such file"));
        }
        if (Files.exists(newFilePath)) {
            return new MessageProcessingResult(new LogMessage("File with such name already exists"));
        }
        try {
            Files.move(oldFilePath, newFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return new MessageProcessingResult(new LogMessage("Unexpected error"));
        }
        return new MessageProcessingResult(new LogMessage("The file was renamed"));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        return null;
    }
}
