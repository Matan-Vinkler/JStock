package com.example.jstock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

class Runnable1 implements Runnable {
    private ObjectAnimator animator1, animator2;

    public Runnable1(ObjectAnimator animator1, ObjectAnimator animator2) {
        this.animator1 = animator1;
        this.animator2 = animator2;
    }

    public void run() {
        this.animator1.setDuration(2000);
        this.animator1.start();

        while (true) {
            this.animator2.setDuration(4000);
            this.animator2.start();

            this.animator1.setDuration(4000);
            this.animator1.start();
        }
    }
}

public class LaunchActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView progressNumber;

    private View animView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_launch);

        progressBar = findViewById(R.id.progress_bar);
        progressNumber = findViewById(R.id.progress_number);

        animView = findViewById(R.id.anim_view);

        ObjectAnimator animatorSpin = ObjectAnimator.ofFloat(animView, "rotation", 1000f);
        ObjectAnimator animatorMove = ObjectAnimator.ofFloat(animView, "translationX", 1000f);

        animatorSpin.setDuration(4000);
        animatorMove.setDuration(4000);

        animatorSpin.start();
        animatorMove.start();

        SharedPreferences sharedPreferences = getSharedPreferences("LoggedUser", MODE_PRIVATE);
        // sharedPreferences.edit().clear().commit();

        Intent musicService = new Intent(LaunchActivity.this, MusicService.class);
        startService(musicService);

        progressBar.incrementProgressBy(15);
        progressNumber.setText(progressBar.getProgress() + "%");

        Handler handler =  new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Global.database = FirebaseDatabase.getInstance();

                progressBar.incrementProgressBy(45);
                progressNumber.setText(progressBar.getProgress() + "%");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Global.dbRef = Global.database.getReference();

                        progressBar.incrementProgressBy(25);
                        progressNumber.setText(progressBar.getProgress() + "%");

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Global.mAuth = FirebaseAuth.getInstance();
                                // Global.mAuth.signOut();

                                progressBar.incrementProgressBy(15);
                                progressNumber.setText(progressBar.getProgress() + "%");

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Check if user is signed in (non-null) and update UI accordingly.
                                        FirebaseUser currentUser = Global.mAuth.getCurrentUser();
                                        if(currentUser != null) {
                                            Global.dbRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        if(snapshot.getKey().equals(currentUser.getUid())) {
                                                            Global.loggedUser = snapshot.getValue(User.class);
                                                            Global.UID = snapshot.getKey();
                                                            break;
                                                        }
                                                    }

                                                    Intent intent = new Intent(LaunchActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                        else
                                        {
                                            String email = sharedPreferences.getString("Email", "");

                                            if(email.equals("")) {
                                                Intent intent = new Intent(LaunchActivity.this, OpenActivity.class);
                                                startActivity(intent);
                                            }
                                            else {
                                                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                                                intent.putExtra("Email", email);
                                                intent.putExtra("EmailFlag", true);
                                                startActivity(intent);
                                            }
                                        }

                                        finish();
                                    }
                                }, 500);
                            }
                        }, 1000);
                    }
                }, 1000);
            }
        }, 1000);
    }
}