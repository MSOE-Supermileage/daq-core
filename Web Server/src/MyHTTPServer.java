/**
 * Created by stacksb on 11/1/2014.
 */

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.stage.Screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.FileNameMap;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MyHTTPServer {
    //our base server
    static HttpServer server = null;

    PhoneConnectionAgent phoneAgent;
    //log
    static String log = "";

    public static String version = "1.2";

    //for proper transferring of embedded files/styles/scripts
    static FileNameMap fileNameMap = URLConnection.getFileNameMap();

    public MyHTTPServer() {
        phoneAgent = new PhoneConnectionAgent(this);
    }

    /*//main thread
    public void main(String[] args) {
        //start the server
        startServer(8000);
    }*/

    /**
     * Starts the server
     *
     * @param port Server Port
     */
    public void startServer(int port) {
        if(server!=null){
            server.stop(0);
        }
        //create the server
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (Exception e) {
            log(e.toString());
            return;
        }
        //establish what handlers do what:
        //  -- "/" is the generic case for web page delivery
        //  -- "/ajax" is the case for AJAX data requests
        server.createContext("/", new RequestHandler());
        server.createContext("/ajax", new AjaxRequestHandler());
        new MyTray().startNewTray();
        //I have no idea what this does
        server.setExecutor(null);

        //start the server
        server.start();
        log("home dir: " + System.getProperty("user.dir"));
    }

    /**
     * Logs the console output and prints it
     *
     * @param s string to be logged
     */
    public static void log(String s) {
        System.out.println(s);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        log += (dateFormat.format(date)) + ": " + s + "\n"; //2014/08/06 15:59:48
    }

    //Handler for http page requests
    static class RequestHandler implements HttpHandler {
        public void handle(HttpExchange e) throws IOException {
            log("testing");
            //get file from request path (after the slashes)
            String path = System.getProperty("user.dir") + e.getRequestURI().toString().replace("/", "\\");

            //if it doesn't exist, serve the 404 page
            if (!(new File(path).exists() )) {
                path = System.getProperty("user.dir") + "\\oops404.html";
            }
            if(new File(path).isDirectory()){
                path+="\\index.html";
            }

            // Get mime type from the ones defined in [jre_home]/lib/content-types.properties
            String mimeType = fileNameMap.getContentTypeFor((new File(path)).toURI().toURL().toString());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            log(mimeType);

            //for clarity
            log(path);

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

    /*static boolean dataThreadIsRunning = false;
    static ArrayList<String> datacollection = null;
    static Thread datathread = null;
    static Socket mysock = null;*/

    public void stopDataCollection() {
        /*try {
            dataThreadIsRunning = false;
            mysock.close();
            datacollection = null;
            mysock = null;
            datathread = null;
        } catch (Exception e) {
            log(e.toString());
        }*/
        phoneAgent.stop();
    }

    public void startDataCollection(String address, int port) {
        /*try {
            log("Connecting to phone at " + address + ":" + port);
            mysock = new Socket(address, port);
            datacollection = new ArrayList<String>();
            dataThreadIsRunning = true;
            datathread = new Thread(new Runnable() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(mysock.getInputStream()));

                public void run() {
                    while (dataThreadIsRunning) {
                        try {
                            String data = reader.readLine();
                            log("received-data: " + data);
                            datacollection.add(data);
                            if (data.contains("Exception") || data.contains("null"))
                                dataThreadIsRunning = false;
                        } catch (Exception e) {
                            dataThreadIsRunning = false;
                            log(e.toString());
                        }
                    }
                    log("Phone disconnected");
                }
            });
            datathread.start();
        } catch (Exception e) {
            log(e.toString());
        }*/
        phoneAgent.start(address, port);
    }

    //gets the data from the phone and returns it
    public String getData() {
        try {
            if (phoneAgent.getData() != null) {

                //return the data
                return phoneAgent.getData().get(phoneAgent.getData().size() - 1);
            }
        } catch (Exception e) {
            log(e.toString());
        }
        return null;
    }

    //gets all of the data from the phone and returns it
    public String getAllData() {
        try {
            if (phoneAgent.getData() != null) {

                //return the data
                return String.join("<br>", Arrays.copyOf(phoneAgent.getData().toArray(), phoneAgent.getData().toArray().length, String[].class));
            }
        } catch (Exception e) {
            log(e.toString());
        }
        return null;
    }

    //parses the data (for now echoes)
    public static String parseData(String data) {

        return data;
    }

    //deals with AJAX server requests (how the web page is sent the data)
    class AjaxRequestHandler implements HttpHandler {
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
            log(request);
            log("done reading");

            //establish the response stream
            OutputStream out = e.getResponseBody();
            String response = "";
            if (request.contains("requesttype=last"))
                response = getData();//"testing 123 testing 123...";
            else if (request.contains("requesttype=all"))
                response = getAllData();
            log(response);
            //let the browser know there's something coming down the pipe
            e.sendResponseHeaders(200, response.length());

            //send the data
            out.write(response.getBytes());
            out.flush();

            //IMPORTANT: close the socket
            out.close();
        }
    }

    /**
     * Shows a JOptionPane Message Box
     *
     * @param title   Title of Message Box
     * @param message Message of Message Box
     */
    public static void showLogBox(String title, final String message) {
        JFrame frame = new JFrame();
        final JTextPane text = new JTextPane();
        text.setText(message);
        frame.setTitle(title);
        frame.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
        JScrollPane scroll = new JScrollPane(text);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setMinimumSize(new Dimension(400, 100));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                text.setText(log);
            }
        });

        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(refresh, BorderLayout.PAGE_START);
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        //JOptionPane.showMessageDialog(null,message,title,JOptionPane.INFORMATION_MESSAGE);
    }

    public void restart() throws Exception {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar"))
            return;

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);

    }

    class MyTray {
        TrayIcon trayIcon = null;

        public void startNewTray() {
            //Check the SystemTray is supported
            if (!SystemTray.isSupported()) {
                log("SystemTray is not supported");
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
                log(e.toString());
                return;
            }
            //create the system tray controller
            final SystemTray tray = SystemTray.getSystemTray();

            //stop the old tray Icon
            if (trayIcon != null) {
                tray.remove(trayIcon);
                trayIcon = null;
            }

            //create the tray icon application
            trayIcon = new TrayIcon(bimg);
            trayIcon.setToolTip("PitView v" + version);
            trayIcon.setImageAutoSize(true);


            // Create pop-up menu components
            MenuItem nameItem = new MenuItem("PitView v" + version);
            MenuItem startItem = new MenuItem("Start");
            startItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log("Starting Server...");
                    startServer(8000);
                }
            });
            MenuItem stopItem = new MenuItem("Stop");
            stopItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log("Stopping Server...");
                    server.stop(0);
                    server = null;
                }
            });
            final MenuItem logsItem = new MenuItem("Show Logs");
            logsItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showLogBox("Console Logs", log);
                }
            });
            MenuItem startDataItem = new MenuItem("Connect to phone");
            startDataItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log("Starting Data Server...");
                    startDataCollection(JOptionPane.showInputDialog("Please enter IP: "), 1112);
                }
            });
            MenuItem stopDataItem = new MenuItem("Disconnect from phone");
            stopDataItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log("Stopping Data Server...");
                    stopDataCollection();
                }
            });
            MenuItem restartItem = new MenuItem("Restart");
            restartItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log("Restarting Server...");
                    server.stop(0);
                    tray.remove(trayIcon);
                    try {
                        restart();
                    }catch(Exception ex){
                        log("Restart failed with stack: "+ex.getStackTrace());
                    }
                }
            });
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log("Exiting Server...");
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
            popup.add(logsItem);
            popup.addSeparator();
            popup.add(startDataItem);
            popup.add(stopDataItem);
            popup.addSeparator();
            popup.add(restartItem);
            popup.add(exitItem);

            trayIcon.setPopupMenu(popup);

            try {
                //add icon to tray
                tray.add(trayIcon);
            } catch (AWTException e) {
                log("TrayIcon could not be added.");
            }
        }
    }
}
