package com.example.chatapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    public SettingsFragment() {
        // Required empty public constructor
    }

    EditText edt_chngPassword;
    Button btn_chngPassword, btn_MainChngPassword, btn_rmvAccount, btn_logOut;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Settings");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        edt_chngPassword = view.findViewById(R.id.chng_password);
        btn_chngPassword = view.findViewById(R.id.btn_chng_password);
        btn_MainChngPassword = view.findViewById(R.id.btn_mainChngPassword);
        btn_rmvAccount = view.findViewById(R.id.btn_rmv_account);
        btn_logOut = view.findViewById(R.id.btn_logout);
        progressDialog=new ProgressDialog(getActivity());
        edt_chngPassword.setVisibility(View.GONE);
        btn_MainChngPassword.setVisibility(View.GONE);

        btn_chngPassword.setOnClickListener(this);
        btn_rmvAccount.setOnClickListener(this);
        btn_logOut.setOnClickListener(this);
        btn_MainChngPassword.setOnClickListener(this);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_chng_password:
                edt_chngPassword.setVisibility(View.VISIBLE);
                btn_MainChngPassword.setVisibility(View.VISIBLE);
                btn_chngPassword.setVisibility(View.GONE);
                break;
            case R.id.btn_mainChngPassword:
                String chngPassword=edt_chngPassword.getText().toString();
                if (firebaseUser != null && !chngPassword.equals("")) {
                    if (chngPassword.length() < 6) {
                        edt_chngPassword.setError("Password too short, enter minimum 6 digits");
                    } else {
                        progressDialog.setTitle("Changing Password");
                        progressDialog.setMessage("Please Wait while we're updating your new password!");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        firebaseUser.updatePassword(chngPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent goBackToLoginActivityIntent = new Intent(getActivity(),LoginActivity.class);
                                            goBackToLoginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(goBackToLoginActivityIntent);
                                            getActivity().finish();
                                        } else {
                                            progressDialog.hide();
                                            Toast.makeText(getActivity(), "Failed to update password due to "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else if (chngPassword.equals("")) {
                    edt_chngPassword.setError("Please Enter a new password");
                }
                break;
            case R.id.btn_rmv_account:
                if (firebaseUser!=null){
                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.setTitle("Removing account");
                            progressDialog.setMessage("Please wait while we're De-activating your account!");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Account Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                Intent goBackToLoginActivityIntent = new Intent(getActivity(),LoginActivity.class);
                                startActivity(goBackToLoginActivityIntent);
                                getActivity().finish();
                            }else {
                                progressDialog.hide();
                                Toast.makeText(getActivity(), "Account Could not be deleted due to "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                Intent goBackToLoginActivityIntent = new Intent(getActivity(),LoginActivity.class);
                startActivity(goBackToLoginActivityIntent);
                getActivity().finish();
                break;
        }
    }
}
