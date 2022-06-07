package com.example.quiz_sheet_remote_control;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDP_Client_Device
{
    public static final int FOUND = 1;
    protected Handler hStringMessage;          // Handle callback for UI controls

    private int udpPort;
    private String udpIPAddresText;
    private String appName;         // Код приложения для UDP
    private Thread recievingProcess;
    private boolean stop = false;

    public UDP_Client_Device(String addr, int rPort, String aName, Handler.Callback hc)
    {
        udpIPAddresText = addr;
        udpPort = rPort;
        appName = aName;
        hStringMessage = new Handler(hc);
    }

    public void Connect()
    {
        stop = false;
        ReceiveMessage();
    }

    public void Disconnect()
    {
        stop = true;
    }

    private void ReceiveMessage()
    {
        recievingProcess = new Thread(ReceivingRunnable);
        recievingProcess.start();
    }

    Runnable ReceivingRunnable = new Runnable() {
        @Override
        public void run() {

            MulticastSocket udpReceivingSocket = null;
            DatagramPacket udpPacket;
            byte[] messageData;

            try {
                udpReceivingSocket = new MulticastSocket(udpPort);
                udpReceivingSocket.joinGroup(InetAddress.getByName(udpIPAddresText));
            } catch (IOException e) {
                e.printStackTrace();
            }
            messageData = new byte[4096];
            udpPacket = new DatagramPacket(messageData, messageData.length);

            while (!stop)
            {
                try {
                    udpReceivingSocket.receive(udpPacket);
                    String reply = new String(udpPacket.getData(), 0, udpPacket.getLength());
                    //-----------------------  Message in replay  -------------------------------
                    if (reply.equals(appName))
                    {
                        stop = true;
                        //*****************************
                        SendStringMessage(udpPacket.getAddress().toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //***********************************  UI  FUNCTIONS  *******************************************
    //-----------------------------------------------------------------------------------------------
    private void SendStringMessage(String txt) {
        Message msg;
        msg = hStringMessage.obtainMessage(FOUND, txt);
        hStringMessage.sendMessage(msg);
    }
}
