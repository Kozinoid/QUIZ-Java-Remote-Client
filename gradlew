package com.example.datagramsocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "myLogs";

    TextView tb_TextBox;
    InetAddress broadcastAddress;
    DatagramSocket socket;
    Boolean running = false;
    DatagramPacket packet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tb_TextBox = findViewById(R.id.tb_TextBox);

        try {
            broadcastAddress = InetAddress.getByName("172.168.0.255");
            socket = new DatagramSocket(8060);
            socket.connect(broadcastAddress, 8060);
            socket.setBroadcast(true);
            byte[] buf = new byte[4];
            packet = new DatagramPacket(buf, buf.length);

            running = true;
        }
        catch (Exception e){

        }

        recievingProcess.start();
    }


    Thread recievingProcess = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    do {
                        {
                            try {
                                socket.receive(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, packet.toString());
                        }
                    }
                    while (running);
                }
            }
    );

    @Override
    protected void onStop() {
        running = fa;
        super.onStop();
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              