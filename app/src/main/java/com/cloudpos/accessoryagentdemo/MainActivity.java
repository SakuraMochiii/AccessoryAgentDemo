package com.cloudpos.accessoryagentdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudpos.accessoryagentdemo.R;
import com.smartpos.accessoryagent.aidl.IRemoteAccessoryApi;

public class MainActivity extends AppCompatActivity {

    private IRemoteAccessoryApi remoteServe;
    private String INTENT_PACKAGE = "com.smartpos.accessoryagent";
    private String INTENT_ACTION = "com.smartpos.accessoryagent.service.RemoteAccessoryApiService";
    private TextView bindServerStatus;
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteServe = IRemoteAccessoryApi.Stub.asInterface(service);
            bindServerStatus.setText("The AIDL service is Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteServe = null;
            bindServerStatus.setText("The AIDL service is disconnected");
        }
    };
    private TextView startActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindServer();
    }

    private void initView() {
        bindServerStatus = findViewById(R.id.bindServerStatus);
        startActivity = findViewById(R.id.startActivity);
        String json = "{\n" +
                "    \"action\": \"android.intent.action.MAIN\",\n" +
                "    \"className\": \"com.wizarpos.merchantselftest.MainActivity\",\n" +
                "    \"flags\": 268435456,\n" +
                "    \"packageName\": \"com.wizarpos.merchantselftest\",\n" +
                "    \"putExtra\": {\n" +
                "        \"extraData\": \"10\"\n" +
                "    }\n" +
                "}";
        startActivity.setOnClickListener(v -> {
            if (remoteServe != null) {
                try {
                    remoteServe.remoteIntent(json);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(this, "The AIDL service is disconnected", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void bindServer() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(INTENT_PACKAGE, INTENT_ACTION);
        intent.setComponent(componentName);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}