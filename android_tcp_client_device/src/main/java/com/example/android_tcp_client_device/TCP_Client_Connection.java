package com.example.android_tcp_client_device;

import android.os.Handler;
import android.os.Message;
//import android.util.Log;

import androidx.annotation.NonNull;

//--------------------------------------------------------------------------------------------------
public class TCP_Client_Connection extends TCP_Client_Device
{
    //----------------------------- Constants ------------------------------------------------------
    public static final int TEXT_MESSAGE = 0;
    public static final int CONNECTED = 1;

    //******************************  CONSTRUCTOR  *************************************************
    public TCP_Client_Connection(String conID, Handler.Callback hc)
    {
        super(conID, hc);
    }

    //----------------------------------------------------------------------------------------------
    @Override
    protected void finalize() throws Throwable {
        SendMessage("#e#n#d");
        Disconnect();
        super.finalize();
    }

    //****************************  CONNECTION FUNCTIONS  ******************************************
    //----------------------------------------------------------------------------------------------
    public void Connect()
    {
        onConnect();
        hStringMessage.postDelayed(new Runnable() {
            public void run() {
                SendMessage("#n#e#w");  // Отправляем сообщение о подключении через секунду
            }
        }, 1000);
    }

    //----------------------------------------------------------------------------------------------
    public void Disconnect()
    {
        SendMessage("#e#n#d");  // Отправдяем сообщение о подключении через секунду
        onDisconnect();
    }

    //******************************  MESSAGING  ***************************************************
    //-------------------- Получает сообщения в виде строки в msg.obj ------------------------------
    Handler.Callback onRecieveMessage = new Handler.Callback()
    {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            ProcessRecievedMessage((String) msg.obj);
            return false;
        }
    };

    //-------------------------- Overrided ---------------------------------------------------------
    protected void ProcessRecievedMessage(String message)
    {
        if (message.equals("#y#e#s"))
        {
            SendEmptyMessage(CONNECTED);
        }
        else
        {
            SendStringMessage(message);
        }
    }

    //---------------- Вызывается из внешней программы ---------------------------------------------
    void SendMessage(String message)
    {
        onSendRequest(message);
    }



    //***********************************  UI  FUNCTIONS  *******************************************
    //-----------------------------------------------------------------------------------------------
    private void SendStringMessage(String txt) {
        Message msg;
        msg = hStringMessage.obtainMessage(TEXT_MESSAGE, txt);
        hStringMessage.sendMessage(msg);
    }

    //-----------------------------------------------------------------------------------------------
    private void SendEmptyMessage(int msgCode)
    {
        hStringMessage.sendEmptyMessage(msgCode);
    }
}
