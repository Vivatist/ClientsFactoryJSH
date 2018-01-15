package clientsfactoryjsh;

import Errors.MyExceptionOfCommandPackage;
import Errors.MyExceptionOfNetworkMessage;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


public class Main {

    private static int PORT;
    private static String HOST;
    private static int MAX_CLIENTS;
    static int NUM_REQUESTS;
    static int DELAY_REQUESTS;
    private static int DELAY_CLIENTS;

    private static Socket socket;

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

    public static void main(String[] args) throws IOException, InterruptedException, MyExceptionOfNetworkMessage {

        final String settingsFilename = "Main.ini";
        System.out.println("Settings file: " + settingsFilename);
        LoadSettings(settingsFilename);


        InetAddress addr = InetAddress.getByName(HOST);
        List<String> CommandList = new ArrayList<>();
        final String commandsFilename = "commands.ini";
        try {

            FileReader fr = new FileReader(commandsFilename);
            BufferedReader reader = new BufferedReader(fr);
            String line;

            while ((line = reader.readLine()) != null) {
                //удаляем комментарии
                if (line.indexOf("#") >= 0) {
                    String str = line.substring(line.indexOf("#") + 0);
                    line = line.replace(str, "");
                }
                line = line.trim();
                if (line.length() != 0) {
                    CommandList.add(line);
                }

            }

            reader.close();
            fr.close();
        } catch (IOException e) {
            PrintWriter writer = new PrintWriter("commands.ini", "UTF-8");

            writer.close();
        }

        Scanner in = new Scanner(System.in);
        System.out.println("\nВыберите режим работы: ");
        System.out.println("[1] автоматический");
        System.out.println("[2] ручной");
        System.out.println("[*] выход");

        int input = in.nextInt();
        switch (input) {
            case 1:
                while (true) {
                    if (SessionThread.threadCount() < MAX_CLIENTS) {
                        new SessionThread(addr, PORT, CommandList);
                    }
                    Thread.currentThread();
                    Thread.sleep(DELAY_CLIENTS);
                }

            case 2:
                while (true) {
                    System.out.println("\nВведите команду: ");
                    Scanner sc = new Scanner(System.in);
                    String line = sc.nextLine();
                    NetworkMessage nm = new NetworkMessage("TestUser", "123", line, false);

                    String sOut = nm.getText();

                    try {
                         socket = new Socket(addr, PORT);
                        BufferedReader  iin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter  out = new PrintWriter(
                                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                        out.println(nm.toJSON());

                        String sIn = "";
                        do {
                            sIn += (char) iin.read();
                        } while (iin.ready());
                        sIn = sIn.substring(0, sIn.length() - 1);

                        nm.fromJSON(sIn);

                        if (!nm.getError())
                            System.out.println("\u001B[33m" + "Request: " + sOut + ", Response: " + nm.getText());
                        else
                            System.err.println("Request: " + sOut + ", Response: " + nm.getText());



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
            default:
        }


        //главный цикл

    }
}