package clientsfactoryjsh;

import Errors.MyExceptionOfNetworkMessage;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;


/**
 * Класс описывающий сетевой пакет данных для обмена между сервером и клиентом. Пересылается
 * в формате JSON. Имеет встроенные методы для конвертации в JSON и обратно.
 *
 * @author Andrey Bochkarev
 * @version 1.0
 */
public class NetworkMessage {

    private String id;
    private String login;
    private String password;
    private String text;
    private Boolean error;


    public void setText(String text) throws MyExceptionOfNetworkMessage {
        if (text == null) {
            throw new MyExceptionOfNetworkMessage("ERROR: An attempt to pass an NULL string", 1);
        }
        if (text.trim().length() == 0) {
            throw new MyExceptionOfNetworkMessage("ERROR: An attempt to pass an empty string", 2);
        }
        this.text = text.trim();
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    private void setId(String id) throws MyExceptionOfNetworkMessage {
        if (id.trim().length() == 0) {
            throw new MyExceptionOfNetworkMessage("ERROR: ID can not be empty", 3);
        }
        this.id = id;
    }

    private void setLogin(String login) throws MyExceptionOfNetworkMessage {
        if ((login.trim().length() == 0)) {
            throw new MyExceptionOfNetworkMessage("ERROR: Login can not be empty", 4);
        }
        this.login = login.trim();
    }

    private void setPassword(String password) throws MyExceptionOfNetworkMessage {
        if ((password.trim().length() == 0)) {
            throw new MyExceptionOfNetworkMessage("ERROR: Password can not be empty", 5);
        }
        this.password = password;
    }

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

    /**
     * Конструктор, создает объект класса NetworkMessage, принимает значения полей в формате JSON
     *
     * @param jsonText Текст в формате JSON
     */
    public NetworkMessage(String jsonText) throws MyExceptionOfNetworkMessage {
        fromJSON(jsonText);
    }


    /**
     * Конструктор, создает объект класса NetworkMessage.
     *
     * @param login    Логин юзера
     * @param password Пароль юзера
     * @param text     Пересылаемый текст
     * @param error    Признак ошибки. True - ошибка
     */
    public NetworkMessage(String login, String password, String text, Boolean error) throws MyExceptionOfNetworkMessage {
        setId(UUID.randomUUID().toString());
        setLogin(login);
        setPassword(password);
        setText(text);
        setError(error);
    }



    /**
     * Сериализует объект в формат JSON
     *
     * @return возвращает текст в формате JSON
     */
    public String toJSON() {
        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        String jsonText = gson.toJson(this);
        return jsonText;
    }

    /**
     * Метод десериализации объекта из текста формата JSON
     *
     * @param jsonText Текст в формате JSON
     */
    public void fromJSON(String jsonText) throws MyExceptionOfNetworkMessage {
        Gson gson = new Gson();
        setId(gson.fromJson(jsonText, NetworkMessage.class).id);
        setLogin(gson.fromJson(jsonText, NetworkMessage.class).login);
        setPassword(gson.fromJson(jsonText, NetworkMessage.class).password);
        setText(gson.fromJson(jsonText, NetworkMessage.class).text);
        setError(gson.fromJson(jsonText, NetworkMessage.class).error);
    }


    /**
     * Извлекает список аргументов (разделенных знаком "-") из текста сетевого пакета
     *
     * @return Список, содержащий аргументы
     * @throws MyExceptionOfNetworkMessage
     */
    public List<String> getArguments() throws MyExceptionOfNetworkMessage {
        if (text == null) {
            throw new MyExceptionOfNetworkMessage("ERROR: An attempt to create arguments list from an NULL string", 8);
        }
        if (text.trim().length() == 0) {
            throw new MyExceptionOfNetworkMessage("ERROR: An attempt to create arguments list from an empty string", 9);
        }
        List<String> arguments = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(text, "-");
        while (tokenizer.hasMoreTokens()) {
            String result = tokenizer.nextToken();
            if (result.trim().length() != 0)
                arguments.add(result.trim());
        }
        arguments.remove(0);
        return arguments;
    }

    /**
     * Извлекает имя команды из текста сетевого пакета
     *
     * @return Имя команды
     * @throws MyExceptionOfNetworkMessage
     */
    public String getCommand() throws MyExceptionOfNetworkMessage {
        if (text == null) {
            throw new MyExceptionOfNetworkMessage("ERROR: An attempt to create a command list from an NULL string", 10);
        }
        if (text.trim().length() == 0) {
            throw new MyExceptionOfNetworkMessage("ERROR: An attempt to create a command from an empty string", 11);
        }
        List<String> arguments = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(text, "-");
        while (tokenizer.hasMoreTokens()) {
            String result = tokenizer.nextToken();
            if (result.trim().length() != 0)
                arguments.add(result.trim());
        }
        return arguments.get(0).toLowerCase();
    }
}

