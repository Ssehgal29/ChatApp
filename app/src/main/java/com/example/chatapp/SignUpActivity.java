package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    EditText edt_userName,edt_signUpId,edt_signUpPassword;
    Button btn_signUp;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private static final String TAG = "SignUpActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Chat App Sign Up !!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edt_userName=findViewById(R.id.user_name);
        edt_signUpId=findViewById(R.id.signUp_id);
        edt_signUpPassword=findViewById(R.id.signUp_password);
        btn_signUp=findViewById(R.id.signUp_btn);
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName=edt_userName.getText().toString();
                String signUpId=edt_signUpId.getText().toString();
                String signUpPassword=edt_signUpPassword.getText().toString();
                    if (userName.equals("")) {
                        edt_userName.setError("Please enter your User Name!");
                        edt_userName.requestFocus();
                    }else if(userName.length()<4){
                        edt_userName.setError("User Name Too short!");
                        edt_userName.requestFocus();
                    }else if (signUpId.equals("")){
                        edt_signUpId.setError("Please enter your E-mail!");
                        edt_signUpId.requestFocus();
                    }else if (!signUpId.contains("@gmail.com")){
                        edt_signUpId.setError("Please Enter a Valid E-mail!");
                        edt_signUpId.requestFocus();
                    }else if (signUpPassword.equals("")){
                        edt_signUpPassword.setError("Please enter your Password!");
                        edt_signUpPassword.requestFocus();
                    }else if (signUpPassword.length()<6){
                        edt_signUpPassword.setError("Your Password Length should not be less then 6 digits!");
                        edt_signUpPassword.requestFocus();
                    }else if(edt_signUpId.equals("") && edt_signUpPassword.equals("")){
                        Toast.makeText(SignUpActivity.this, "Please Enter Your Credentials!", Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialog.setTitle("Registering Your Account");
                        progressDialog.setMessage("Please wait while we create your account!");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        firebaseAuth.createUserWithEmailAndPassword(signUpId,signUpPassword).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String uid = currentUser.getUid();
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                    HashMap<String,String> userMap = new HashMap<>();
                                    userMap.put("user_name",userName);
                                    userMap.put("status","Hola Amigos");
                                    userMap.put("image","default");
                                    userMap.put("thumb_image","default");
                                    databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Intent goToMainActivityIntent=new Intent(SignUpActivity.this, MainActivity.class);
                                                goToMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(goToMainActivityIntent);
                                                finish();
                                            }
                                        }
                                    });
                                }else {
                                    progressDialog.hide();
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Sign Up Unsuccessful, Please try again!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
        });
    }
}
