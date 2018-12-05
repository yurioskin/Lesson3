package com.example.oskin.servicestartapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class MyIntentService extends IntentService {

    public static final int MSG_REGISTER_CLIENT = 0x00001;
    public static final int MSG_UNREGISTER_CLIENT = 0x00002;
    public static final int MSG_CURENT_INTENT_STOP = 0x00003;
    public static final int MSG_CURRENT_VALUE = 0x00004;

    public static boolean sShouldStop;

    public static final String MSG_KEY = "message_kew";

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        sShouldStop = false;
        return super.onStartCommand(intent, flags, startId);
    }


    private List<Messenger> mClients = new ArrayList<>();

    private Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return mMessenger.getBinder();
    }

    private class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
            }
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        sShouldStop = false;
        Message msg;
        try {
            for (int i = 0; ;i++) {
                if (sShouldStop){
                    msg = Message.obtain(null, MSG_CURENT_INTENT_STOP, intent.getStringExtra(MSG_KEY));
                    for (Messenger messenger:mClients) {
                        messenger.send(msg);
                    }
                    return;
                }
                msg = Message.obtain(null,MSG_CURRENT_VALUE,i);
                for (Messenger messenger:mClients) {
                    messenger.send(msg);
                }
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }
    }



    public static final Intent getIntentForSend(@NonNull Context context, @NonNull String message){
        Intent intent = newIntent(context);
        intent.putExtra(MSG_KEY, message);
        return intent;
    }

    public static final Intent newIntent(Context context){
        Intent intent = new Intent(context, MyIntentService.class);
        return intent;
    }
}


