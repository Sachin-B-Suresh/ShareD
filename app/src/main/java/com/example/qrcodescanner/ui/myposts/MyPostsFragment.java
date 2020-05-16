package com.example.qrcodescanner.ui.myposts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.qrcodescanner.R;

public class MyPostsFragment extends Fragment {

    private MyPostsViewModel myPostsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myPostsViewModel =
                ViewModelProviders.of(this).get(MyPostsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_posts, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        myPostsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
