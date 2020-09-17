package gb.cloudstorage.utils.messages;

public class AuthenticationMessage extends AbstractMessage {

    private String login;
    private String password;

    public AuthenticationMessage(String login, String password) {
        setAuthenticationMessage();
        this.login = login;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

}
