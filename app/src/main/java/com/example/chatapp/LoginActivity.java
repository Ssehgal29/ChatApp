package com.example.chatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    TextView txt_no_acc,txt_forgotPassword;
    EditText edt_loginUserId, edt_loginPassword;
    Button btn_login;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Chat App Login !!");

        txt_no_acc=findViewById(R.id.txt_goToSignUp);
        txt_forgotPassword=findViewById(R.id.forgotPassword);
        edt_loginUserId=findViewById(R.id.login_id);
        edt_loginPassword=findViewById(R.id.login_password);
        btn_login=findViewById(R.id.login_button);
        progressDialog=new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser !=null){
                    Toast.makeText(LoginActivity.this, "You are Logged in. Enjoy :)", Toast.LENGTH_SHORT).show();
                    Intent goToMainActivityIntent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(goToMainActivityIntent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "Please Login :)", Toast.LENGTH_SHORT).show();
                }
            }
        };

        txt_no_acc.setOnClickListener(this);
        txt_forgotPassword.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.txt_goToSignUp:
                Intent goToSignUpIntent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(goToSignUpIntent);
                break;

            case R.id.login_button:
                String loginEmail = edt_loginUserId.getText().toString();
                String loginPassword = edt_loginPassword.getText().toString();
                if (loginEmail.equals("")){
                    edt_loginUserId.setError("Please Enter Your E-mail!");
                    edt_loginUserId.requestFocus();
                }else if (!loginEmail.contains("@gmail.com")){
                    edt_loginUserId.setError("Please Enter a Valid E-mail");
                    edt_loginPassword.requestFocus();
                }else if (loginPassword.equals("")){
                    edt_loginPassword.setError("Please enter your password!");
                    edt_loginPassword.requestFocus();
                }else if (loginEmail.equals("") && loginPassword.equals("")){
                    Toast.makeText(this, "Please Enter Your Credentials!", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("Please wait a while!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                progressDialog.hide();
                                Toast.makeText(LoginActivity.this, "User Not Found!", Toast.LENGTH_SHORT).show();
                            }else {
                                progressDialog.dismiss();
                                Intent goToMainActivityIntent = new Intent(LoginActivity.this,MainActivity.class);
                                goToMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(goToMainActivityIntent);
                                finish();
                            }
                        }
                    });
                }
                break;
            case R.id.forgotPassword:
                Intent goToResetPassIntent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(goToResetPassIntent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        LoginActivity.super.onBackPressed();
                        finish();
                    }
                }).create().show();
    }
}
