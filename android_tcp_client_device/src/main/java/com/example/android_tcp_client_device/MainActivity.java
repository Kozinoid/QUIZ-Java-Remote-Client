package com.example.android_tcp_client_device;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //----------------------------------------------------------------------------------------------
    private static final int port = 8060;
    TCP_Client_Connection myDevice;
    //LanNetworkScanner lanScanner;

    //---------------------------------  UI  -------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CreateUI();
        CreateConnection();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        CreateUI();
        CreateConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Disconnect();
    }

    //----------------------------------------------------------------------------------------------
    private void CreateUI()
    {
        setContentView(R.layout.activity_main);
    }

    //----------------------------------------------------------------------------------------------
    private void CreateConnection()
    {
        //*************************************************************************
        //|                     ЗДЕСЬ БУДЕТ ПОИСК СЕРВЕРА,                        |
        //|                реализованный в LanNetworkScanner                      |
        //*************************************************************************
        //lanScanner = new LanNetworkScanner(port, onScannerAnswer);
        //lanScanner.onScan();
        //*************************************************************************

        Connect("tcp://192.168.59.42:8060/");
    }

    //-------------------- Получает сообщения от сканнера ------------------------------------------
//    Handler.Callback onScannerAnswer = new Handler.Callback()
//    {
//        @Override
//        public boolean handleMessage(@NonNull Message msg) {
//            switch (msg.what)
//            {
//                case LanNetworkScanner.STRING_MESSAGE:
//                    TextOut("Scanning " + msg.obj + "...");
//                    break;
//                case LanNetworkScanner.SCAN_COMPLITED:
//                    TextOut("Found: " + msg.obj);
//                    Connect(LanNetworkScanner.FoundIPAddress);
//                    break;
//            }
//            return false;
//        }
//    };

    //----------------------------------------------------------------------------------------------
    private void Connect(String addrString)
    {
        myDevice = new TCP_Client_Connection(addrString, onRecieveMessage);
        myDevice.Connect();
    }

    //----------------------------------------------------------------------------------------------
    private void Disconnect()
    {
        myDevice.Disconnect();
    }

    //----------------------------------------------------------------------------------------------
//    private void TextOut(String text)
//    {
//        tvText.setText(text);
//    }


    //------------------ Получает TCP сообщения в виде строки в msg.obj ----------------------------
    Handler.Callback onRecieveMessage = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case TCP_Client_Connection.CONNECTED:
                    SetUIControls(true);
                    break;

                case TCP_Client_Connection.TEXT_MESSAGE:
                    //TextOut((String) msg.obj);
                    break;
            }
            return false;
        }
    };

    //--------------------- Состояние органоы управления -------------------------------------------
    private void SetUIControls(boolean enable)
    {
        if (enable)
        {
            EnableUI();
        }
        else
        {
            DisableUI();
        }
    }

    //--------------------------------  Разрешить UI  ----------------------------------------------
    private void EnableUI()
    {
        // Сюда вписываем все Control, которые нужно активировать
        //btnSend.setEnabled(true);
    }

    //--------------------------------  Запретить UI  ----------------------------------------------
    private void DisableUI()
    {
        // Сюда вписываем все Control, которые нужно деактивировать
        //btnSend.setEnabled(false);
    }

    //----------------------------------- Кнопка Send ----------------------------------------------
    @Override
    public void onClick(View v) {
//        switch (v.getId())
//        {
//            case R.id.btnSend:
//                //myDevice.SendMessage(teInput.getText().toString());
//                break;
//        }
    }
}
