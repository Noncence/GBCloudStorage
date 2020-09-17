package gb.cloudstorage.client;

import gb.cloudstorage.utils.messages.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Interactions {

    public static void authenticate(String login, String password) {
        AuthenticationMessage authenticationMessage = new AuthenticationMessage(login, password);
        int oldProcessedCount = ClientMessageProcessor.getProcessedCount();
        Network.sendMessage(authenticationMessage);
        while (oldProcessedCount >= ClientMessageProcessor.getProcessedCount()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void sendFile(String fileName) {
        if (!Files.exists(Paths.get(fileName))) {
            System.out.println("File doesn't exist");
            return;
        }
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "r");
            FileMessage fileMessage;
            long pos = 0;
            while (pos < file.length()) {
                long increment = Math.min(1024 * 1024, file.length() - pos);
                fileMessage = new FileMessage(Paths.get(fileName), pos, increment, file.length());
                Network.sendMessage(fileMessage);
                pos += increment;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFile(String fileName) {
        FileRemoveRequestMessage fileRemoveRequestMessage = new FileRemoveRequestMessage(fileName);
        Network.sendMessage(fileRemoveRequestMessage);
    }

    public static void receiveFile(String fileName) {
        FileRequestMessage fileRequestMessage = new FileRequestMessage(fileName);
        Network.sendMessage(fileRequestMessage);
    }

    public static void receiveFilesList() {
        Network.sendMessage(new FilesListRequestMessage());
    }

    public static void renameFile(String oldName, String newName) {
        Network.sendMessage(new FileRenameRequestMessage(oldName, newName));
    }

}
