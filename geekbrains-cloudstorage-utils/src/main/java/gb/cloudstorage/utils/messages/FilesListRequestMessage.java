package gb.cloudstorage.utils.messages;

import gb.cloudstorage.utils.processing.MessageProcessingContext;
import gb.cloudstorage.utils.processing.MessageProcessingResult;
import gb.cloudstorage.utils.processing.ProcessingMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FilesListRequestMessage extends AbstractMessage implements ProcessingMessage {
    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        String operatingFolder = messageProcessingContext.getOperatingFolder();
        List<String> filesList;
        try {
            filesList = Files.list(Paths.get(operatingFolder)).map(path -> path.getFileName().toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new MessageProcessingResult(new LogMessage("Unexpected error"));
        }
        return new MessageProcessingResult(new FilesListMessage(filesList));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        return null;
    }
}
