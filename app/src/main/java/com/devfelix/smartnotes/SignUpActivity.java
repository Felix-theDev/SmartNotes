package com.devfelix.smartnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText mSignUpEmail, mSignUpPassword;
    private RelativeLayout mSignUp;
    private TextView mGoToLogin;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


//        getSupportActionBar().hide();

        mSignUpEmail = findViewById(R.id.signupemail);
        mSignUpPassword = findViewById(R.id.signuppassword);
        mSignUp = findViewById(R.id.signup);
        mGoToLogin = findViewById(R.id.gotologin);

        firebaseAuth = FirebaseAuth.getInstance();



        mGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
               startActivity(intent);
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mSignUpEmail.getText().toString().trim();
                String password = mSignUpPassword.getText().toString().trim();

                if(mail.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "All fields are Required", Toast.LENGTH_SHORT).show();
                }
                else if(password.length() < 7){
                    Toast.makeText(getApplicationContext(), "Password should contain minimum of 7 characters", Toast.LENGTH_SHORT).show();

                }
                else {
                    // register user to firebase

                    firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Registration failed!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

    }

    //send email verification
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser !=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Verification Email has been sent to your mail, verify and Login ", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));

                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Failed to send verification mail ", Toast.LENGTH_SHORT).show();

        }
    }


}