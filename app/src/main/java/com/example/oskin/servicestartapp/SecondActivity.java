package com.example.oskin.servicestartapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private TextView mTextView;

    private Button mButton;

    private Messenger mService;
    private Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
        initListener();
    }

    private void initView() {
        mTextView = findViewById(R.id.second_activity_text_view);

        mButton = findViewById(R.id.service_stop_service);

    }

    private void initListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyIntentService.sShouldStop = true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService();
    }

    public void bindService(){
        bindService(MyIntentService.getIntentForSend(SecondActivity.this, "Second Activity"), mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.v("SecondActivity","bind");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService();
    }

    public void unbindService(){
        Message msg = Message.obtain(null, MyIntentService.MSG_UNREGISTER_CLIENT);
        msg.replyTo = mMessenger;
        try {
            mService.send(msg);
        }
        catch (RemoteException exc){
            exc.printStackTrace();
        }
        unbindService(mServiceConnection);
        Log.v("SecondActivity","unbind");
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message msg = Message.obtain(null,MyIntentService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            try {
                mService.send(msg);
            }
            catch (RemoteException exc){
                exc.printStackTrace();
            }
            Log.v("SecondActivity","connect");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.v("SecondActivity","disconnect");
        }
    };

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case MyIntentService.MSG_CURRENT_VALUE:
                    setDataFromService(msg.obj);
                    break;
                case MyIntentService.MSG_CURENT_INTENT_STOP:
                    mTextView.setText(getResources().getString(R.string.intent_from_was_stopped, msg.obj.toString()));
                    break;
            }
        }


    }
    private void setDataFromService(Object object){
        mTextView.setText(getResources().getString(R.string.print_string_number, Integer.parseInt(object.toString())));
    }

    public static final Intent newIntent(Context context){
        Intent intent = new Intent(context, SecondActivity.class);
        return intent;
    }
}
