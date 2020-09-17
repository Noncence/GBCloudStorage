package gb.cloudstorage.client;

import gb.cloudstorage.utils.messages.AbstractMessage;
import gb.cloudstorage.utils.processing.MessageProcessingContext;
import gb.cloudstorage.utils.processing.MessageProcessingResult;
import gb.cloudstorage.utils.processing.ProcessingMessage;

public class ClientMessageProcessor {

    private static int processedCount = 0;

    public static MessageProcessingResult process(AbstractMessage incomingMessage) {
        ProcessingMessage processingMessage = (ProcessingMessage) incomingMessage;
        MessageProcessingContext messageProcessingContext = new MessageProcessingContext();
        MessageProcessingResult messageProcessingResult = processingMessage.processOnClient(messageProcessingContext);
        if (messageProcessingResult.getNewToken() != null) {
            Network.setToken(messageProcessingResult.getNewToken());
        }
        processedCount++;
        return messageProcessingResult;
    }

    public static int getProcessedCount() {
        return processedCount;
    }
}
