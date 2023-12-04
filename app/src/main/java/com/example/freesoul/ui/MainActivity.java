package com.example.freesoul.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.freesoul.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Button login,register;
    EditText _etEmail, _etPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(this);
        register = findViewById(R.id.btn_register);
        register.setOnClickListener(this);
        _etEmail = findViewById(R.id.et_email);
        _etPassword = findViewById(R.id.et_password);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            if (currentUser.isEmailVerified()){
                Intent home = new Intent(this, Home.class);
                home.putExtra("email", currentUser.getEmail());
                startActivity(home);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_login){
            mAuth.signInWithEmailAndPassword(_etEmail.getText().toString(), _etPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user!=null){
                            if (user.isEmailVerified()){
                                Intent home = new Intent(MainActivity.this, Home.class);
                                home.putExtra("email", _etEmail.getText().toString());
                                startActivity(home);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Not Verified", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Verfication Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else if (v.getId()==R.id.btn_register) {
            mAuth.createUserWithEmailAndPassword(_etEmail.getText().toString(), _etPassword.getText().toString()).addOnCompleteListener(this,(task) -> {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user!=null){
                        UserProfileChangeRequest profilUpdate = new UserProfileChangeRequest.Builder().setDisplayName("User Test").build();
                        user.updateProfile(profilUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d("gagal update profil", "user profile updated");
                                }
                            }
                        });
                        ///////////////////////////////
                        if (user.isEmailVerified()){
                            Intent home = new Intent(MainActivity.this, Home.class);
                            home.putExtra("email", _etEmail.getText().toString());
                            startActivity(home);
                        }
                        else {
                            final String email = user.getEmail();
                            user.sendEmailVerification().addOnCompleteListener(MainActivity.this,task1 -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Verification eail send to : " + email, Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("eror di verifikasi", "sendEmailverification", task.getException());
                                    Toast.makeText(MainActivity.this, "Failed to send verification email", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        }
                }
                else {
                    Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                }
            });
            
        }
    }
}