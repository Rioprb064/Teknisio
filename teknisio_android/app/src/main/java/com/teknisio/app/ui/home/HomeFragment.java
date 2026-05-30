package com.teknisio.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.teknisio.app.data.model.TeknisiModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories, rvTechnicians, rvNews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCategories  = view.findViewById(R.id.rvCategories);
        rvTechnicians = view.findViewById(R.id.rvTechnicians);
        rvNews        = view.findViewById(R.id.rvNews);

        setupTechnicians();
        setupNews();
        fetchCategories();

        return view;
    }

    // ── Technician Carousel (dummy data) ──────────────────────────────────
    private void setupTechnicians() {
        List<TeknisiModel> teknisiList = new ArrayList<>();

        teknisiList.add(new TeknisiModel(
                "Charlie Hugh",
                "https://i.pravatar.cc/300?img=12",
                4f, "± 2 Km", "50.000", "200.000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                Arrays.asList("TV", "Oven")
        ));

        teknisiList.add(new TeknisiModel(
                "Ahmed Rush",
                "https://i.pravatar.cc/300?img=11",
                4f, "± 2 Km", "50.000", "200.000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                Arrays.asList("AC", "Fridge")
        ));

        teknisiList.add(new TeknisiModel(
                "Ben Adams",
                "https://i.pravatar.cc/300?img=67",
                4f, "± 2 Km", "50.000", "200.000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                Arrays.asList("Washing Machine", "Rice Cooker")
        ));

        TechnicianCarouselAdapter adapter = new TechnicianCarouselAdapter(teknisiList);
        rvTechnicians.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTechnicians.setAdapter(adapter);
    }

    // ── News (dummy data) ──────────────────────────────────────────────────
    private void setupNews() {
        List<NewsAdapter.NewsItem> newsList = new ArrayList<>();
        newsList.add(new NewsAdapter.NewsItem(
                "Cara agar perabotan mu awet",
                "Mari kita pahami bagaimana cara agar ...",
                "2 April 2026"
        ));
        newsList.add(new NewsAdapter.NewsItem(
                "Waspada arus listrik dirumah mu",
                "Hindari tindakan ini kalau kamu ga mau...",
                "2 April 2026"
        ));

        NewsAdapter newsAdapter = new NewsAdapter(newsList);
        rvNews.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNews.setNestedScrollingEnabled(false);
        rvNews.setAdapter(newsAdapter);
    }

    // ── Kategori (from API) ────────────────────────────────────────────────
    private void fetchCategories() {
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvCategories.setNestedScrollingEnabled(false);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.getKategori().enqueue(new Callback<List<KategoriResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<KategoriResponse>> call,
                                   @NonNull Response<List<KategoriResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    KategoriAdapter adapter = new KategoriAdapter(response.body());
                    rvCategories.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Gagal memuat kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<KategoriResponse>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
