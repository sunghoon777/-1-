package com.stepbystep.bossapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.stepbystep.bossapp.R;
import com.stepbystep.bossapp.databinding.ActivityFullPicBinding;


public class FullPicActivity extends AppCompatActivity {

    private ImageView mContentView;
    private ActivityFullPicBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFullPicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent get = getIntent();
        String img = get.getStringExtra("url");
        mContentView = findViewById(R.id.fullscreen_img);
        Picasso.get().load(img).into(mContentView);

        ActionBar abar = getSupportActionBar();
        abar.hide();
    }


}