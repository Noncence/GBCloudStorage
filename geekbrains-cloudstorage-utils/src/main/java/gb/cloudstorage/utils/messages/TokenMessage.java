package gb.cloudstorage.utils.messages;

import gb.cloudstorage.utils.processing.MessageProcessingContext;
import gb.cloudstorage.utils.processing.MessageProcessingResult;
import gb.cloudstorage.utils.processing.ProcessingMessage;

public class TokenMessage extends AbstractMessage implements ProcessingMessage {

    public TokenMessage(String token) {
        this.setToken(token);
    }

    @Override
    public MessageProcessingResult processOnServer(MessageProcessingContext context) {
        return new MessageProcessingResult(new LogMessage("Wrong message type"));
    }

    @Override
    public MessageProcessingResult processOnClient(MessageProcessingContext context) {
        System.out.println("Token received");
        MessageProcessingResult messageProcessingResult = new MessageProcessingResult();
        messageProcessingResult.setNewToken(this.getToken());
        return messageProcessingResult;
    }
}
