package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText edt_resetId;
    Button btn_resetPassword;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle("Reset Password !!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth=FirebaseAuth.getInstance();

        edt_resetId=findViewById(R.id.reset_id);
        btn_resetPassword=findViewById(R.id.btn_reset_password);

        btn_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resetId=edt_resetId.getText().toString();
                if (resetId.equals("")){
                    edt_resetId.setError("Please Enter your E-mail!");
                }else if (!resetId.contains("@gmail.com")){
                    edt_resetId.setError("Please Enter a valid E-mail!");
                }else{
                    firebaseAuth.sendPasswordResetEmail(resetId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent goBackToLoginActivityIntent=new Intent(ResetPasswordActivity.this,LoginActivity.class);
                                startActivity(goBackToLoginActivityIntent);
                                finish();
                                Toast.makeText(ResetPasswordActivity.this, "A Link has been sent to your E-mail!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ResetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
