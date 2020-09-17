package gb.cloudstorage.utils.messages;

import gb.cloudstorage.utils.processing.MessageProcessingContext;
import gb.cloudstorage.utils.processing.MessageProcessingResult;
import gb.cloudstorage.utils.processing.ProcessingMessage;

import java.util.List;

public class FilesListMessage extends AbstractMessage implements ProcessingMessage {

    private List<String> filesList;

    public FilesListMessage(List<String> filesList){
        this.filesList = filesList;
    };

    public List getFilesList() {
        return filesList;
    }


    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext messageProcessingContext) {
        return null;
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext messageProcessingContext) {
        System.out.println("=== " + filesList.size() + " file" + (filesList.size() != 1 ? "s" : "") + ": ===");
        for (String fileName : filesList) {
            System.out.println(fileName);
        }
        System.out.println("===   ===");
        return new MessageProcessingResult();
    }
}
