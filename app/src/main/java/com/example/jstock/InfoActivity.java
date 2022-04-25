package com.example.jstock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class InfoActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnCompare;
    private ImageButton btnFavorite;

    private TextView txtName, txtCheckTime, txtOpen, txtClose, txtHigh, txtLow, txtVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        BroadcastReceiver receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        txtName = findViewById(R.id.txt_name);
        txtCheckTime = findViewById(R.id.txt_check_time);
        txtOpen = findViewById(R.id.txt_open);
        txtClose = findViewById(R.id.txt_close);
        txtHigh = findViewById(R.id.txt_high);
        txtLow = findViewById(R.id.txt_low);
        txtVolume = findViewById(R.id.txt_volume);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        String stockName = getIntent().getStringExtra("STOCK_NAME");

        btnFavorite = findViewById(R.id.btn_favorite);

        if (!Global.loggedUser.getFavoriteStocks().contains(stockName)) {
            btnFavorite.setImageResource(R.drawable.ic_not_favorite);
        }
        else {
            btnFavorite.setImageResource(R.drawable.ic_favorite);
        }

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("***INFO**", Global.loggedUser == null ? "true" : "false");

                if(!Global.loggedUser.getFavoriteStocks().contains(stockName) || Global.loggedUser.getFavoriteStocks() == null) {
                    Global.loggedUser.addFavoriteStock(stockName);
                    Global.dbRef.child("Users").child(Global.UID).child("favoriteStocks").setValue(Global.loggedUser.getFavoriteStocks());

                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                }
                else
                {
                    Global.loggedUser.removeFavoriteStock(stockName);
                    Global.dbRef.child("Users").child(Global.UID).child("favoriteStocks").setValue(Global.loggedUser.getFavoriteStocks());

                    btnFavorite.setImageResource(R.drawable.ic_not_favorite);
                }
            }
        });

        btnCompare = findViewById(R.id.btn_compare);
        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, CompareActivity.class);
                intent.putExtra("STOCK1", stockName);

                startActivity(intent);
                finish();
            }
        });

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Stock s1 = null;
                try {
                    s1 = Global.generateStock(stockName, InfoActivity.this);

                    class MyRunnable implements Runnable {
                        private Stock s1;

                        public void setData(Stock data) {
                            this.s1 = data;
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
                        }
                    }

                    MyRunnable runnable = new MyRunnable();
                    runnable.setData(s1);
                    runOnUiThread(runnable);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }
}