package com.chat.client.impl;

import android.os.AsyncTask;
import android.util.Log;
import com.chat.client.ClientSvc;
import com.chat.common.data.Message;
import com.chat.common.utils.Type;
import com.chat.messenger.ActMessenger;
import com.chat.server.ServerSvc;
import com.chat.server.impl.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client implements ClientSvc {
    private static final String TAG = "Client";
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    private ActMessenger actMessenger;
    private String server, username;
    private int port;
    private Socket socket;

    public Client(String server, int port, String username, ActMessenger actMessenger) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.actMessenger = actMessenger;
    }

    @Override
    public String start() {
        ConnectorAsync myClientTask = new ConnectorAsync(
                server,
                port);
        myClientTask.execute();
        server = myClientTask.dstAddress;
        return myClientTask.dstAddress;
    }

    public void display(final Message msg) {
        actMessenger.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                actMessenger.appendText(msg);
            }
        });
    }

    @Override
    public void sendMessage(Message msg) {
        try {
            msg.setUsername(username);
            objectOutputStream.writeObject(msg);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            display(new Message(e, "Exception writing to server"));
        }
    }

    private void disconnect() {
        try {
            if(objectInputStream != null) objectInputStream.close();
            if(objectOutputStream != null) objectOutputStream.close();
            if(socket!=null) socket.close();
            // inform the GUI
            if(actMessenger != null)
                actMessenger.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actMessenger.connectionFailed();
                    }
                });
        }
        catch(Exception e) {Log.e(TAG, e.getMessage(), e);} // not much else I can do
    }

    public class ConnectorAsync extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;

        public ConnectorAsync(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            while(socket==null){
                try {
                    display(new Message("Connecting to the server", Type.INFO));
                    socket = new Socket(dstAddress, dstPort);
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(true) {
                                try {
                                    Message msg = (Message) objectInputStream.readObject();
                                    display(msg);
                                } catch(IOException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                    display(new Message(e, "Server has closed the connection"));
                                    disconnect();
                                    break;
                                } catch(ClassNotFoundException e2) {
                                    Log.e(TAG, e2.getMessage(), e2);
                                }
                            }
                        }
                    }).start();
                    try {
                        objectOutputStream.writeObject(username);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                    display(new Message("Chat server not available at " + server + ":" + port,
                            Type.ERROR));
                    display(new Message("Starting the chat server on this device", Type.INFO));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ServerSvc serviceSvc = new Server(port);
                            serviceSvc.start();
                        }
                    }).start();
                }
            }
            return null;
        }
    }

}

