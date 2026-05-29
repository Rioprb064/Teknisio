package com.teknisio.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.teknisio.app.R;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories;
    private RecyclerView rvTechnicians;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        rvCategories = view.findViewById(R.id.rvCategories);
        rvTechnicians = view.findViewById(R.id.rvTechnicians);

        setupRecyclerViews();

        // TODO: Panggil ApiService.getKategori() menggunakan Retrofit lalu set ke Adapter

        return view;
    }

    private void setupRecyclerViews() {
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
        // rvCategories.setAdapter(new KategoriAdapter(kategoriList));

        rvTechnicians.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // rvTechnicians.setAdapter(new TechnicianAdapter(technicianList));
    }
}
