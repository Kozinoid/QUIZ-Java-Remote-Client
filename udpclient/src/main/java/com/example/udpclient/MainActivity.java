package com.example.udpclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "myLogs";
    public static final int port = 9010;
    public static final String address = "235.5.5.254";

    Thread sendingProcess;
    Thread recievingProcess;

    String message;
    boolean stop = false;

    Button bt_Send;
    TextView tv_Message;
    EditText et_Input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitUI();
        ReceiveMessage();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_main);
        InitUI();
        ReceiveMessage();
    }

    @Override
    protected void onStop() {

        stop = true;

        super.onStop();
    }

    private void InitUI()  {
        bt_Send = findViewById(R.id.bt_Send);
        bt_Send.setOnClickListener(bt_Listener);
        tv_Message = findViewById(R.id.tv_Message);
        et_Input = findViewById(R.id.et_Input);
    }

    View.OnClickListener bt_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.bt_Send:
                    SendMessage();
                    break;
            }
        }
    };

    private void SendMessage()
    {
        message = et_Input.getText().toString();

        sendingProcess = new Thread(SandingRunnable);
        sendingProcess.start();
    }

    private void ReceiveMessage()
    {
        stop = false;

        recievingProcess = new Thread(ReceivingRunnable);
        recievingProcess.start();
    }

    //=============================================================================================
    Runnable SandingRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            SocketAddress remoteAddress = new InetSocketAddress(address, port);
            DatagramSocket sendingSocket = null;
            try {
                sendingSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                DatagramPacket request = new DatagramPacket(message.getBytes(), message.getBytes().length, remoteAddress);
                sendingSocket.send(request);
            } catch (IOException e1) {
                Log.d(TAG, "error sending packets", e1);
            }
        }
    };

    Runnable ReceivingRunnable = new Runnable() {
        @Override
        public void run()
        {
            MulticastSocket receivingSocket = null;
            try {
                receivingSocket = new MulticastSocket(port);
                receivingSocket.joinGroup(InetAddress.getByName(address));
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] data = new byte[4096];
            DatagramPacket receivingPacket = new DatagramPacket(data,data.length);

            while (!stop)
            {
                try {
                    receivingSocket.receive(receivingPacket);
                    String reply = new String(receivingPacket.getData(), 0, receivingPacket.getLength());
                    String str = receivingPacket.getAddress().toString() + " : " + reply;
                    tv_Message.setText(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
