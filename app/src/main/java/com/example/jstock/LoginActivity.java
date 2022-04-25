package com.example.jstock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        if(getIntent().getBooleanExtra("EmailFlag", false)) {
            CharSequence email = getIntent().getStringExtra("Email");
            edtEmail.setText(email);
        }

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, OpenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if(!validateEmailAndPassword(email, password)) {
                    Toast.makeText(LoginActivity.this, "Invalid Data", Toast.LENGTH_SHORT).show();
                    return;
                }

                Global.Login(email, password, LoginActivity.this);
            }
        });
    }

    private boolean validateEmailAndPassword(String email, String password) {
        if(email.trim().equals("") || password.trim().equals("")) {
            return false;
        }

        return true;
    }
}