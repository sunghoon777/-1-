package com.stepbystep.bossapp.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stepbystep.bossapp.databinding.FragmentTruckReviewBinding;

public class TruckReviewFragment extends Fragment {
    private View view;
    private FragmentTruckReviewBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTruckReviewBinding.inflate(inflater, container, false);
        view = binding.getRoot();



        return view;
    }


}
