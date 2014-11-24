package com.chat.messenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.chat.client.ClientSvc;
import com.chat.client.impl.Client;
import com.chat.common.data.Message;
import com.chat.common.utils.Helper;
import com.chat.common.utils.Type;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ActMessenger extends Activity{
    private static final String TAG="ActMessenger";
    private EditText etMessage;
    private EditText metMessages;
    private Button btSend;
    private ClientSvc clientSvc;
    private int port = 2000;
    private String host;
    private String username = "Anonymous";

    boolean isConnected = false;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        etMessage = (EditText) findViewById(R.id.etMessage);
        metMessages = (EditText) findViewById(R.id.etMessages);

        btSend = (Button) findViewById(R.id.btSend);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected) {
                    clientSvc.sendMessage(new Message(etMessage.getText().toString(), Type.MSG));
                } else {
                    join();
                }
                etMessage.setText("");
            }
        });

        etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMessage.setText("");
            }
        });

        if(Helper.isNetworkAvailable(this)){
            host = getLocalIpAddress();
            join();
            etMessage.requestFocus();
        } else {
            Helper.makeToast("Internet must be enabled!", Toast.LENGTH_LONG, this);
            exit();
        }
    }

    private void exit(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public void appendText(Message msg) {
        metMessages.append(msg.toString());
    }

    public void connectionFailed() {
        etMessage.setText("Enter the username here");
        isConnected = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!isConnected){
            menu.findItem(R.id.iMates).setEnabled(false);
            menu.findItem(R.id.iLeave).setEnabled(false);
            menu.findItem(R.id.iJoin).setEnabled(true);
        } else {
            menu.findItem(R.id.iMates).setEnabled(true);
            menu.findItem(R.id.iLeave).setEnabled(true);
            menu.findItem(R.id.iJoin).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.iInfo:
                displayServerDetails();
                return true;
            case R.id.iMates:
                clientSvc.sendMessage(new Message("", com.chat.common.utils.Type.ALL_USERS));
                ActMessenger.this.setTitle("Mates");
                return true;
            case R.id.iLeave:
                clientSvc.sendMessage(new Message("", com.chat.common.utils.Type.SIGNOUT));
                ActMessenger.this.setTitle("Signed-out");
                return true;
            case R.id.iJoin:
                join();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayServerDetails(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View serverInfoView = inflater.inflate(R.layout.server_info, null);
        ((TextView)serverInfoView.findViewById(R.id.tvHostVal)).setText(host);
        ((TextView)serverInfoView.findViewById(R.id.tvPortVal)).setText(String.valueOf(port));

        builder.setView(serverInfoView)
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void join(){
        if(Helper.isNetworkAvailable(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View serverJoinView = inflater.inflate(R.layout.server_join, null);
            final EditText etHostVal = (EditText)serverJoinView.findViewById(R.id.etHost);
            final EditText etPortVal = (EditText)serverJoinView.findViewById(R.id.etPort);
            etHostVal.setText(host);
            etPortVal.setText(String.valueOf(port));
            final EditText etUsername = (EditText) serverJoinView.findViewById(R.id.etAlias);
            etUsername.setText(username);
            builder.setCancelable(false);
            builder.setView(serverJoinView)
                    .setPositiveButton(R.string.join, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if((etUsername.getText() == null || etUsername.getText().length() == 0)
                                    || (etHostVal.getText() == null || etHostVal.getText().length() == 0)
                                    || etPortVal.getText() == null || etPortVal.getText().length() == 0) {
                                return;
                            }

                            host = etHostVal.getText().toString();
                            port = Integer.parseInt(etPortVal.getText().toString());
                            clientSvc = new Client(host, port, username.toString(), ActMessenger.this);
                            clientSvc.start();
                            etMessage.setText("Enter your message below");
                            isConnected = true;
                            Helper.makeToast(username + " joined!", Toast.LENGTH_SHORT, ActMessenger.this);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            final AlertDialog alertDialog = builder.create();
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (ActMessenger.this.host == null || ActMessenger.this.host.isEmpty()) {
                        Helper.makeToast("Host is required!", Toast.LENGTH_LONG, ActMessenger.this);;
                        alertDialog.show();
                    }
                    else if (ActMessenger.this.port <= 0) {
                        Helper.makeToast("Port is required!", Toast.LENGTH_LONG, ActMessenger.this);;
                        alertDialog.show();
                    }
                    else if (ActMessenger.this.username == null || ActMessenger.this.username.isEmpty()) {
                        Helper.makeToast("Username is required!", Toast.LENGTH_LONG,
                                ActMessenger.this);;
                        alertDialog.show();
                    }
                }
            });
            alertDialog.show();
        } else Helper.makeToast("Internet is not available.", Toast.LENGTH_LONG, this);

    }
}
