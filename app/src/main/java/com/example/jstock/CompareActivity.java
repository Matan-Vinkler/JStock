package com.example.jstock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CompareActivity extends AppCompatActivity {

    private LinearLayout favStockLayout;
    private FrameLayout frameLayout;
    private FrameLayout.LayoutParams params;
    private FragmentManager ft;

    private String stock1;

    private ImageButton btnBack;
    private Button btnSearch;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        BroadcastReceiver receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        favStockLayout = findViewById(R.id.fav_stock_layout);

        stock1 = getIntent().getStringExtra("STOCK1");

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, InfoActivity.class);
                intent.putExtra("STOCK_NAME", stock1);
                startActivity(intent);
                finish();
            }
        });

        edtSearch = findViewById(R.id.edt_search);
        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stock2 = edtSearch.getText().toString().trim();
                if(stock2.equals("") || stock2.equals(stock1)) {
                    Toast.makeText(CompareActivity.this, "Invalid Data", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(CompareActivity.this, Info2Activity.class);

                intent.putExtra("STOCK1", stock1);
                intent.putExtra("STOCK2", stock2);
                startActivity(intent);
                finish();
            }
        });

        Global.dbRef.child("Users").child(Global.UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                List<String> favoriteStocks = user.getFavoriteStocks();

                String stockName;
                for(int i = 1; i < favoriteStocks.size(); i++) {
                    stockName = favoriteStocks.get(i);
                    generateFragment(stockName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void generateFragment(String stockName) {
        frameLayout = new FrameLayout(CompareActivity.this);
        frameLayout.setId(View.generateViewId());

        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(params);

        favStockLayout.addView(frameLayout, params);

        ft = getSupportFragmentManager();

        if(!ft.isDestroyed()) {
            ft.beginTransaction().add(frameLayout.getId(), FavoriteStocksFragment.newInstance(stockName, "Compare", stock1)).commit();
        }
    }
}