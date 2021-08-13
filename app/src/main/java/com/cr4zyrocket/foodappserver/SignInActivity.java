package com.cr4zyrocket.foodappserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cr4zyrocket.foodappserver.Common.Common;
import com.cr4zyrocket.foodappserver.Model.Manager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    MaterialEditText edtPhone,edtPassword;
    Button btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign in");

        edtPhone=findViewById(R.id.edtPhone);
        edtPassword=findViewById(R.id.edtPassword);
        btnSignIn=findViewById(R.id.btnSignIn);

        //Init Firebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("Manager");
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())){
                    final ProgressDialog dialog=new ProgressDialog(SignInActivity.this);
                    dialog.setMessage("Please waiting...");
                    dialog.show();
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child(Objects.requireNonNull(edtPhone.getText()).toString()).exists()){
                                //Get User Information
                                dialog.dismiss();
                                Manager manager=snapshot.child(edtPhone.getText().toString()).getValue(Manager.class);
                                assert manager != null;
                                manager.setPhone(edtPhone.getText().toString());
                                if (manager.getPassword().equals(Objects.requireNonNull(edtPassword.getText()).toString())){
                                    Intent homeIntent=new Intent(SignInActivity.this,HomeActivity.class);
                                    Common.currentManager=manager;
                                    startActivity(homeIntent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
                                    table_user.removeEventListener(this);
                                }else{
                                    Toast.makeText(SignInActivity.this, "Wrong password !", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                dialog.dismiss();
                                Toast.makeText(SignInActivity.this, "Manager not exist in Database", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    Toast.makeText(SignInActivity.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}