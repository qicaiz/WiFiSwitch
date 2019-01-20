package com.daxiniot.wifiswitch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * WiFi控制4路继电器开关
 */
public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

    private final static String TAG = "MainActivity";
    //首先声明sharedPreference与它的editor对象，必须在onCreate函数中实例化
    private SharedPreferences mPreferences;
    //声明sharedPreference的editor
    private SharedPreferences.Editor mEditor;

    private Socket mSocket;
    private SwitchCompat mSwitch1;
    private SwitchCompat mSwitch2;
    private SwitchCompat mSwitch3;
    private SwitchCompat mSwitch4;
    private TextView mConnectionStatusTv;
    /**
     * 连接线程
     */
    private ConnectThread mConnectThread;
    private PrintStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取当前Activity的sharedPreference,onCreate函数之前不能调用getPreference
        mPreferences = getPreferences(Activity.MODE_PRIVATE);
        //创建preference的editor对象
        mEditor = mPreferences.edit();

        mConnectionStatusTv = findViewById(R.id.tv_connection_status);
        TextView txt1 = findViewById(R.id.txt1);
        TextView txt2 = findViewById(R.id.txt2);
        TextView txt3 = findViewById(R.id.txt3);
        TextView txt4 = findViewById(R.id.txt4);
        txt1.setText(mPreferences.getString(Constants.TXT1_NAME_KEY, Constants.TXT1_NAME_VALUE));
        txt2.setText(mPreferences.getString(Constants.TXT2_NAME_KEY, Constants.TXT2_NAME_VALUE));
        txt3.setText(mPreferences.getString(Constants.TXT3_NAME_KEY, Constants.TXT3_NAME_VALUE));
        txt4.setText(mPreferences.getString(Constants.TXT4_NAME_KEY, Constants.TXT4_NAME_VALUE));
        txt1.setOnLongClickListener(this);
        txt2.setOnLongClickListener(this);
        txt3.setOnLongClickListener(this);
        txt4.setOnLongClickListener(this);

        mSwitch1 = findViewById(R.id.switch1);
        mSwitch2 = findViewById(R.id.switch2);
        mSwitch3 = findViewById(R.id.switch3);
        mSwitch4 = findViewById(R.id.switch4);
        mSwitch1.setOnCheckedChangeListener(this);
        mSwitch2.setOnCheckedChangeListener(this);
        mSwitch3.setOnCheckedChangeListener(this);
        mSwitch4.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.connect) {
            //连接
            if (mSocket == null || !mSocket.isConnected()) {
                mConnectThread = new ConnectThread("192.168.4.1", 333);
                mConnectThread.start();
            }
            //断开连接
            if (mSocket != null && mSocket.isConnected()) {
                try {
                    mSocket.close();
                    item.setTitle("连接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private class ConnectThread extends Thread {
        private String ip;
        private int port;

        public ConnectThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                mSocket = new Socket(ip, port);
                out = new PrintStream(mSocket.getOutputStream());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mBtnConnect.setText("断开");
                        mConnectionStatusTv.setText("连接状态：已连接");
                    }
                });
                //new HeartBeatThread().start();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        final TextView txtView = (TextView) v;
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        final EditText txtName = dialogView.findViewById(R.id.et_name);
        Button okButton = dialogView.findViewById(R.id.btn_ok);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);
        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(dialogView)
                .create();
        dialog.show();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString();
                dialog.dismiss();
                txtView.setText(name);
                switch (txtView.getId()) {
                    case R.id.txt1:
                        mEditor.putString(Constants.TXT1_NAME_KEY, name);
                        break;
                    case R.id.txt2:
                        mEditor.putString(Constants.TXT2_NAME_KEY, name);
                        break;
                    case R.id.txt3:
                        mEditor.putString(Constants.TXT3_NAME_KEY, name);
                        break;
                    case R.id.txt4:
                        mEditor.putString(Constants.TXT4_NAME_KEY, name);
                        break;
                }
                mEditor.commit();

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch1:
                if (isChecked) {
                    //turn on
                    Log.d(TAG, "onCheckedChanged: send1");
                    sendData("1");
                } else {
                    //turn off
                    Log.d(TAG, "onCheckedChanged: send2");
                    sendData("2");
                }
                break;
            case R.id.switch2:
                if (isChecked) {
                    //turn on
                    Log.d(TAG, "onCheckedChanged: send3");
                    sendData("3");
                } else {
                    //turn off
                    Log.d(TAG, "onCheckedChanged: send4");
                    sendData("4");
                }
                break;
            case R.id.switch3:
                if (isChecked) {
                    //turn on
                    Log.d(TAG, "onCheckedChanged: send5");
                    sendData("5");
                } else {
                    //turn off
                    Log.d(TAG, "onCheckedChanged: send6");
                    sendData("6");
                }
                break;
            case R.id.switch4:
                if (isChecked) {
                    //turn on
                    Log.d(TAG, "onCheckedChanged: send7");
                    sendData("7");
                } else {
                    //turn off
                    Log.d(TAG, "onCheckedChanged: send8");
                    sendData("8");
                }
                break;
        }
    }

    private void sendData(final String data) {
        if (out != null) {
            //开启子线程进行网络IO，主线程禁止网络IO
            new Thread() {
                @Override
                public void run() {
                    out.print(data);
                    out.flush();
                }
            }.start();
        }
    }

}
