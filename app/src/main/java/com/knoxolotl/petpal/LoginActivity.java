package com.knoxolotl.petpal;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    SharedPreferences sp;

    User thisUser;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FirebaseAuth.getInstance().signOut();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        intent = new Intent(LoginActivity.this, MainActivity.class);

        if (mAuth.getCurrentUser() != null) {  // if there is a user logged in
            Log.d("Auth", "User is already logged in");
            Toast.makeText(this, "Logging in as " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            //sp.edit().putString("household", null).apply();
            startActivity(intent);
            finish();
        }

        TextInputLayout emailField = findViewById(R.id.email_field);
        TextInputLayout passwordField = findViewById(R.id.password_field);

        Button loginButton = findViewById(R.id.login_button);
        Button signupButton = findViewById(R.id.signup_button);

        loginButton.setOnClickListener(v -> {
            String email = emailField.getEditText().getText().toString();
            String password = passwordField.getEditText().getText().toString();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Please enter an email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                startActivity(intent);
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        signupButton.setOnClickListener(v -> {
            String email = emailField.getEditText().getText().toString();
            String password = passwordField.getEditText().getText().toString();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter an email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                createUser(email, password, user.getUid());

                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                passwordField.setError(null);
                                emailField.setError(null);

                                if (password.length() < 8) {
                                    Toast.makeText(LoginActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                                    passwordField.setError("Password must be at least 8 characters");
                                } else if (!isValidEmail(email)) {
                                    emailField.setError("Invalid email");
                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    passwordField.setError(null);
                                    emailField.setError(null);
                                }


                                //TODO: Do something
                            }
                        }
                    });
        });

        //startActivity(intent);

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    private void createUser(String email, String password, String uid){
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("chip", false);

        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        SharedPreferences.Editor ed = sp.edit();
                        ed.putString("uid", uid);
                        ed.putString("email", email);
                        ed.putBoolean("chip", false);
                        ed.apply();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private static Pet fixMissingValues(Pet pet) {
        if (pet.getMed_schedule() == null) {
            pet.setMed_schedule(new ArrayList<String>());
        } if (pet.getFood_schedule() == null) {
            pet.setFood_schedule(new ArrayList<String>());
        } if (pet.getFood_history() == null) {
            pet.setFood_history(new ArrayList<DataHistory>());
        } if (pet.getMed_history() == null) {
            pet.setMed_history(new ArrayList<DataHistory>());
        } if (pet.getWalk_history() == null) {
            pet.setWalk_history(new ArrayList<DataHistory>());
        } if (pet.getLitter_history() == null) {
            pet.setLitter_history(new ArrayList<DataHistory>());
        } if (pet.getWater_history() == null) {
            pet.setWater_history(new ArrayList<DataHistory>());
        } if (pet.getNext_litter_change() == null) {
            pet.setNext_litter_change("");
        }

        return pet;
    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}