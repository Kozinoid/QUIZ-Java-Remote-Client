package com.example.android_tcp_client_device;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import eneter.messaging.diagnostic.EneterTrace;

public class LanNetworkScanner {
    //----------------------------- Constants ------------------------------------------------------
    public static final int SCAN_COMPLITED = 0;
    public static final int STRING_MESSAGE = 1;
    public static final int IP_MESSAGE = 2;
    //---------------------------------------------------------
    //|"tcp://192.168.0.41:8060" - Home "Kozinoid" Cable PC IP|
    //|"tcp://192.168.0.99:8060" - Home "Kozinoid" Wi-Fi PC IP|
    //---------------------------------------------------------
    public static final String FoundIPAddress = "tcp://192.168.0.99:8060";

    private Handler hScannerReport;
    private int port;

    //******************************  CONSTRUCTOR  *************************************************
    public LanNetworkScanner(int pt, Handler.Callback reportCallback) {
        hScannerReport = new Handler(reportCallback);
        port = pt;
    }

    //*****************************  SCAN FUNCTIONS  ***********************************************
    //------------------------------  Scan thread  -------------------------------------------------
    public void onScan() {
        // Open the connection in another thread.
        Thread anScannerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String str = ScanHostsInLocalNetwork(97, 100);
                } catch (Exception err) {
                    EneterTrace.error("Open connection failed.", err);
                }
            }
        });

        anScannerThread.start();
    }

    //----------------------------  Scan hosts in Local Network  ------------------------------------
    private String ScanHostsInLocalNetwork(int begin, int lessThen) // From begin to lessThen in mask
    {
        String answer = "";
        for (int i = begin; i < lessThen; i++) {
            String url = "192.168.0." + i;
            SendStringMessage(STRING_MESSAGE, url);
            String str = ping(url);
            if (str != "") {
                answer = url;
            }
        }
        SendStringMessage(SCAN_COMPLITED, FoundIPAddress);
        return answer;
    }

    //----------------------------  Ping  -----------------------------------------------------------
    //private String TAG = "myLogs";
    public String ping(String url) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 " + url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((i = reader.read(buffer)) > 0) output.append(buffer, 0, i);
            reader.close();
            // body.append(output.toString()+"\n");
            str = output.toString();
            //Log.d(TAG, str);
        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
        }
        return str;
    }

    //***********************************  UI  FUNCTIONS  *******************************************
    //-----------------------------------------------------------------------------------------------
    private void SendStringMessage(int msgCode, String txt) {
        Message msg;
        msg = hScannerReport.obtainMessage(msgCode, txt);
        hScannerReport.sendMessage(msg);
    }

    //-----------------------------------------------------------------------------------------------
    private void SendEmptyMessage(int msgCode)
    {
        hScannerReport.sendEmptyMessage(msgCode);
    }

}
