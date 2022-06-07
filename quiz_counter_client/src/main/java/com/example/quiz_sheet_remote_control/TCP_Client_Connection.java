package com.example.quiz_sheet_remote_control;

import android.os.Handler;
import android.os.Message;

//--------------------------------------------------------------------------------------------------
public class TCP_Client_Connection extends TCP_Client_Device
{
    //----------------------------- Constants ------------------------------------------------------
    public static final int TEXT_MESSAGE = 0;
    public static final int CONNECTED = 1;

    //******************************  CONSTRUCTOR  *************************************************
    public TCP_Client_Connection()
    {

    }

    //----------------------------------------------------------------------------------------------
    @Override
    protected void finalize() throws Throwable {
        Disconnect();
        super.finalize();
    }

    //****************************  CONNECTION FUNCTIONS  ******************************************
    //----------------------------------------------------------------------------------------------
    @Override
    public void Connect(String conID, Handler.Callback hc)
    {
        super.Connect(conID, hc);
        Thread anOpenConnectionThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                onConnect();
                SendMessage("#n#e#w");  // Отправляем сообщение о подключении
            }
        });
        anOpenConnectionThread.start();
    }

    //----------------------------------------------------------------------------------------------
    public void Disconnect()
    {
        SendMessage("#e#n#d");  // Отправдяем сообщение о разъединении
        onDisconnect();
    }

    //******************************  MESSAGING  ***************************************************
    //-------------------------- Overrided ---------------------------------------------------------
    @Override
    protected void ProcessRecievedMessage(String message)
    {
        super.ProcessRecievedMessage(message);

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
