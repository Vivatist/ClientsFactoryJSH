package clientsfactoryjsh;

import Errors.MyExceptionOfCommandPackage;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Main {

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

        final String settingsFilename = "Main.ini";
        System.out.println("Settings file: " + settingsFilename);
        LoadSettings(settingsFilename);


        InetAddress addr = InetAddress.getByName(HOST);
        List<CommandPackage> CommandList = new ArrayList<>();
        final String commandsFilename = "commands.ini";
        try {

            FileReader fr = new FileReader(commandsFilename);
            BufferedReader reader = new BufferedReader(fr);
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    CommandPackage cp = new CommandPackage(line);
                    CommandList.add(cp);
                } catch (MyExceptionOfCommandPackage myExceptionOfCommandPackage) {
                    myExceptionOfCommandPackage.printStackTrace();
                }
            }
            reader.close();
            fr.close();
        } catch (IOException e) {
            PrintWriter writer = new PrintWriter("commands.ini", "UTF-8");

            writer.close();
        }


        //главный цикл
        while (true) {
            if (SessionThread.threadCount() < MAX_CLIENTS) {
                new SessionThread(addr, PORT, CommandList);
            }
            Thread.currentThread();
            Thread.sleep(DELAY_CLIENTS);
        }
    }
}