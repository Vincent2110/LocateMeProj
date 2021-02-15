package com.project.locateme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {


    CircleImageView profileImageView;
    EditText inputUsername;
    EditText inputAdress;
    Button btnSave;
    Uri imageUri;
    ProgressDialog mLoadingBar;
    FirebaseAuth mAuth;
    DatabaseReference DataRef;
    StorageReference StorageRef;
    FirebaseUser mUser;

    public static final int IMAGE_PICKER_SELECT = 101;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA =1010 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        profileImageView = findViewById(R.id.profileImage);
        inputUsername = findViewById(R.id.inputUsername);
        inputAdress = findViewById(R.id.inputAddress);
        btnSave = findViewById(R.id.btnSave);
        mLoadingBar=new ProgressDialog(this);

        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        DataRef= FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRef= FirebaseStorage.getInstance().getReference().child("ProfileImages");

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(SetupActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ) {

                    ActivityCompat.requestPermissions(SetupActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.TITLE, "New Pic");
                    contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Front Camera Pic");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, IMAGE_PICKER_SELECT);
                }


            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveProfile();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER_SELECT) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SetupActivity.this.getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    private void SaveProfile() {
        final String username=inputUsername.getText().toString();
       final String address=inputAdress.getText().toString();


        if (username.isEmpty() || username.length()<3)
        {
            inputUsername.setError("Select Correct Username with min 4 letter");
        }
        else if(address.isEmpty() || address.length()<3)
        {
            inputUsername.setError("Select Correct Address with min 4 letter");
        }
        else if (imageUri==null)
        {
            Toast.makeText(this, "Please Select an Profile Image", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mLoadingBar.setTitle("Saving Profile");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            StorageRef.child(mUser.getUid()).putFile(imageUri).addOnSuccessListener(taskSnapshot -> StorageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    HashMap hashMap=new HashMap();
                    hashMap.put("username",username);
                    hashMap.put("address",address);
                    hashMap.put("profileImageUrl",uri.toString());

                    DataRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                        {
                            mLoadingBar.dismiss();
                            Intent intent=new Intent(SetupActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            mLoadingBar.dismiss();
                            Toast.makeText(SetupActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.Images.Media.TITLE, "New Pic");
                        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Front Camera Pic");
                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, IMAGE_PICKER_SELECT);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}