package com.example.oskin.servicestartapp;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    public static final int MSG_REGISTER_CLIENT = 0x00001;
    public static final int MSG_UNREGISTER_CLIENT = 0x00002;
    public static final int MSG_SERVICE_STOP = 0x00003;
    public static final int MSG_CURRENT_VALUE = 0x00004;
    public static final int MSG_INTERRUPT = 0x00005;

    public static final String MSG_KEY = "message_kew";

    private final static int MODE = Service.START_NOT_STICKY;

    private boolean isInterrupted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg;
                    for (int i = 0; ;i++) {
                        if (isInterrupted){
                            msg = Message.obtain(null, MSG_SERVICE_STOP);
                            for (Messenger messenger:mClients) {
                                messenger.send(msg);
                            }
                            stopSelf();
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
        }).start();
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return MODE;
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
                case MSG_INTERRUPT:
                    isInterrupted = true;
                    break;
            }
        }
    }

    public static final Intent getIntentForSend(@NonNull Context context, @NonNull String message){
        Intent intent = newIntent(context);
        intent.putExtra(MSG_KEY, message);
        return intent;
    }

    public static final Intent newIntent(Context context){
        Intent intent = new Intent(context, MyService.class);
        return intent;
    }
}


