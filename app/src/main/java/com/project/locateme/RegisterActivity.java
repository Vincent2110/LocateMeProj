package com.project.locateme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


    //Veribale Decleration
    EditText inputEmail;
    EditText inputPassword;
    EditText inputConfrimPassword;
    Button btnRegister;
    TextView alreadyHaveAccount;
    ProgressDialog progressBar;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //init varibale
        progressBar = new ProgressDialog(this);
        inputEmail = findViewById(R.id.inputEMail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfrimPassword = findViewById(R.id.inputConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        //click on already have account to open login page
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        //click on register button to perform registration
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String confrimPassword = inputConfrimPassword.getText().toString();

                if (email.isEmpty() || !email.contains("@")) {
                    inputEmail.setError("Enter Correct format Email");
                    inputEmail.requestFocus();
                } else if (password.isEmpty() || password.length() < 6) {
                    inputPassword.setError("Enter min 7 latter password");
                } else if (!confrimPassword.equals(password)) {
                    inputConfrimPassword.setError("Pasword not match both field");
                } else {
                    //if user enter valid format of email and password then perform registration
                    progressBar.setMessage("Registration...");
                    progressBar.setCanceledOnTouchOutside(false);
                    progressBar.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.dismiss();

                                //after successfull registration open Setup activity
                                Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                progressBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });


    }
}