package org.techtown.foodtruck.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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

import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.Review;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.DO.UserAccount;
import org.techtown.foodtruck.MainActivity;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.login.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailedTruckMenu extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    TabLayout tabLayout;
    Truck_menu menu;
    Truck_review review;
    Bundle bundle;
    FloatingActionButton floatingActionButton;
    FloatingActionButton reviewButton;
    Truck truck;

    //dialog를 위한 뷰들
    RatingBar rb;
    ImageView CameraImageView;
    TextView cameraTextView;
    ImageView reviewImage;
    AlertDialog cameraDialog;
    ImageView closeView;
    ImageView commitView;
    TextView comment;
    TextView truckName;

    //파이어베이스 인증
    FirebaseAuth mAuth;
    FirebaseUser user;
    //데이터베이스 참조 변수
    DatabaseReference databaseReference;
    //파이어베이스 저장소
    FirebaseStorage storage;
    StorageReference storageRef;
    String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_truck_menu);
        //사용자 객체 생성
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //전 액티비티에서 intent를 받아온다
        Intent intent = getIntent();
        truck = (Truck) intent.getSerializableExtra("Truck");
        imageView = findViewById(R.id.detailed_truck_menu_imageview);
        textView = findViewById(R.id.detailed_truck_menu_textview);
        //대표 이미지 , 푸드트럭명 설정
        Glide.with(this).load(truck.getImage()).into(imageView);
        textView.setText(truck.getName());
        //탭레이아웃 설정
        tabLayout = findViewById(R.id.detailed_truck_menu_tab);
        tabLayout.addTab(tabLayout.newTab().setText("메뉴"));
        tabLayout.addTab(tabLayout.newTab().setText("리뷰"));
        //fragment 객체 생성
        menu = new Truck_menu();
        review = new Truck_review();
        //fragment에게 전달해줄 bundle 설정
        bundle = new Bundle();
        bundle.putSerializable("Truck",truck);
        menu.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.detailed_truck_menu_container,menu).commit();
        //tab 리스너 설정
        setTabListener();
        //타이틀 이름 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle(truck.getName());
        //floating 버튼 리스너 설정
        floatingActionButton = findViewById(R.id.fragment_truck_menu_floatButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("order",MODE_PRIVATE);
                if(sharedPreferences.getString("order_list","empty").equals("empty")){
                    Toast.makeText(v.getContext(),"장바구니가 비었어요",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(v.getContext(),Basket.class);
                    intent.putExtra("Truck",truck);
                    startActivity(intent);
                }
            }
        });
        databaseReference =  FirebaseDatabase.getInstance().getReference("FoodTruck");
        if(user != null){
            //유저 데이터 받아오기
            databaseReference.child("UserAccount").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    userName = userAccount.getName();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        setReviewButton();
    }

    //탭 리스너 설정 메소드
    private void setTabListener(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    menu.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.detailed_truck_menu_container,menu).commit();
                    reviewButton.getLayoutParams().height = 0;
                    reviewButton.getLayoutParams().width = 0;
                    CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.END | Gravity.BOTTOM;
                    params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                    params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                    floatingActionButton.setLayoutParams(params);
                }
                else if(position == 1){
                    review.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.detailed_truck_menu_container,review).commit();
                    floatingActionButton.getLayoutParams().height = 0;
                    floatingActionButton.getLayoutParams().width = 0;
                    CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.END | Gravity.BOTTOM;
                    params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                    params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                    reviewButton.setLayoutParams(params);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //리뷰 작성 로직
    private void setReviewButton(){
        reviewButton = findViewById(R.id.fragment_truck_menu_reviewButton);
        Dialog dialog = new Dialog(this, R.style.fullscreen);
        View view = getLayoutInflater().inflate(R.layout.review_dialog,null);
        dialog.setContentView(view);
        rb = dialog.findViewById(R.id.ratingbar);
        reviewImage = dialog.findViewById(R.id.review_image);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null){
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    dialog.show();
                }
            }
        });
        CameraImageView = dialog.findViewById(R.id.camera);
        CameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePicture();
            }
        });
        closeView = dialog.findViewById(R.id.iv_close);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();;
            }
        });
        truckName = dialog.findViewById(R.id.review_truckName);
        truckName.setText(truck.getName());
        comment = dialog.findViewById(R.id.review_comment);
        commitView = dialog.findViewById(R.id.review_commit);
        commitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                storage = FirebaseStorage.getInstance();
                storageRef = storage.getReferenceFromUrl("gs://foodtruck-309c3.appspot.com").child("Review").child(truck.getId()).child(user.getUid()).child(dateFormat.format(date));
                reviewImage.setDrawingCacheEnabled(true);
                reviewImage.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) reviewImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                dialog.dismiss();
                UploadTask uploadTask = storageRef.putBytes(data);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return storageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Review review = new Review(user.getUid(),dateFormat.format(date),comment.getText().toString(),downloadUri.toString(), Float.toString(rb.getRating()),userName);
                            databaseReference.child("Review").child(truck.getId()).push().setValue(review);
                            Log.d("이미지업로드","추가도 성공");

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        });
    }

    //카메라 갤러리 찍기 메소드
    private void chooseProfilePicture(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedTruckMenu.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.camera_dialog,null);
        builder.setCancelable(false);
        builder.setView(dialogview);
        cameraDialog = builder.create();
        cameraDialog.show();
        cameraTextView = cameraDialog.findViewById(R.id.camera_dialog_cancel);
        cameraTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDialog.dismiss();
            }
        });
        ImageView imageView1 = dialogview.findViewById(R.id.lytCameraPick);
        ImageView imageView2 = dialogview.findViewById(R.id.lytGalleryPick);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissions()){
                    takePictureFromCamera();
                }
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureFromGallery();
            }
        });
    }

    //갤러리에서 가지고 오기
    private void takePictureFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto,1);
    }

    //카메라로 찍기
    private void takePictureFromCamera(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePicture,2);
        }
    }

    //결과 받고 이미지뷰에 저장
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1 :
                if(resultCode == RESULT_OK){
                    Uri selectedImageUri = data.getData();
                    reviewImage.setImageURI(selectedImageUri);
                    cameraDialog.dismiss();
                }
                break;
            case 2:
                Bundle bundle = data.getExtras();
                Bitmap bitmapImage = (Bitmap) bundle.get("data");
                reviewImage.setImageBitmap(bitmapImage);
                cameraDialog.dismiss();
                break;
        }
    }

    //권한부여 코드
    private boolean checkAndRequestPermissions(){
        if(Build.VERSION.SDK_INT >= 23){
            int cameraPermission = ActivityCompat.checkSelfPermission(DetailedTruckMenu.this, Manifest.permission.CAMERA);
            if(cameraPermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(DetailedTruckMenu.this,new String[]{Manifest.permission.CAMERA},20);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            takePictureFromCamera();
        }
        else{
            Toast.makeText(getApplicationContext(),"권한을 부여해주세요",Toast.LENGTH_SHORT).show();
        }
    }




}