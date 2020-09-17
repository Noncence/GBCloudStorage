package gb.cloudstorage.utils.messages;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    private boolean isAuthenticationMessage = false;
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    protected void setAuthenticationMessage() {
        isAuthenticationMessage = true;
    }

    public boolean isAuthenticationMessage() {
        return isAuthenticationMessage;
    }

}
