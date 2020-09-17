package gb.cloudstorage.server;

import gb.cloudstorage.utils.messages.AbstractMessage;
import gb.cloudstorage.utils.messages.AuthenticationMessage;
import gb.cloudstorage.utils.messages.LogMessage;
import gb.cloudstorage.utils.messages.TokenMessage;
import gb.cloudstorage.utils.processing.MessageProcessingContext;
import gb.cloudstorage.utils.processing.MessageProcessingResult;
import gb.cloudstorage.utils.processing.ProcessingMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class MessageProcessor {

    private final String FOLDER;
    private final DatabaseServer databaseServer;

    public MessageProcessor(String folder, DatabaseServer databaseServer) {
        this.FOLDER = folder;
        this.databaseServer = databaseServer;
    }

    public String getOperatingFolder() {
        return FOLDER;
    }

    public AbstractMessage process(AbstractMessage incomingMessage) {
        if (incomingMessage.isAuthenticationMessage()) { // обрабатываем запросы на аутентификацию, отправляем клиенту токен
            AuthenticationMessage authenticationMessage = (AuthenticationMessage) incomingMessage;
            String token = null;
            try {
                token = databaseServer.getToken(authenticationMessage.getLogin(), authenticationMessage.getPassword());
            } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                return new LogMessage("Unexpected error");
            }
            if (token == null) {
                return new LogMessage("Wrong login or password");
            }
            return new TokenMessage(token);
        }

        String operatingFolderPath;
        // тут делаем аутентификацию отдельного сообщения по токену
        String userFolderName = databaseServer.getFolderNameForToken(incomingMessage.getToken());
        if (userFolderName == null) {
            return new LogMessage("No authentication");
        }
        operatingFolderPath = FOLDER + userFolderName;

        if (Files.notExists(Paths.get(operatingFolderPath))) { // если у юзера нет директории, то создаём
            try {
                Files.createDirectory(Paths.get(operatingFolderPath));
            } catch (IOException e) {
                e.printStackTrace();
                return new LogMessage("Unexpected error");
            }
        }

        // обрабатываем сообщение
        MessageProcessingContext messageProcessingContext = new MessageProcessingContext(operatingFolderPath);
        MessageProcessingResult result = ((ProcessingMessage) incomingMessage).processOnServer(messageProcessingContext);
        if (result != null) {
            return result.getAnswerMessage();
        }
        return new LogMessage("Unexpected error");
    }

}
