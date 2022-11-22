package com.stepbystep.bossapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.stepbystep.bossapp.R;
import com.stepbystep.bossapp.databinding.FragmentMenuManageBinding;

public class MenuManageFragment extends Fragment {
    private View view;
    private FragmentMenuManageBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_menu_manage, container, false);
        binding = FragmentMenuManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        return view;
    }
}
