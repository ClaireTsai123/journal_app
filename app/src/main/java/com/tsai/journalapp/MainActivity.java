package com.tsai.journalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //widgets
    EditText email, pwd;
    Button btn_signin, btn_signup;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        pwd = findViewById(R.id.password);
        btn_signin = findViewById(R.id.email_signin);
        btn_signup = findViewById(R.id.email_signup);

        btn_signup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        firebaseAuth = FirebaseAuth.getInstance();

        btn_signin.setOnClickListener(v -> {
            loginWithEmailandPassword(email.getText().toString().trim(),
                    pwd.getText().toString().trim());
        });
    }

    private void loginWithEmailandPassword(String email, String pwd) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnSuccessListener(
                    authResult -> {
                        currentUser = firebaseAuth.getCurrentUser();

                        Intent i = new Intent(MainActivity.this, JournalListActivity.class);
                        startActivity(i);
                    }
            );
        }
    }
}