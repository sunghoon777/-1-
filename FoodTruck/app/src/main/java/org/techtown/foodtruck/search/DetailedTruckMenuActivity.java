package org.techtown.foodtruck.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

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

import org.techtown.foodtruck.DO.Review;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.DO.UserAccount;
import org.techtown.foodtruck.R;
import org.techtown.foodtruck.login.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailedTruckMenuActivity extends AppCompatActivity {
    
    //앱바에 표시될 트럭이미지
    private ImageView truckImage;
    //앱바에 표시될 트럭 텍스트
    private TextView truckName1;
    //탭레이아웃 뷰
    private TabLayout tabLayout;
    //메뉴 리뷰 프래그먼트
    private TruckMenuFragment menuFragment;
    private TruckReviewFragment reviewFragment;
    //fragment에게 전달해줄 bundle
    private Bundle bundle;
    //주문 버튼
    private FloatingActionButton orderButton;
    //리뷰 버튼
    private FloatingActionButton reviewButton;
    //현재 트럭 정보를 담은 트럭 객체
    private Truck truck;

    //다어어로그를 위한 뷰들
    private RatingBar ratingBar;
    private ImageView CameraImageView;
    private TextView cameraTextView;
    private ImageView reviewImage;
    private AlertDialog cameraDialog;
    private ImageView closeView;
    private ImageView commitView;
    private TextView comment;
    private TextView truckName2;

    //파이어베이스 인증
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //데이터베이스 참조 변수
    private DatabaseReference databaseReference;
    //이미지 저장소
    private StorageReference storageRef;
    private String userName;


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
        //트럭 상태가 1 닫혀있는 상태이면 볼수없음.
        if(truck.getVendor_status().equals("1")){
            Toast.makeText(getApplicationContext(),"오픈 시간이 아닙니다",Toast.LENGTH_SHORT).show();
            finish();
        }
        truckImage = findViewById(R.id.activity_detailed_truck_menu_truck_image);
        truckName1 = findViewById(R.id.activity_detailed_truck_menu_truck_name);
        //대표 이미지 , 푸드트럭명 설정
        Glide.with(this).load(truck.getImage()).into(truckImage);
        truckName1.setText(truck.getName());
        //탭레이아웃 설정
        tabLayout = findViewById(R.id.activity_detailed_truck_menu_tab);
        tabLayout.addTab(tabLayout.newTab().setText("메뉴"));
        tabLayout.addTab(tabLayout.newTab().setText("리뷰"));
        //fragment 객체 생성
        menuFragment = new TruckMenuFragment();
        reviewFragment = new TruckReviewFragment();
        //fragment에게 전달해줄 bundle 설정
        bundle = new Bundle();
        bundle.putSerializable("Truck",truck);
        menuFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_detailed_truck_menu_container,menuFragment).commit();
        //tab 리스너 설정
        setTabListener();
        //타이틀 이름 설정
        ActionBar ac = getSupportActionBar();
        ac.setTitle(truck.getName());
        //floating 버튼 리스너 설정
        orderButton = findViewById(R.id.activity_detailed_truck_menu_order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("order",MODE_PRIVATE);
                if(sharedPreferences.getString("order_list","empty").equals("empty")){
                    Toast.makeText(v.getContext(),"장바구니가 비었어요",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(v.getContext(), PaymentActivity.class);
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
                //리뷰 메뉴를 선택하면 리뷰 버튼은 보여지고 주문 버튼은 안보여지게 만든다.
                if(position == 0){
                    menuFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_detailed_truck_menu_container,menuFragment).commit();
                    reviewButton.getLayoutParams().height = 0;
                    reviewButton.getLayoutParams().width = 0;
                    CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.END | Gravity.BOTTOM;
                    params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                    params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                    orderButton.setLayoutParams(params);
                }
                //주문 버튼을 선택하면 주문 버튼은 보여지고 리뷰 버튼은 안보여지게 만든다.
                else if(position == 1){
                    reviewFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_detailed_truck_menu_container,reviewFragment).commit();
                    orderButton.getLayoutParams().height = 0;
                    orderButton.getLayoutParams().width = 0;
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
        reviewButton = findViewById(R.id.activity_detailed_truck_menu_review_button);
        Dialog dialog = new Dialog(this, R.style.fullscreen);
        View view = getLayoutInflater().inflate(R.layout.dialog_review,null);
        dialog.setContentView(view);
        ratingBar = dialog.findViewById(R.id.dialog_review_ratingbar);
        reviewImage = dialog.findViewById(R.id.dialog_review_review_image);
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
        CameraImageView = dialog.findViewById(R.id.dialog_review_camera_button);
        CameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePicture();
            }
        });
        closeView = dialog.findViewById(R.id.dialog_review_close_button);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();;
            }
        });
        truckName2 = dialog.findViewById(R.id.dialog_review_truck_name);
        truckName2.setText(truck.getName());
        comment = dialog.findViewById(R.id.dialog_review_comment);
        commitView = dialog.findViewById(R.id.dialog_review_commit_button);
        commitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                //파이어 베이스 이미지 저장소를  review.trucKId,userId.주문날짜 key로 해서 이미지를 저장한다.
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://foodtruck-309c3.appspot.com").child("Review").child(truck.getId()).child(user.getUid()).child(dateFormat.format(date));
                //이미지를 바이트값으로 변환하는 과정 확장자는 JPEG로
                reviewImage.setDrawingCacheEnabled(true);
                reviewImage.buildDrawingCache();

                try{
                    Bitmap bitmap = ((BitmapDrawable) reviewImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    dialog.dismiss();
                    UploadTask uploadTask = storageRef.putBytes(data);
                    //이미지 저장소에 업로드
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
                            //성공시
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Review review = new Review(user.getUid(),dateFormat.format(date),comment.getText().toString(),downloadUri.toString(), Float.toString(ratingBar.getRating()),userName,truck.getId());
                                databaseReference.child("Review").push().setValue(review);
                            }
                            //실패시
                            else {

                            }
                        }
                    });
                }catch (Exception e){
                    //이미지를 업로드 안할시에는
                    dialog.dismiss();
                    Review review = new Review(user.getUid(),dateFormat.format(date),comment.getText().toString(),"", Float.toString(ratingBar.getRating()),userName,truck.getId());
                    databaseReference.child("Review").push().setValue(review);
                }
            }
        });
    }

    //카메라 갤러리 찍기 메소드
    private void chooseProfilePicture(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedTruckMenuActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.dialog_camera,null);
        builder.setCancelable(false);
        builder.setView(dialogview);
        cameraDialog = builder.create();
        cameraDialog.show();
        cameraTextView = cameraDialog.findViewById(R.id.dialog_camera_cancel_button);
        cameraTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDialog.dismiss();
            }
        });
        ImageView galleryButton = dialogview.findViewById(R.id.dialog_camera_gallery_button);
        ImageView takePictureButton = dialogview.findViewById(R.id.dialog_camera_take_picture_button);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissions()){
                    takePictureFromCamera();
                }
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
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
            int cameraPermission = ActivityCompat.checkSelfPermission(DetailedTruckMenuActivity.this, Manifest.permission.CAMERA);
            if(cameraPermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(DetailedTruckMenuActivity.this,new String[]{Manifest.permission.CAMERA},20);
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