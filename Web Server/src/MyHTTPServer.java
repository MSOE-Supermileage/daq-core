/**
 * Created by stacksb on 11/1/2014.
 */

import com.sun.net.httpserver.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class MyHTTPServer {
    //our base server
    static HttpServer server = null;

    //for proper transferring of embedded files/styles/scripts
    static FileNameMap fileNameMap = URLConnection.getFileNameMap();

    //main thread
    public static void main(String[] args) {
        //start the server
        startServer(8000);
    }

    //function to start the server
    public static void startServer(int port) {
        //create the server
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (Exception e) {
            System.out.println(e.toString());
            return;
        }
        //establish what handlers do what:
        //  -- "/" is the generic case for web page delivery
        //  -- "/ajax" is the case for AJAX data requests
        server.createContext("/", new RequestHandler());
        server.createContext("/ajax", new AjaxRequestHandler());
        MyTray.startNewTray();
        //I have no idea what this does
        server.setExecutor(null);

        //start the server
        server.start();
        System.out.println("home dir: "+System.getProperty("user.dir"));
    }

    //Handler for http page requests
    static class RequestHandler implements HttpHandler {
        public void handle(HttpExchange e) throws IOException {

            //get file from request path (after the slashes)
            String path = System.getProperty("user.dir") + e.getRequestURI().toString().replace("/", "\\");

            //if it doesn't exist, serve the 404 page
            if (!(new File(path).exists() || new File(path).isDirectory())) {
                path = System.getProperty("user.dir") + "\\oops404.html";
            }

            // Get mime type from the ones defined in [jre_home]/lib/content-types.properties
            String mimeType = fileNameMap.getContentTypeFor((new File(path)).toURI().toURL().toString());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            System.out.println(mimeType);

            //for clarity
            System.out.println(path);

            //create output stream
            OutputStream out = e.getResponseBody();

            //read the requested file from the working directory
            byte[] bytes = Files.readAllBytes(Paths.get(path));

            //tell the browser what type of file it is
            e.getResponseHeaders().set("Content-Type", mimeType);

            //let the browser know there's something coming down the pipe
            e.sendResponseHeaders(200, bytes.length);

            //send the requested file
            out.write(bytes);
            out.flush();

            //IMPORTANT: close the writer
            out.close();
        }
    }
    static boolean dataThreadIsRunning=false;
    static ArrayList<String> datacollection=null;
    static Thread datathread=null;
    static Socket mysock=null;
    public static void stopDataCollection(){
        try {
            dataThreadIsRunning=false;
            mysock.close();
            datacollection=null;
            mysock=null;
            datathread=null;
        }catch(Exception e){
            System.out.println(e.toString());
        }

    }
    public static void startDataCollection(String address, int port){
        try {
            System.out.println("Connecting to phone at "+address+":"+port);
            mysock = new Socket(address, port);
            datacollection=new ArrayList<String>();
            dataThreadIsRunning=true;
            datathread=new Thread(new Runnable(){
                BufferedReader reader = new BufferedReader(new InputStreamReader(mysock.getInputStream()));

                public void run(){
                    while(dataThreadIsRunning) {
                        try {
                            String data=reader.readLine();
                            System.out.println("received-data: "+data);
                            datacollection.add(data);
                            if (data.contains("Exception")||data.contains("null"))
                                dataThreadIsRunning=false;
                        }catch(Exception e){
                            dataThreadIsRunning=false;
                            System.out.println(e.toString());
                        }
                    }
                    System.out.println("Phone disconnected");
                }
            });
            datathread.start();
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }

    //gets the data from the phone and returns it
    public static String getData() {
        try {
            if(datacollection!=null) {

                //return the data
                return datacollection.get(datacollection.size() - 1);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    //parses the data (for now echoes)
    public static String parseData(String data) {

        return data;
    }

    //deals with AJAX server requests (how the web page is sent the data)
    static class AjaxRequestHandler implements HttpHandler {
        public void handle(HttpExchange e) throws IOException {
            //establish the input data stream
            InputStream in = e.getRequestBody();
            String request = "", line = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            //read the data passed from the webpage data request
            while ((line = reader.readLine()) != null) {
                request += line;
            }
            //done reading

            //for clarity
            System.out.println(request);
            System.out.println("done reading");

            //establish the response stream
            OutputStream out = e.getResponseBody();
            //dummy response for now
            String response = getData();//"testing 123 testing 123...";
            System.out.println(response);
            //let the browser know there's something coming down the pipe
            e.sendResponseHeaders(200, response.length());

            //send the data
            out.write(response.getBytes());
            out.flush();

            //IMPORTANT: close the socket
            out.close();
        }
    }

    static class MyTray {
        static TrayIcon trayIcon = null;

        public static void startNewTray() {
            //Check the SystemTray is supported
            if (!SystemTray.isSupported()) {
                System.out.println("SystemTray is not supported");
                return;
            }

            //new right-click menu
            final PopupMenu popup = new PopupMenu();

            //load icon file
            Image bimg = null;
            try {
                URL url = new URL("http://files.softicons.com/download/web-icons/flat-style-icons-by-flaticonmaker/png/16x16/vip.png");
                bimg = ImageIO.read(url);

            } catch (Exception e) {
                System.out.println(e.toString());
                return;
            }

            //create the tray icon application
            trayIcon = new TrayIcon(bimg);
            trayIcon.setToolTip("PitView v1.1");
            trayIcon.setImageAutoSize(true);

            //create the system tray controller
            final SystemTray tray = SystemTray.getSystemTray();

            // Create pop-up menu components
            MenuItem nameItem = new MenuItem("PitView v1.0");
            MenuItem startItem = new MenuItem("Start");
            startItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Starting Server...");
                    startServer(8000);
                }
            });
            MenuItem stopItem = new MenuItem("Stop");
            stopItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Stopping Server...");
                    server.stop(0);
                    server = null;
                }
            });
            MenuItem startDataItem = new MenuItem("Connect to phone");
            startDataItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Starting Data Server...");
                    startDataCollection(JOptionPane.showInputDialog("Please enter IP: "),1112);
                }
            });
            MenuItem stopDataItem = new MenuItem("Disconnect from phone");
            stopDataItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Stopping Data Server...");
                    stopDataCollection();
                }
            });
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Exiting Server...");
                    server.stop(0);
                    tray.remove(trayIcon);
                    System.exit(0);
                }
            });
            //Add components to pop-up menu
            popup.add(nameItem);
            popup.addSeparator();
            popup.add(startItem);
            popup.add(stopItem);
            popup.addSeparator();
            popup.add(startDataItem);
            popup.add(stopDataItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon.setPopupMenu(popup);

            try {
                //add icon to tray
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
            }
        }
    }
}
