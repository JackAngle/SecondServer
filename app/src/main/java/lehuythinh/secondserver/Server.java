package lehuythinh.secondserver;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import java.util.Enumeration;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import org.java_websocket.server.WebSocketServer;

public class Server{
    MainActivity activity;
    private static int socketServerPort = 8000;

    public Server (MainActivity activity){
        this.activity = activity;
        int count = 0;
        /*
        try {
            Thread serverThread = new Thread(new ThreadWebSocketServer(socketServerPort));

            serverThread.start();
        }catch(UnknownHostException e) {
            e.printStackTrace();
        }
        */
        try {
        ThreadWebSocketServer threadWebSocketServer = new ThreadWebSocketServer(socketServerPort);
        //threadWebSocketServer.setConnectionLostTimeout(5);
        threadWebSocketServer.start();
        }catch(UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /*Server actions*/
    private class ThreadWebSocketServer extends WebSocketServer{
        int cnt = 0;
        public ThreadWebSocketServer( int port ) throws UnknownHostException {
            super( new InetSocketAddress(port), null);

        }


        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            final String clientAddress = getClientAddress(conn);
            activity.runOnUiThread(new Runnable (){
                @Override
                public void run() {
                    activity.msg.setText("[Server]New connection:" + clientAddress);
                }
            });
            conn.send("1" + conn.getLocalSocketAddress() + conn.getRemoteSocketAddress() + "|"
                        + "Welcome to server!"); //This method sends a message to the new client
            //broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
            System.out.println("new connection to " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            activity.runOnUiThread(new Runnable (){
                @Override
                public void run() {
                    activity.msg.setText("");
                }
            });
            System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
           Log.d("onMessage","[String]Received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
        }

        @Override
        public void onMessage( WebSocket conn, ByteBuffer message ) {
            System.out.println("[Byte]Received ByteBuffer from "	+ conn.getRemoteSocketAddress() + ": " + message);
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.err.println("an error occured on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
        }

        @Override
        public void onStart() {
            System.out.println("server started successfully");
        }
    }

    /*Return WebSocketPort*/
    public int getPort() {
        return socketServerPort;
    }

    public String getClientAddress(WebSocket conn){
        final String address = conn.getRemoteSocketAddress().toString();
        return address;
    }

    /*Get IP address of server*/
    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}