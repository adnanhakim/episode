package com.devteam.episode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private RelativeLayout relativeSettingsEditDp;
    private boolean isExpanded = false, isImageSelected = false;
    private TextView tvEditDp, tvNightMode;
    private ImageView ivSettingsDp;
    private Button btnSave;
    private Switch switchNightMode;

    private RequestOptions requestOptions;

    private static int P_REQ_CODE = 1;
    private static int REQUEST_CODE = 1;
    private Uri pickedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        init();

        Glide.with(SettingsActivity.this).load(ProfileFragment.user.getImageURL())
                .apply(requestOptions).into(ivSettingsDp);
        // To make the image view circular
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivSettingsDp.setClipToOutline(true);
        }

        tvEditDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    relativeSettingsEditDp.setVisibility(View.GONE);
                    isExpanded = false;
                } else {
                    relativeSettingsEditDp.setVisibility(View.VISIBLE);
                    isExpanded = true;
                }
            }
        });

        ivSettingsDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageSelected) {
                    saveImage();
                } else {
                    Toast.makeText(SettingsActivity.this, "No changes detected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchNightMode.setChecked(false);
        switchNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(SettingsActivity.this, "Night Mode coming soon!", Toast.LENGTH_SHORT).show();
                    switchNightMode.setChecked(false);
                }
            }
        });
    }

    private void init() {
        relativeSettingsEditDp = findViewById(R.id.relativeSettingsEditDp);
        tvEditDp = findViewById(R.id.tvSettingsEditDp);
        tvNightMode = findViewById(R.id.tvSettingsNightMode);
        ivSettingsDp = findViewById(R.id.ivSettingsDp);
        btnSave = findViewById(R.id.btnSettingsSave);
        switchNightMode = findViewById(R.id.switchSettingsNightMode);

        requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_screen)
                .error(R.drawable.loading_screen);
        relativeSettingsEditDp.setVisibility(View.GONE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please accept required permissions", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(SettingsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, P_REQ_CODE);
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: User has successfully picked an image");
            pickedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: Image uri: " + pickedImageUri);
            ivSettingsDp.setImageURI(pickedImageUri);
            isImageSelected = true;
        } else {
            Log.d(TAG, "onActivityResult: User has not picked an image");
            Toast.makeText(this, "Image not picked", Toast.LENGTH_SHORT).show();
            isImageSelected = false;
        }
    }

    private void saveImage() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String uid = firebaseAuth.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(uid);
        final StorageReference imageFilePath = storageReference.child(pickedImageUri.getLastPathSegment());
        Log.d(TAG, "saveImage: imageFilePath: " + imageFilePath);
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: Image uploaded successfully");
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: Uri: " + uri);
                        //saveImageUrl(uid, uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Exception:" + e.toString());
                Toast.makeText(SettingsActivity.this, "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageUrl(String uid, Uri uri) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);
        databaseReference.child("imageURL").setValue(uri).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Database updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Exception:" + e.toString());
                Toast.makeText(SettingsActivity.this, "Database not updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
