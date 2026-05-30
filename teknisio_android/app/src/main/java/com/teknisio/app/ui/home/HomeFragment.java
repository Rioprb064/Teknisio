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
import com.teknisio.app.data.api.ApiClient;
import com.teknisio.app.data.api.ApiService;
import com.teknisio.app.data.model.KategoriResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;

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
        fetchCategories();

        return view;
    }

    private void fetchCategories() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.getKategori().enqueue(new Callback<List<KategoriResponse>>() {
            @Override
            public void onResponse(Call<List<KategoriResponse>> call, Response<List<KategoriResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    KategoriAdapter adapter = new KategoriAdapter(response.body());
                    rvCategories.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Gagal memuat kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<KategoriResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerViews() {
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));

        rvTechnicians.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }
}
