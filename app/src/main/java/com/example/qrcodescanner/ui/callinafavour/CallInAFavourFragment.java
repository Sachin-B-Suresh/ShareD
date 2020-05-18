package com.example.qrcodescanner.ui.callinafavour;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.qrcodescanner.DatabaseHelper;
import com.example.qrcodescanner.R;
import com.example.qrcodescanner.ui.home.HomeFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CallInAFavourFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private CallInAFavourViewModel mViewModel;
    private String spinnerItem;
    private String descriptionItem;
    private String[] userDetails;
    private EditText editTextDescription;
    private Button submitButton;
    DatabaseHelper databaseHelper;
    Fragment fragment = null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference requestsRef = database.getReference("Requests");
    DatabaseReference newRequestRef = requestsRef.push();


    public static CallInAFavourFragment newInstance() {
        return new CallInAFavourFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_call_in_a_favour, container, false);
        databaseHelper = new DatabaseHelper(getActivity());
        userDetails = databaseHelper.fetchLocalInstance();
        submitButton = (Button) v.findViewById(R.id.submit);
        editTextDescription = (EditText) v.findViewById(R.id.description1);
        String [] values =
                {"Type C Charger","MacBook 2017+ Charger","Broadband","Others"};
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        spinner.setPrompt("Select Option!");
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do something
                descriptionItem=editTextDescription.getText().toString();
                Log.d("Edittext",descriptionItem);
                Log.d("Spinner item", spinnerItem);
                HashMap<String, Object> userRequest = new HashMap<>();
                userRequest.put("Email", userDetails[0]);
                userRequest.put("Name", userDetails[2]);
                userRequest.put("RequestedItem",spinnerItem);
                userRequest.put("Description",descriptionItem);
                newRequestRef.setValue(userRequest);
                fragment = new HomeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CallInAFavourViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        spinnerItem=parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
