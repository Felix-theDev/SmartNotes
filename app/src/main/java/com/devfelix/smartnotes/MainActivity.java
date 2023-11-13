package com.devfelix.smartnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText mLoginEmail, mLoginPassword;
    private RelativeLayout mLogin, mGoToSignUp;
    private TextView mGoToForgotPassword;

    private FirebaseAuth firebaseAuth;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        getSupportActionBar().hide();

        mLoginEmail = findViewById(R.id.loginemail);
        mLoginPassword = findViewById(R.id.loginpassword);
        mLogin = findViewById(R.id.login);
        mGoToForgotPassword = findViewById(R.id.gotoforgotpassword);
        mGoToSignUp = findViewById(R.id.gotosignup);

        mProgressBar= findViewById(R.id.progressbarmainactivity);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            finish();
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
        }


        mGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });
        mGoToForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mLoginEmail.getText().toString().trim();
                String password = mLoginPassword.getText().toString().trim();

                if(mail.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else {

                    //login the user


                    mProgressBar.setVisibility(View.VISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                checkEmailVerification();
                            }
                         else { Exception exception = task.getException();
                               if (exception instanceof FirebaseAuthInvalidUserException) {
                                    // Handle non-existent account
                                    Toast.makeText(getApplicationContext(), "Account doesn't exist. Please sign up.", Toast.LENGTH_SHORT).show();
                                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Handle wrong password
                                    Toast.makeText(getApplicationContext(), "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle other authentication failures
                                    Toast.makeText(getApplicationContext(), "Authentication failed. Please try again later.", Toast.LENGTH_SHORT).show();
                                }}
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified() == true){
            Toast.makeText(getApplicationContext(), "Successfully Logged In", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Verify Your Account", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}