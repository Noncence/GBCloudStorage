package gb.cloudstorage.client;

import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.out;


public class ConsoleClient {

    public static void main(String[] args) {
        Network.start();
        Scanner in = new Scanner(System.in);
        while (!Network.hasToken()) {
            out.print("Login: ");
            String login = in.nextLine();
            out.print("Password: ");
            String password;
            if (System.console() != null) {
                password = Arrays.toString(System.console().readPassword());
            } else {
                password = in.nextLine();
            }
            Interactions.authenticate(login, password);
        }
        System.out.println("Use only one letter for: show [l]ist of your files, [u]pload, [d]ownload, re[m]ove, re[n]ame, [q]uit");
        String line;
        boolean doesntWantToQuit = true;
        while (doesntWantToQuit) {
            line = in.nextLine().trim().toLowerCase();
            boolean flag = true;
            while (flag) {
                int lineSize = line.length();
                line = line.replaceAll("  ", " ");
                flag = lineSize > line.length();
            }

            String[] words = line.split(" ");
            if (words[0].length() == 0) {
                continue;
            }

            char commandChar = words[0].charAt(0);
            boolean hasProperNumberOfArguments = true;
            switch (commandChar) {
                case ('l'):
                    if (hasNotProperArgumentNumber(words, 1))  {
                        hasProperNumberOfArguments = false;
                        break;
                    }
                    break;
                case ('u'):
                case ('d'):
                case ('m'):
                    if (hasNotProperArgumentNumber(words, 2)) {
                        hasProperNumberOfArguments = false;
                        break;
                    }
                    break;
                case ('n'):
                    if (hasNotProperArgumentNumber(words, 3)) {
                        hasProperNumberOfArguments = false;
                        break;
                    }
                    break;
            }

            if (!hasProperNumberOfArguments) {
                out.println("Wrong amount of arguments");
                continue;
            }
            switch (commandChar) {
                case ('l'):
                    Interactions.receiveFilesList();
                    break;
                case ('u'):
                    Interactions.sendFile(words[1]);
                    break;
                case('d'):
                    Interactions.receiveFile(words[1]);
                    break;
                case('m'):
                    Interactions.removeFile(words[1]);
                    break;
                case ('n'):
                    Interactions.renameFile(words[1], words[2]);
                    break;
                case ('q'):
                    doesntWantToQuit = false;
                    break;
            }
        }
        Network.stop();
    }

    private static boolean hasNotProperArgumentNumber(String[] words, int sizeNeeded) {
        return words.length != sizeNeeded;
    }

}
