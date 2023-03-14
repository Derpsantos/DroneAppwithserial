package com.drone.app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.drone.app.utility.LoadingHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {
     ImageView backArrow;
     EditText edEmail;
     Button buttonGetResetLink;
     LoadingHelper loadingHelper;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        loadingHelper=new LoadingHelper(this);
        initAuth();
        initViews();
    }

    private void initAuth() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
    }

    private void initViews() {
        backArrow=findViewById(R.id.backArrow);
        edEmail=findViewById(R.id.edEmail);
        buttonGetResetLink=findViewById(R.id.buttonGetResetLink);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonGetResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }

    private void resetPassword() {
        String email=edEmail.getText().toString();

        if(email.equals("")){
            Toast.makeText(this, "Email required", Toast.LENGTH_SHORT).show();
        }else {
            Dialog dialogProgress=loadingHelper.openNetLoaderDialog();
          auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                  Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.resetLink), Toast.LENGTH_LONG).show();
                   dialogProgress.dismiss();
                   finish();
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                dialogProgress.dismiss();
                  Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
              }
          });
        }
    }
}