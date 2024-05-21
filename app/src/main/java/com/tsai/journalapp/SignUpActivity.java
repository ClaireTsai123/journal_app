package com.tsai.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    //widgets
    EditText email_create, password_create, username_create;
    Button btn_signup;
    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_create = findViewById(R.id.email_create);
        password_create = findViewById(R.id.password_create);
        username_create = findViewById(R.id.userName);
        btn_signup = findViewById(R.id.signup_btn);

        //Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Listening for changes in the authentication state and response accordingly when the
        //state changes
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                //check if the user is logged in or not
                if (currentUser != null) {
                    //user already logged in
                } else {
                    //user signed out
                }
            }
        };

        btn_signup.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(email_create.getText().toString()) &&
                    !TextUtils.isEmpty(password_create.getText().toString()) &&
                    !TextUtils.isEmpty(username_create.getText().toString())) {
                String email = email_create.getText().toString().trim();
                String pwd = password_create.getText().toString().trim();
                String username = username_create.getText().toString().trim();
                createUserAccount(email, pwd, username);
            } else {
                Toast.makeText(SignUpActivity.this, "No Empty fields are allowed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserAccount(String email, String pwd, String username) {
        if (!TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            //The user is created successfully!
                            Toast.makeText(SignUpActivity.this,"Account is created successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
}