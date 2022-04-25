package com.example.jstock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout favStockLayout;
    private FrameLayout frameLayout;
    private FrameLayout.LayoutParams params;
    private FragmentManager ft;

    private EditText edtSearch;
    private Button btnSearch;
    private ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        boolean thrownFromInfo = getIntent().getBooleanExtra("INVALID", false);
        if (thrownFromInfo) {
            Toast.makeText(HomeActivity.this, "Invalid Name, Try Again", Toast.LENGTH_SHORT).show();
        }

        BroadcastReceiver receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        edtSearch = findViewById(R.id.edt_search);
        btnSearch = findViewById(R.id.btn_search);
        btnMenu = findViewById(R.id.btn_menu);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                int id = item.getItemId();

                if(id == R.id.menu_about) {
                    Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(id == R.id.menu_signout) {
                    Global.mAuth.signOut();

                    SharedPreferences sharedPreferences = getSharedPreferences("LoggedUser", MODE_PRIVATE);
                    sharedPreferences.edit().clear().commit();

                    Global.loggedUser = null;
                    Global.UID = "";

                    Intent intent = new Intent(HomeActivity.this, OpenActivity.class);
                    startActivity(intent);
                    finish();
                }

                return true;
            }
        });

        View headerLayout = navigationView.getHeaderView(0);
        TextView txtUsername = headerLayout.findViewById(R.id.txt_username);
        TextView txtEmail = headerLayout.findViewById(R.id.txt_email);

        try {
            txtUsername.setText(Global.loggedUser.getUsername());
            txtEmail.setText(Global.loggedUser.getEmail());
        }
        catch (Exception e) {
            txtUsername.setText("");
            txtEmail.setText("");
        }

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        favStockLayout = findViewById(R.id.fav_stock_layout);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stockName = edtSearch.getText().toString().trim();
                if(stockName.equals("")) {
                    Toast.makeText(HomeActivity.this, "Invalid Data", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(HomeActivity.this, InfoActivity.class);
                intent.putExtra("STOCK_NAME", stockName);
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
        frameLayout = new FrameLayout(HomeActivity.this);
        frameLayout.setId(View.generateViewId());

        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(params);

        favStockLayout.addView(frameLayout, params);

        ft = getSupportFragmentManager();

        if(!ft.isDestroyed()) {
            ft.beginTransaction().add(frameLayout.getId(), FavoriteStocksFragment.newInstance(stockName, "Home", "")).commit();
        }
    }
}