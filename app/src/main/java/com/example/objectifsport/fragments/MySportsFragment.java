package com.example.objectifsport.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.objectifsport.R;
import com.example.objectifsport.Services.DataManager;
import com.example.objectifsport.adapters.SportAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MySportsFragment extends Fragment {

    private DataManager dataManager;
    private FloatingActionButton addSportButton;

    public static MySportsFragment newInstance() {
        MySportsFragment fragment = new MySportsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = DataManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_sports_fragment, container, false);
        SportAdapter sportAdapter = new SportAdapter(view.getContext(), dataManager.getSports());
        ListView listView = view.findViewById(R.id.sports_list);
        listView.setAdapter(sportAdapter);

        addSportButton = view.findViewById(R.id.add_sport);
        addSportButton.setOnClickListener(v -> {
            FragmentManager fm = getFragmentManager();
            AddSportDialogFragment addSportDialogFragment = AddSportDialogFragment.newInstance("Add sport");
            addSportDialogFragment.show(fm, "fragment_add_sport");
        });

        return view;
    }

}