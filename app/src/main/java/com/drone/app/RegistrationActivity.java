package com.drone.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.drone.app.models.UserModel;
import com.drone.app.utility.LoadingHelper;
import com.drone.app.utility.StoragePermission;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {
      ImageView imageViewProfileImage;
      EditText editTextFullName,editTextEmail,editTextPhoneNumber
              ,editTextPassword,editTextConfirmPassword;

      Button buttonSignUp;
      LinearLayout linearLayoutNoAccount;

      DatabaseReference reference;
      FirebaseAuth auth;
      FirebaseUser user;
      Uri imageUri=null;
      Dialog dialogLoading;
      LoadingHelper loadingHelper;
      


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loadingHelper=new LoadingHelper(this);
        initBD();
        initViews();


    }

    private void initBD() {
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users");
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

    }

    private void initViews() {
        imageViewProfileImage=findViewById(R.id.imageViewProfileImage);
        editTextFullName=findViewById(R.id.editTextFullName);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        editTextConfirmPassword=findViewById(R.id.editTextConfirmPassword);
        buttonSignUp=findViewById(R.id.buttonSignUp);
        editTextPhoneNumber=findViewById(R.id.editTextPhoneNumber);

        linearLayoutNoAccount=findViewById(R.id.linearLayoutNoAccount);

        linearLayoutNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageViewProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result= StoragePermission.isAllowedToReadStorage(RegistrationActivity.this);
                if(result){
                    openStorageIntent();
                }
            }
        });

      buttonSignUp.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              UserModel us=new UserModel();
              us.setEmail(editTextEmail.getText().toString());
              us.setPhoneNumber(editTextPhoneNumber.getText().toString());
              us.setUserName(editTextFullName.getText().toString());
              us.setImgURL("");

              if(us.getEmail().equals("")){
                  Toast.makeText(RegistrationActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
              }else if(us.getUserName().equals("")){
                  Toast.makeText(RegistrationActivity.this, "Name required", Toast.LENGTH_SHORT).show();
              }else if(editTextPassword.getText().toString().equals("")){
                  Toast.makeText(RegistrationActivity.this, "Password Required", Toast.LENGTH_SHORT).show();
              }else if(!editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())){
                  Toast.makeText(RegistrationActivity.this, "Invalid confirm password", Toast.LENGTH_SHORT).show();
              }else{
                  createUserProfile(us);
              }


          }
      });


    }

    private void createUserProfile(UserModel us) {
       dialogLoading=loadingHelper.openNetLoaderDialog();
       
       auth.createUserWithEmailAndPassword(us.getEmail(),editTextPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
           @Override
           public void onSuccess(AuthResult authResult) {
               Toast.makeText(RegistrationActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
               if(imageUri!=null){
                   uploadImage(us);
               }else {
                   uploadUserData(us);
               }
           
           
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();     
               dialogLoading.dismiss();
           }
       });
       
       
       
        
        
        
    }

    private void uploadUserData(UserModel us) {
        user=auth.getCurrentUser();
        reference.child(user.getUid()).child("profileData")
                .setValue(us).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegistrationActivity.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                        dialogLoading.dismiss();
                        startNewTaskActivity();
                        
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogLoading.dismiss();
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        
        
        
    }

    private void startNewTaskActivity() {
        Intent intent=new Intent(RegistrationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void uploadImage(UserModel us) {
        if(imageUri!=null){
            String  imageID="profileImages/" + UUID.randomUUID().toString();
            StorageReference reference1= FirebaseStorage.getInstance().getReference().child(imageID);
            reference1.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(RegistrationActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    Task<Uri> uri2=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uri2.isComplete());
                    Uri uri3=uri2.getResult();
                    us.setImgURL(uri3.toString());
                    uploadUserData(us);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegistrationActivity.this, "can't upload image", Toast.LENGTH_SHORT).show();

                    us.setImgURL("");
                    uploadUserData(us);
                }
            });
        }else {
            us.setImgURL("");
            uploadUserData(us);
        }
        
        
        
    }


    private void openStorageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction( Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),1);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageViewProfileImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Can't get image ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}