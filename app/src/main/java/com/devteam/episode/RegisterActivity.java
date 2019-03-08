package com.devteam.episode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +     // at least one number
                    "(?=.*[a-z])" +     // at least one lowercase letter
                    "(?=.*[A-Z])" +     // at least one uppercase letter
                    "(?=\\S+$)" +       // no white spaces
                    ".{8,}" +           // minimum 8 characters
                    "$");

    // UI Elements
    private RelativeLayout relativeLogin, relativeRegister;
    private TextInputLayout tilLoginEmail, tilLoginPassword, tilRegisterName, tilRegisterEmail, tilRegisterPassword;
    private Button btnLogin, btnRegister, btnLoginInstead, btnRegisterInstead;

    // Google Sign In
    private SignInButton googleSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 0;
    private FirebaseAuth mAuth;

    // Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
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

        if (firebaseUser != null && firebaseUser.isEmailVerified() == true) {
            // directly go to main page
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            Log.d(TAG, "onCreate: Firebase user" + firebaseUser.getEmail());
            finish();
        } else if (firebaseUser != null) {
            showLogin();
        }

        btnRegisterInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegister();
            }
        });

        btnLoginInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Register");
                final String name = tilRegisterName.getEditText().getText().toString().trim();
                String email = tilRegisterEmail.getEditText().getText().toString().trim();
                String password = tilRegisterPassword.getEditText().getText().toString().trim();
                String context = "Register";

                if (validateName(context, name) & validateEmail(context, email) & validatePassword(context, password)) {
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
                } else {
                    Log.d(TAG, "onClick: All/Some fields blank");
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Login");
                String email = tilLoginEmail.getEditText().getText().toString().trim();
                String password = tilLoginPassword.getEditText().getText().toString().trim();
                String context = "Login";

                if (validateEmail(context, email) & validatePassword(context, password)) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseUser = firebaseAuth.getCurrentUser();
                                if (firebaseUser.isEmailVerified()) {
                                    Log.d(TAG, "onComplete: Signed in successfully");
                                    startActivity(new Intent(RegisterActivity.this, SplashScreenActivity.class));
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
                } else {
                    Log.d(TAG, "onClick: All/Some fields blank");
                }
            }
        });


        // <----------------- Google Sign In --------------------->
        mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1334923011-sqs5e74sg1aui1c06iq974tr9iasi0n6.apps.googleusercontent.com")
                .requestEmail()
                .build();
        //.requestIdToken("1334923011-sqs5e74sg1aui1c06iq974tr9iasi0n6.apps.googleusercontent.com")
        //.requestIdToken(getString(R.string.default_web_client_id))

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void init() {
        // Login elements
        relativeLogin = findViewById(R.id.relativeLogin);
        tilLoginEmail = findViewById(R.id.tilLoginEmail);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterInstead = findViewById(R.id.btnRegisterInstead);

        // Register elements
        relativeRegister = findViewById(R.id.relativeRegister);
        tilRegisterName = findViewById(R.id.tilRegisterName);
        tilRegisterEmail = findViewById(R.id.tilRegisterEmail);
        tilRegisterPassword = findViewById(R.id.tilRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoginInstead = findViewById(R.id.btnLoginInstead);

        // Google Sign-in
        googleSignIn = findViewById(R.id.btnGoogleSignIn);
    }

    private boolean validateName(String context, String name) {
        if (name.isEmpty() && context.equals("Register")) {
            tilRegisterName.setError("Field cannot be empty");
            return false;
        } else {
            tilRegisterName.setError(null);
            return true;
        }
    }

    private boolean validateEmail(String context, String email) {
        if (context.equals("Login")) {
            if (email.isEmpty()) {
                tilLoginEmail.setError("Field cannot be empty");
                return false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilLoginEmail.setError("Not a valid email address");
                return false;
            } else {
                tilLoginEmail.setError(null);
                return true;
            }
        } else {
            if (email.isEmpty()) {
                tilRegisterEmail.setError("Field cannot be empty");
                return false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilRegisterEmail.setError("Not a valid email address");
                return false;
            } else {
                tilRegisterEmail.setError(null);
                return true;
            }
        }
    }

    private boolean validatePassword(String context, String password) {
        if (context.equals("Login")) {
            if (password.isEmpty()) {
                tilLoginPassword.setError("Field cannot be empty");
                return false;
            } else {
                tilLoginPassword.setError(null);
                return true;
            }
        } else {
            if (password.isEmpty()) {
                tilRegisterPassword.setError("Field cannot be empty");
                return false;
            } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                tilRegisterPassword.setError("Password must contain a lowercase, uppercase and a number");
                return false;
            } else {
                tilRegisterPassword.setError(null);
                return true;
            }
        }
    }

    private void showLogin() {
        btnRegisterInstead.setVisibility(View.VISIBLE);
        relativeLogin.setVisibility(View.VISIBLE);

        btnLoginInstead.setVisibility(View.GONE);
        relativeRegister.setVisibility(View.GONE);
    }

    private void showRegister() {
        btnLoginInstead.setVisibility(View.VISIBLE);
        relativeRegister.setVisibility(View.VISIBLE);

        btnRegisterInstead.setVisibility(View.GONE);
        relativeLogin.setVisibility(View.GONE);
    }

    private void checkUserIfExists(final String name, final String uid) {
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: User doesn't exists, saving in database...");
                    saveName(name, uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Database error: " + databaseError.getMessage());
            }
        });
    }

    private void saveName(String name, String uid) {
        User user = new User(name, 0);
        databaseReference.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Database updated");
                } else {
                    Log.d(TAG, "onComplete: Database not updated");
                }
            }
        });
    }

    // <---------- Google Sign In -------------->
    private void signIn() {
        Log.d(TAG, "signIn: Clicked Google Sign In");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed: " + e.getMessage(), e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(RegisterActivity.this, SplashScreenActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            checkUserIfExists(personName, firebaseAuth.getUid());
        }
    }
}
