import org.omg.CORBA.NameValuePair;
import sun.net.www.http.HttpClient;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blake on 12/17/2014.
 */
public class PhoneConnectionAgent {
    private MyHTTPServer server;
    public boolean isRunning = false;
    private ArrayList<String> datacollection = null;
    private Thread datathread = null;
    //private Socket mysock = null;

    public PhoneConnectionAgent(MyHTTPServer server) {
        this.server = server;
    }

    public void start(String address, int port) {
        try {
            server.log("Connecting to phone at " + address + ":" + port);
            //mysock = new Socket(address, port);

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
            //mysock.close();
            datacollection = null;
            //mysock = null;
            datathread = null;
        } catch (Exception e) {
            server.log(e.toString());
        }

    }

    class DataCollector implements Runnable {
        BufferedReader reader;

        public void run() {
            while (isRunning) {
                try {
                    URL url = new URL("http://msoesmv.hostei.com/webRelay.php");
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");

                    String urlParams="operation=pull";
                    connection.setDoOutput(true);
                    DataOutputStream d=new DataOutputStream(connection.getOutputStream());
                    d.writeBytes(urlParams);
                    d.flush();
                    d.close();

                    //reader= new BufferedReader(new InputStreamReader(mysock.getInputStream()));
                    reader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String data;
                    while(!(data= reader.readLine()).equals("****")) {
                        server.log("received-data: " + data);
                        datacollection.add(data);
                        if (data.contains("Exception") || data.contains("null"))
                            isRunning = false;
                    }
                    reader.close();
                } catch (IOException e) {
                    isRunning = false;
                    server.log(e.toString());
                }
            }
            server.log("Phone disconnected");
        }
    }
}
