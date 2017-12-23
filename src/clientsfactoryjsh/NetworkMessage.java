package clientsfactoryjsh;

import java.util.UUID;

import com.google.gson.*;

public class NetworkMessage {

    private String id;
    private String login;
    private String password;
    private String text;
    private Boolean error;

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getText() {
        return text;
    }

    public Boolean getError() {
        return error;
    }

    public NetworkMessage(String login, String password, String text, Boolean error) {
        this.login = login;
        this.password = password;
        this.text = text;
        this.error = error;
        this.id = UUID.randomUUID().toString();
    }

    public String toJSON() {
        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        String jsonText = gson.toJson(this); // serializes target to Json
        return jsonText;
    }

    public void fromJSON (String jsonText){
        Gson gson = new Gson();
        this.id = gson.fromJson(jsonText, NetworkMessage.class).id;
        this.login = gson.fromJson(jsonText, NetworkMessage.class).login;
        this.password = gson.fromJson(jsonText, NetworkMessage.class).password;
        this.text = gson.fromJson(jsonText, NetworkMessage.class).text;
        this.error = gson.fromJson(jsonText, NetworkMessage.class).error;
    }
}

