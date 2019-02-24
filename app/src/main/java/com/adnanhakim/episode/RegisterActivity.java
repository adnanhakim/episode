package com.adnanhakim.episode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    // UI Elements
    private RelativeLayout relativeLogin, relativeRegister;
    private TextView tvLoginHeader, tvRegisterHeader;
    private EditText etLoginEmail, etLoginPassword, etRegisterName, etRegisterEmail, etRegisterPassword;
    private Button btnLogin, btnRegister;
    private SignInButton googleSignIn;

    // Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        showRegister();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (firebaseUser == null) {
            // directly go to main page
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }

        tvLoginHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegister();
            }
        });

        tvRegisterHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Register");
                final String name = etRegisterName.getText().toString().trim();
                String email = etRegisterEmail.getText().toString().trim();
                String password = etRegisterPassword.getText().toString().trim();

                if (name.equals("") || email.equals("") || password.equals("")) {
                    Log.d(TAG, "onClick: All/Some fields blank");
                    Toast.makeText(RegisterActivity.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Log.d(TAG, "onClick: Password less than 8 characters");
                    Toast.makeText(RegisterActivity.this, "Password must be minimum 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onClick: All details entered");
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: Successfully created account");
                                firebaseUser = firebaseAuth.getCurrentUser();
                                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: Email verification sent successfully");
                                            saveName(name, firebaseUser.getUid());
                                            Toast.makeText(RegisterActivity.this, "Email verification sent successfully", Toast.LENGTH_SHORT).show();
                                            showLogin();
                                        } else {
                                            Log.d(TAG, "onComplete: Email verification could not be sent");
                                            Toast.makeText(RegisterActivity.this, "Email verification not sent", Toast.LENGTH_SHORT).show();
                                        }
                                        firebaseAuth.signOut();
                                    }
                                });
                            } else {
                                Log.d(TAG, "onComplete: Account couldn't be created");
                                Toast.makeText(RegisterActivity.this, "Account couldn't be created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: Exception: " + e.toString());
                        }
                    });
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Login");
                String email = etLoginEmail.getText().toString().trim();
                String password = etLoginPassword.getText().toString().trim();

                if (email.equals("") || password.equals("")) {
                    Log.d(TAG, "onClick: All/Some fields blank");
                    Toast.makeText(RegisterActivity.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                firebaseUser = firebaseAuth.getCurrentUser();
                                if(firebaseUser.isEmailVerified()) {
                                    Log.d(TAG, "onComplete: Signed in successfully");
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Log.d(TAG, "onComplete: Email not verified");
                                    Toast.makeText(RegisterActivity.this, "Email is not verified", Toast.LENGTH_SHORT).show();
                                    firebaseAuth.signOut();
                                }
                            } else {
                                Log.d(TAG, "onComplete: Invalid user name or password");
                                Toast.makeText(RegisterActivity.this, "Invalid user name or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void init() {
        // Login elements
        relativeLogin = findViewById(R.id.relativeLogin);
        tvLoginHeader = findViewById(R.id.tvLoginHeader);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Register elements
        relativeRegister = findViewById(R.id.relativeRegister);
        tvRegisterHeader = findViewById(R.id.tvRegisterHeader);
        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Google Sign-in
        googleSignIn = findViewById(R.id.btnGoogleSignIn);
    }

    private void showLogin() {
        tvLoginHeader.setVisibility(View.VISIBLE);
        relativeLogin.setVisibility(View.VISIBLE);

        tvRegisterHeader.setVisibility(View.INVISIBLE);
        relativeRegister.setVisibility(View.INVISIBLE);
    }

    private void showRegister() {
        tvRegisterHeader.setVisibility(View.VISIBLE);
        relativeRegister.setVisibility(View.VISIBLE);

        tvLoginHeader.setVisibility(View.INVISIBLE);
        relativeLogin.setVisibility(View.INVISIBLE);
    }

    private void saveName(String name, String uid) {
        databaseReference.child("users").child(uid).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Database updated");
                } else {
                    Log.d(TAG, "onComplete: Database not updated");
                }
            }
        });
    }
}
