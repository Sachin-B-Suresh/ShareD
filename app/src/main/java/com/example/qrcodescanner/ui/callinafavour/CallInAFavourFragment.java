package com.example.qrcodescanner.ui.callinafavour;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrcodescanner.R;

public class CallInAFavourFragment extends Fragment {

    private CallInAFavourViewModel mViewModel;

    public static CallInAFavourFragment newInstance() {
        return new CallInAFavourFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call_in_a_favour, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CallInAFavourViewModel.class);
        // TODO: Use the ViewModel
    }

}
