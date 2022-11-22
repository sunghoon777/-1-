package com.stepbystep.bossapp.home;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.stepbystep.bossapp.databinding.ActivityEditTruckInfoBinding;

public class EditTruckInfoActivity extends AppCompatActivity {
    private ActivityEditTruckInfoBinding binding;

    private static final int EDIT_NONE = 11111;
    private static final int EDIT_ONLY = 22222;
    private static final int EDIT_BOTH = 33333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTruckInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar abar = getSupportActionBar();
        abar.hide();


        initClickListener();
    }

    private void initClickListener() {
        binding.btnBackToManageFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnSaveStoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
