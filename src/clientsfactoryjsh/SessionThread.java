package clientsfactoryjsh;

import Errors.MyExceptionOfNetworkMessage;
import org.jetbrains.annotations.Contract;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class SessionThread extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static int counter = 0;
    private int id = counter++;
    private static int threadCount = 0;
    private boolean echo;
    private static long sendingBites;
    private static long errorsCount;
    private static long packetCount;


    @Contract(pure = true)
    public static int ThreadCount() {
        return threadCount;
    }


    private List<String> CommandList;


    private String getRandomCommand(List<String> CommandList) {
        if (CommandList.size() > 0) {
            int i = Math.round((float) (Math.random() * (CommandList.size() - 1)));
            return CommandList.get(i);
        } else return null;
    }


    SessionThread(InetAddress addr, int port, List<String> CommandList, boolean echo) {
        this.CommandList = CommandList;
        this.echo = echo;
        System.out.println("Making client " + id);
        threadCount++;


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
            for (int i = 0; i < Main.NUM_REQUESTS; i++) {
                String str = getRandomCommand(CommandList);
                if (echo) str = "echo -" + str;
                NetworkMessage nm = new NetworkMessage("TestUser", "123", str, false);
                String sOut = nm.getText();
                sendingBites += nm.toJSON().length();
                out.println(nm.toJSON());

                String sIn = "";
                do {
                    sIn += (char) in.read();
                } while (in.ready());
                sIn = sIn.substring(0, sIn.length() - 1);

                nm.fromJSON(sIn);
                sendingBites += nm.toJSON().length();

                if (echo) { //Если тест, то сравниваеем ответ и запрос, при несовпадении - ошибка
                    if (!sOut.equals(nm.getText())) {
                        errorsCount++;
                    }
                }

                packetCount++; // увеличиваем счетчик пакетов

                if (!nm.getError())
                    System.out.println("ERR:" + errorsCount + " PCK:" + packetCount + " SND[kB]: " + (int) Math.floor(sendingBites / 1024) + " Client " + id + ": Request:" + i + " " + sOut + ", Response: " + nm.getText());
                else
                    System.err.println("ERR:" + errorsCount + " PCK:" + packetCount + " SND[kB]: " + (int) Math.floor(sendingBites / 1024) + " Client " + id + ": Request:" + i + " " + sOut + ", Response: " + nm.getText());


                try {
                    Thread.sleep((int) (Math.random() * Main.DELAY_REQUESTS));
                } catch (InterruptedException ex) {
                    Logger.getLogger(SessionThread.class.getName()).log(Level.SEVERE, null, ex);
                    errorsCount++;
                }
            }
            out.println("END");
        } catch (IOException e) {
            System.err.println("IO Exception" + e);
            errorsCount++;
        } catch (MyExceptionOfNetworkMessage myExceptionOfNetworkMessage) {
            myExceptionOfNetworkMessage.printStackTrace();
            errorsCount++;
        } finally {
            // Всегда закрывает:
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
            threadCount--; // Завершаем эту нить
        }
    }
}
