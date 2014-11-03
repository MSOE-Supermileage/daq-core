/**
 * Created by stacksb on 11/1/2014.
 */
import com.sun.net.httpserver.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class MyHTTPServer {
    static HttpServer server=null;
    public static void main(String[] args){
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        server.createContext("/", new RequestHandler());
        server.setExecutor(null);
        server.start();
    }

    static class RequestHandler implements HttpHandler{
        public void handle(HttpExchange e) throws IOException{
            String path=System.getProperty("user.dir")+e.getRequestURI().toString().replace("/","\\");
            System.out.println(path);
            Scanner scanner = new Scanner(new File(path));
            OutputStream out=e.getResponseBody();
            String response="";
            while(scanner.hasNextLine()){
                response+="\n"+scanner.nextLine();
            }
            System.out.println("done reading");
            e.sendResponseHeaders(200,response.length());
            out.write(response.getBytes());
            out.flush();
            out.close();
        }
    }
}
