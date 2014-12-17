import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Blake on 12/17/2014.
 */
public class PhoneConnectionAgent {
    private MyHTTPServer server;
    public boolean isRunning = false;
    private ArrayList<String> datacollection = null;
    private Thread datathread = null;
    private Socket mysock = null;

    public PhoneConnectionAgent(MyHTTPServer server) {
        this.server = server;
    }

    public void start(String address, int port) {
        try {
            server.log("Connecting to phone at " + address + ":" + port);
            mysock = new Socket(address, port);
            datacollection = new ArrayList<String>();
            isRunning = true;
            datathread = new Thread(new DataCollector());
            datathread.start();
        } catch (Exception e) {
            server.log("Error! ("+e.toString()+")");
        }
    }
    public ArrayList<String> getData(){
        return datacollection;
    }

    public void stop() {
        try {
            isRunning = false;
            mysock.close();
            datacollection = null;
            mysock = null;
            datathread = null;
        } catch (Exception e) {
            server.log(e.toString());
        }

    }

    class DataCollector implements Runnable {
        BufferedReader reader;

        public void run() {
            try {
                reader= new BufferedReader(new InputStreamReader(mysock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (isRunning) {
                try {
                    String data = reader.readLine();
                    server.log("received-data: " + data);
                    datacollection.add(data);
                    if (data.contains("Exception") || data.contains("null"))
                        isRunning = false;
                } catch (Exception e) {
                    isRunning = false;
                    server.log(e.toString());
                }
            }
            server.log("Phone disconnected");
        }
    }
}
