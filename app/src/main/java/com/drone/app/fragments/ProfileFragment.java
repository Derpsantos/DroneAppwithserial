package com.drone.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.drone.app.LoginActivity;
import com.drone.app.R;
import com.drone.app.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    View view;
     FirebaseAuth auth;
     FirebaseUser user;
     DatabaseReference reference;
     ImageView imageViewUserImage;
     TextView textViewUserName,textViewEmail
             ,textViewPhoneNumber;

     Button buttonLogout;

    public ProfileFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_profile, container, false);
        initDB();
        initView();
        getUserData();
        return view;
    }

    private void getUserData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists()){
                  UserModel userModel=snapshot.getValue(UserModel.class);
                  textViewUserName.setText(userModel.getUserName());
                  textViewEmail.setText(userModel.getEmail());
                  textViewPhoneNumber.setText(userModel.getPhoneNumber());
                  downloadImage(userModel.getImgURL());


              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void downloadImage(String imgURL) {

        if(!imgURL.equals("")){
            Picasso.get().load(imgURL).placeholder(getContext().getDrawable(R.drawable.person_place_holder))
                    .into(imageViewUserImage);

        }


    }
    private void initView() {
        imageViewUserImage=view.findViewById(R.id.imageViewUserImage);
        textViewUserName=view.findViewById(R.id.textViewUserName);
        textViewEmail=view.findViewById(R.id.textViewEmail);
        textViewPhoneNumber=view.findViewById(R.id.textViewPhoneNumber);
        buttonLogout=view.findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               logout();
            }
        });



    }

    private void logout() {
            new AlertDialog.Builder(getContext())
                    .setMessage("Are you sure to logout from this application?")
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(user!=null){
                                auth.signOut();
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }

                        }
                    })
                    .show();
    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(user.getUid())
                .child("profileData");

    }
}