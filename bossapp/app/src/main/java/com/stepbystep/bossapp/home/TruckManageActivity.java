package com.stepbystep.bossapp.home;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.stepbystep.bossapp.R;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.stepbystep.bossapp.ViewPagerAdapter;
import com.stepbystep.bossapp.databinding.ActivityTruckManageBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class TruckManageActivity extends AppCompatActivity {
    private ActivityTruckManageBinding binding;
    private String saveCurrentDate, saveCurrentTime, iconRandomKey, downloadUrl;
    private String tName;
    private Uri imageUri;
    private static final int GALLERYPICK = 1;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    RatingBar ratingBar;
    TextView tvScore;

    public static int totalStars, oneStar, twoStars, threeStars, fourStars, fiveStars;
    public static float reviewTotalRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTruckManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar abar = getSupportActionBar();
        abar.hide();

        storageReference = FirebaseStorage.getInstance().getReference().child("StoreIcon");
        databaseReference = FirebaseDatabase.getInstance().getReference("BossApp");

        ImageButton btnBack = findViewById(R.id.btn_backToManageFrag);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.ivStoreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GALLERYPICK);
            }
        });

        tvScore = findViewById(R.id.tv_score);
        tvScore.setText("0점");
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tvScore.setText(rating + "점");
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, 0, 2);
        binding.vpManage.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.vpManage,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText("Tab" + (position + 1));
                        switch (position) {
                            case 0:
                                tab.setText("메뉴관리");
                                break;
                            default:
                                tab.setText("리뷰관리");
                                break;
                        }
                    }
                }).attach();
        databaseReference.child("StoreAccount").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null){
                            tName = snapshot.child("name").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERYPICK && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            binding.ivStoreIcon.setImageURI(imageUri);
            truckMenuInfo();
        }

    }

    private void setRecyclerView(ViewGroup rootView) {

    }


    private void truckMenuInfo() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat date = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = date.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:MM:SS a");
        saveCurrentTime = time.format(calendar.getTime());

        iconRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = storageReference.child(imageUri.getLastPathSegment() + iconRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TruckManageActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(TruckManageActivity.this, "image uploaded successfully", Toast.LENGTH_LONG).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            downloadUrl = task.getResult().toString();
                            Toast.makeText(TruckManageActivity.this, "done", Toast.LENGTH_LONG).show();
                            saveTruckInfoToDatabase();
                        }
                    }
                });
            }
        });

    }


    private void saveTruckInfoToDatabase() {
        HashMap<String, Object> truckMap = new HashMap<>();
        truckMap.put("image", downloadUrl);

        databaseReference.child("StoreAccount").child("truck").updateChildren(truckMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Glide.with(TruckManageActivity.this).load(downloadUrl).into(binding.ivStoreIcon);
                            finish();
                            Toast.makeText(TruckManageActivity.this, "Profile added successfully..", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(TruckManageActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
