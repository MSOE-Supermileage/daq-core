/**
 * Created by Blake on 12/17/2014.
 */
public class Main {
    static MyHTTPServer server;
    public static void main(String[] args) {
        server=new MyHTTPServer();
        server.startServer(8000);
    }
}
