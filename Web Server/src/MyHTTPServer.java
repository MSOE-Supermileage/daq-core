/**
 * Created by stacksb on 11/1/2014.
 */
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.SocketHandler;

public class MyHTTPServer {
    static HttpServer server=null;
    public static void main(String[] args){
        startServer(8000);
    }
    public static void startServer(int port){
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        server.createContext("/", new RequestHandler());
        server.createContext("/ajax", new AjaxRequestHandler());
        server.setExecutor(null);
        server.start();
    }
    static class RequestHandler implements HttpHandler{
        public void handle(HttpExchange e) throws IOException{
            String path=System.getProperty("user.dir")+e.getRequestURI().toString().replace("/","\\");
            if (!(new File(path).exists()||new File(path).isDirectory())){
                path = System.getProperty("user.dir")+"/oops404.html";
            }
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
    public static String getData(String address, int port){
        try {
            Socket sock = new Socket(address, port);
            BufferedReader reader=new BufferedReader(new InputStreamReader( sock.getInputStream()));
            return reader.readLine();
        }catch(Exception e){

        }
        return null;
    }
    public static String parseData(String data){

        return data;
    }
    static class AjaxRequestHandler implements HttpHandler {
        public void handle(HttpExchange e) throws IOException{
            InputStream in=e.getRequestBody();
            String request="",line=null;
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));

            while((line=reader.readLine())!=null){
                request+= line;
            }
            System.out.println(request);

            OutputStream out=e.getResponseBody();
            String response="testing 123 testing 123...";
            System.out.println("done reading");
            e.sendResponseHeaders(200,response.length());
            out.write(response.getBytes());
            out.flush();
            out.close();
        }
    }
}
