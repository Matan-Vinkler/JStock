package com.example.jstock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class Info2Activity extends AppCompatActivity {

    private TextView txtName, txtName2;
    private TextView txtCheckTime, txtCheckTime2;
    private TextView txtOpen, txtOpen2;
    private TextView txtClose, txtClose2;
    private TextView txtHigh, txtHigh2;
    private TextView txtLow, txtLow2;
    private TextView txtVolume, txtVolume2;

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info2);

        BroadcastReceiver receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        txtName = findViewById(R.id.txt_name);
        txtName2 = findViewById(R.id.txt_name2);

        txtCheckTime = findViewById(R.id.txt_check_time);
        txtCheckTime2 = findViewById(R.id.txt_check_time2);

        txtOpen = findViewById(R.id.txt_open);
        txtOpen2 = findViewById(R.id.txt_open2);

        txtClose = findViewById(R.id.txt_close);
        txtClose2 = findViewById(R.id.txt_close2);

        txtHigh = findViewById(R.id.txt_high);
        txtHigh2 = findViewById(R.id.txt_high2);

        txtLow = findViewById(R.id.txt_low);
        txtLow2 = findViewById(R.id.txt_low2);

        txtVolume = findViewById(R.id.txt_volume);
        txtVolume2 = findViewById(R.id.txt_volume2);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Info2Activity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        String stock1 = getIntent().getStringExtra("STOCK1");
        String stock2 = getIntent().getStringExtra("STOCK2");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Stock s1 = null;
                Stock s2 = null;

                try {
                    class MyRunnable implements Runnable {
                        private Stock s1;
                        private Stock s2;

                        public void setData(Stock data1, Stock data2) {
                            this.s1 = data1;
                            this.s2 = data2;
                        }

                        @Override
                        public void run() {
                            txtName.setText(s1.getName());
                            txtCheckTime.setText(s1.getCheckTime());
                            txtOpen.setText(s1.getOpenPrice());
                            txtClose.setText(s1.getPrevClosePrice());
                            txtHigh.setText(s1.getHighPrice());
                            txtLow.setText(s1.getLowPrice());
                            txtVolume.setText(s1.getVolume());

                            txtName2.setText(s2.getName());
                            txtCheckTime2.setText(s2.getCheckTime());
                            txtOpen2.setText(s2.getOpenPrice());
                            txtClose2.setText(s2.getPrevClosePrice());
                            txtHigh2.setText(s2.getHighPrice());
                            txtLow2.setText(s2.getLowPrice());
                            txtVolume2.setText(s2.getVolume());
                        }
                    }

                    s1 = Global.generateStock(stock1, Info2Activity.this);
                    Thread.sleep(1000);
                    s2 = Global.generateStock(stock2, Info2Activity.this);

                    MyRunnable runnable = new MyRunnable();
                    runnable.setData(s1, s2);
                    runOnUiThread(runnable);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }
}