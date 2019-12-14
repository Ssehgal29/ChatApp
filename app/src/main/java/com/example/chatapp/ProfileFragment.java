package com.example.chatapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView imgProfilePic;
    private EditText edtUserName, edtAboutYou;
    private Button btnUpdateInfo;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;
    private static final int GALLERY_PIC =1;
    private StorageReference imageStorage;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Chat App");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfilePic=view.findViewById(R.id.user_profilePic);
        edtUserName=view.findViewById(R.id.userName);
        edtAboutYou=view.findViewById(R.id.aboutYou);
        btnUpdateInfo=view.findViewById(R.id.btn_updtInfo);
        progressDialog=new ProgressDialog(getActivity());
        imageStorage= FirebaseStorage.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String curentUser_uid = currentUser.getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(curentUser_uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString().trim();
                String status = dataSnapshot.child("status").getValue().toString().trim();
//                String image = dataSnapshot.child("image").getValue().toString();
//                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                edtUserName.setText(name);
                edtAboutYou.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imgProfilePic.setOnClickListener(this);
        btnUpdateInfo.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_profilePic:
                //Using Dependencies
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(getActivity());

                break;
            case R.id.btn_updtInfo:
                String userName=edtUserName.getText().toString().trim();
                String status=edtAboutYou.getText().toString().trim();
                if (userName.equals("")){
                    edtUserName.setError("Please Enter your User Name!");
                }else if (status.equals("")){
                    edtAboutYou.setError("Please Enter your Status!");
                }else if (userName.isEmpty() && status.isEmpty()){
                    Toast.makeText(getActivity(), "Please Write Your Personal Information", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setTitle("Saving Changes!");
                    progressDialog.setMessage("Please wait while we are updating your profile!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    HashMap<String,String> userMap=new HashMap<>();
                    userMap.put("user_name",userName);
                    userMap.put("status",status);
                    databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Information Updated!", Toast.LENGTH_SHORT).show();
                            }else {
                                progressDialog.hide();
                                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PIC && resultCode == RESULT_OK){
            Uri imageUri=data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(getActivity());
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Please wait while we're Uploading your image!");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                String CurrentUserID=currentUser.getUid();
                Uri resultUri = result.getUri();
                StorageReference filepath=imageStorage.child("profile_image").child(CurrentUserID+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            String downloadUrl = task.getResult().toString();
                            databaseReference.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Successful Upload!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception exception = result.getError();
            }
        }
    }
}
