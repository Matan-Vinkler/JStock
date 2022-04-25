package com.example.jstock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPassword2;
    private ImageButton btnBack;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edt_username);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtPassword2 = findViewById(R.id.edt_password2);

        btnBack = findViewById(R.id.btn_back);
        btnRegister = findViewById(R.id.btn_register);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, OpenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                String password2 = edtPassword2.getText().toString();

                if(!validateUsername(username) || !validatePassword(password, password2) || !validateEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Not Valid Data", Toast.LENGTH_SHORT).show();
                    return;
                }

                Global.mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = Global.mAuth.getCurrentUser();

                                    User newUser = new User(username, email);
                                    Global.dbRef.child("Users").child(user.getUid()).setValue(newUser);

                                    Global.Login(email, password, RegisterActivity.this);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public boolean validate(String string) {
        if(string.trim().equals("")) {
            return false;
        }

        return true;
    }

    public boolean validateUsername(String username) {
        if(!validate(username)) {
            return false;
        }

        for(int i = 0; i < username.length(); i++) {
            if(!Character.isLetterOrDigit(username.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public boolean validateEmail(String email) {
        if(!validate(email)) {
            return false;
        }

        return true;
    }

    public boolean validatePassword(String p1, String p2) {
        if(!p1.equals(p2)) {
            return false;
        }

        if(!validate(p1)) {
            return false;
        }

        if(p1.length() < 8) {
            return false;
        }

        String numbers = "1234567890";
        String smallLetters = "abcdefghijklmnopqrstuvwxyz";
        String bigLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String symbols = "!@#$%^&*_=+\\/|<>~";
        String chars = numbers + smallLetters + bigLetters + symbols;

        if(!contains(p1, chars)) {
            return false;
        }

        String[] commonPasswords = Global.commonPasswords.split("|");
        for(int i = 0; i < commonPasswords.length; i++) {
            if(p1.equals(commonPasswords[i])) {
                return false;
            }
        }

        return true;
    }

    public boolean contains(String password, String data) {
        for(int i = 0; i < data.length(); i++) {
            if(password.contains(Character.toString(data.charAt(i)))) {
                return true;
            }
        }

        return false;
    }
}