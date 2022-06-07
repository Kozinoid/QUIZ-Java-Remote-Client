package com.example.quiz_sheet_remote_control;

import android.os.Handler;

import eneter.messaging.diagnostic.EneterTrace;
import eneter.messaging.endpoints.typedmessages.*;
import eneter.messaging.messagingsystems.messagingsystembase.*;
import eneter.messaging.messagingsystems.tcpmessagingsystem.TcpMessagingSystemFactory;
import eneter.net.system.EventHandler;

//--------------------------------------------------------------------------------------------------
public class TCP_Client_Device {

    // Request message type
    public static class MyRequest
    {
        public String Text;
    }

    // Response message type
    public static class MyResponse
    {
        public String Text;
    }

    // Sender sending MyRequest and as a response receiving MyResponse.
    private IDuplexTypedMessageSender<MyResponse, MyRequest> mySender;
    protected Handler hStringMessage;          // Handle callback for UI controls
    protected String connectionID;
    protected boolean connected = false;

    //----------------------------------------------------------------------------------------------
    public TCP_Client_Device()
    {

    }

    //----------------------------------------------------------------------------------------------
    protected void Connect(final String conID, Handler.Callback hc)
    {
        hStringMessage = new Handler(hc);
        connectionID = conID;
    }

    //----------------------------------------------------------------------------------------------
    protected void onConnect()
    {
        // Open the connection in another thread.
        try
        {
            openConnection(connectionID);
            connected = true;
        }
        catch (Exception err)
        {
            EneterTrace.error("Open connection failed.", err);
        }
    }

    //----------------------------------------------------------------------------------------------
    protected void onDisconnect()
    {
        connected = false;
        mySender.detachDuplexOutputChannel();
    }

    //----------------------------------------------------------------------------------------------
    private void openConnection(String conID) throws Exception
    {
        // Create sender sending MyRequest and as a response receiving MyResponse
        IDuplexTypedMessagesFactory aSenderFactory = new DuplexTypedMessagesFactory();
        mySender = aSenderFactory.createDuplexTypedMessageSender(MyResponse.class, MyRequest.class);

        // Subscribe to receive response messages.
        mySender.responseReceived().subscribe(myOnResponseHandler);

        // Create TCP messaging for the communication.
        IMessagingSystemFactory aMessaging = new TcpMessagingSystemFactory();
        IDuplexOutputChannel anOutputChannel =
                aMessaging.createDuplexOutputChannel(conID);

        // Attach the output channel to the sender and be able to send
        // messages and receive responses.
        mySender.attachDuplexOutputChannel(anOutputChannel);
    }

    //----------------------------------------------------------------------------------------------
    protected void onSendRequest(String message)
    {
        // Create the request message.
        MyRequest aRequestMsg = new MyRequest();
        aRequestMsg.Text = message;

        // Send the request message.
        try
        {
            mySender.sendRequestMessage(aRequestMsg);
        }
        catch (Exception err)
        {
            EneterTrace.error("Sending the message failed.", err);
        }
    }

    //----------------------------------------------------------------------------------------------
    private void onResponseReceived(Object sender, final TypedResponseReceivedEventArgs<MyResponse> e)
    {
        ProcessRecievedMessage(e.getResponseMessage().Text);
    }

    //----------------------------------------------------------------------------------------------
    private EventHandler<TypedResponseReceivedEventArgs<MyResponse>> myOnResponseHandler
            = new EventHandler<TypedResponseReceivedEventArgs<MyResponse>>()
    {
        @Override
        public void onEvent(Object sender,
                            TypedResponseReceivedEventArgs<MyResponse> e)
        {
            onResponseReceived(sender, e);
        }
    };

    //----------------------------------------------------------------------------------------------
    protected void ProcessRecievedMessage(String message)
    {
        // Overrided in child class
    }
}
