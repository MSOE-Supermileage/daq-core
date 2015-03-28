import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Created by Blake on 12/17/2014.
 */
public class Main {
    static MyHTTPServer server;
    public static void main(String[] args) {
        server=new MyHTTPServer();
        server.startServer(80);
    }
}
