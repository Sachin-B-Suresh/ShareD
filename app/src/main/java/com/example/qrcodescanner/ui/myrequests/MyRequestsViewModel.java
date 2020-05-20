package com.example.qrcodescanner.ui.myrequests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyRequestsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyRequestsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is MyPosts fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}