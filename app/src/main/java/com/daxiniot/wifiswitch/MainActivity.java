package com.daxiniot.wifiswitch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    private final static String TXT1_NAME = "TXT1_NAME";
    private final static String TXT2_NAME = "TXT2_NAME";
    private final static String TXT3_NAME = "TXT3_NAME";
    private final static String TXT4_NAME = "TXT4_NAME";
    //首先声明sharedPreference与它的editor对象，必须在onCreate函数中实例化//声明的sharedPreference

    SharedPreferences preferences;

    //声明sharedPreference的editor

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取当前Activity的sharedPreference,onCreate函数之前不能调用getPreference
        preferences = getPreferences(Activity.MODE_PRIVATE);
        //创建preference的editor对象
        editor = preferences.edit();

        TextView txt1 = findViewById(R.id.txt1);
        TextView txt2 = findViewById(R.id.txt2);
        TextView txt3 = findViewById(R.id.txt3);
        TextView txt4 = findViewById(R.id.txt4);
        txt1.setText(preferences.getString(TXT1_NAME,"开关#1"));
        txt2.setText(preferences.getString(TXT2_NAME,"开关#2"));
        txt3.setText(preferences.getString(TXT3_NAME,"开关#3"));
        txt4.setText(preferences.getString(TXT4_NAME,"开关#4"));
        txt1.setOnLongClickListener(this);
        txt2.setOnLongClickListener(this);
        txt3.setOnLongClickListener(this);
        txt4.setOnLongClickListener(this);
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
                switch (txtView.getId()){
                    case R.id.txt1:
                        editor.putString(TXT1_NAME,name);
                        break;
                    case R.id.txt2:
                        editor.putString(TXT2_NAME,name);
                        break;
                    case R.id.txt3:
                        editor.putString(TXT3_NAME,name);
                        break;
                    case R.id.txt4:
                        editor.putString(TXT4_NAME,name);
                        break;

                }
                editor.commit();

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

}
