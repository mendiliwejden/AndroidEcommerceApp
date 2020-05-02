package com.example.wejden.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    // montre la progression d'une tâche
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);


        //si on clique sur le button create account button il appelle la fonction create account
            CreateAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    CreateAccount();
                }
            });
        }



        private void CreateAccount()
        {
            String name = InputName.getText().toString();
            String phone = InputPhoneNumber.getText().toString();
            String password = InputPassword.getText().toString();

            if (TextUtils.isEmpty(name))
            {
                Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(phone))
            {
                Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(password))
            {
                Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
            }
            else
            {
                loadingBar.setTitle("Create Account");
                loadingBar.setMessage("Please wait, while we are checking the credentials.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                ValidatephoneNumber(name, phone, password);
            }
        }

    private void ValidatephoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //if phone number not exist in the database then we will create a new account
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    //creation d'une collection en Java qui associe une clé à une valeur
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    //update the children with userdatamap we created
                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                //else we will tell user the phone number is exist
                else
                {
                    Toast.makeText(RegisterActivity.this, "This " + phone + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    }

