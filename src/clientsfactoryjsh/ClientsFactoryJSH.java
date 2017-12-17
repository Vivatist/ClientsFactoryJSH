package clientsfactoryjsh;

import java.net.*;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.*;

class JabberClientThread extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static int counter = 0;
    private int id = counter++;
    private static int threadcount = 0;

    public static int threadCount() {
        return threadcount;
    }

   // Gson gson = new Gson();

    JabberClientThread(InetAddress addr, int port) {
        System.out.println("Making client " + id);
        threadcount++;
        try {
            socket = new Socket(addr, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
            // Если создание сокета провалилось,
            // ничего ненужно чистить.
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Включаем автоматическое выталкивание:
            out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            start();
        } catch (IOException e) {
            // Сокет должен быть закрыт при любой
            // ошибке, кроме ошибки конструктора сокета:
            try {
                socket.close();
            } catch (IOException e2) {
                System.err.println("Socket not closed");
            }
        }
        // В противном случае сокет будет закрыт
        // в методе run() нити.
    }

    @Override
    public void run() {

        try {
            for (int i = 0; i < ClientsFactoryJSH.NUM_REQUESTS; i++) {
                out.println("Client " + id + ": " + i);
                String str = in.readLine();
                System.out.println(str);
                try {
                    Thread.sleep((int) (Math.random() * ClientsFactoryJSH.DELAY_REQUESTS));
                } catch (InterruptedException ex) {
                    Logger.getLogger(JabberClientThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            out.println("END");
        } catch (IOException e) {
            System.err.println("IO Exception" + e);
        } finally {
            // Всегда закрывает:
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
            threadcount--; // Завершаем эту нить
        }
    }
}

public class ClientsFactoryJSH {

    private static int PORT;
    private static String HOST;
    private static int MAX_CLIENTS;
    static int NUM_REQUESTS;
    static int DELAY_REQUESTS;
    private static int DELAY_CLIENTS;

    private static final Properties props = new Properties();

    // Загрузка сохраненных настроек
    private static void LoadSettings(String _settingsFilename) {

        try {
            //пытаемся прочитать настройки из файла
            FileInputStream input = new FileInputStream(_settingsFilename);
            props.load(input);
            input.close();
            PORT = Integer.parseInt(props.getProperty("PORT"));
            HOST = props.getProperty("HOST");
            MAX_CLIENTS = Integer.parseInt(props.getProperty("MAX_CLIENTS"));
            NUM_REQUESTS = Integer.parseInt(props.getProperty("NUM_REQUESTS"));
            DELAY_REQUESTS = Integer.parseInt(props.getProperty("DELAY_REQUESTS"));
            DELAY_CLIENTS = Integer.parseInt(props.getProperty("DELAY_CLIENTS"));
            System.out.println("Upload settings complete");
            System.out.println(HOST);
        } catch (Exception ignore) {
            //если файл не существует - создаем и записываем значения по умолчанию
            props.setProperty("PORT", "7777");
            props.setProperty("HOST", "89.169.58.253");
            props.setProperty("MAX_CLIENTS", "5");
            props.setProperty("NUM_REQUESTS", "10");
            props.setProperty("DELAY_REQUESTS", "10000");
            props.setProperty("DELAY_CLIENTS", "0");

            FileOutputStream output = null;
            try {
                output = new FileOutputStream(_settingsFilename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                props.store(output, "Saved settings");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Making new settings file");
            LoadSettings(_settingsFilename);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        final String settingsFilename = "ClientsFactoryJSH.ini";
        System.out.println("Settings file: " + settingsFilename);
        LoadSettings(settingsFilename);

        InetAddress addr = InetAddress.getByName(HOST);

        //главный цикл
        while (true) {
            if (JabberClientThread.threadCount() < MAX_CLIENTS) {
                new JabberClientThread(addr, PORT);
            }
            Thread.currentThread();
            Thread.sleep(DELAY_CLIENTS);
        }
    }
}