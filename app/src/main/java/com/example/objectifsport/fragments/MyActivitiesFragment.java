package com.example.objectifsport.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.objectifsport.R;
import com.example.objectifsport.Services.DataManager;
import com.example.objectifsport.adapters.SportAdapter;

public class MyActivitiesFragment extends Fragment {

    private DataManager dataManager = DataManager.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static MySportsFragment newInstance() {
        MySportsFragment fragment = new MySportsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_sports, container, false);
        SportAdapter sportAdapter = new SportAdapter(view.getContext(), dataManager.getSports());
        ListView listView = view.findViewById(R.id.sports_list);
        listView.setAdapter(sportAdapter);

        return view;
    }


}
