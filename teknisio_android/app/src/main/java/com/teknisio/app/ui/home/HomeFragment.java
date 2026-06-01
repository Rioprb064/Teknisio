package com.teknisio.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teknisio.app.R;
import com.teknisio.app.data.api.ApiClient;
import com.teknisio.app.data.api.ApiContract;
import com.teknisio.app.data.api.ApiService;
import com.teknisio.app.data.model.ApiResponse;
import com.teknisio.app.data.model.CustomerTechnicianResponse;
import com.teknisio.app.data.model.DeviceCategoryResponse;
import com.teknisio.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView rvCategories;
    private RecyclerView rvTechnicians;
    private RecyclerView rvNews;
    private TextView tvLocation;

    private ApiService apiService;
    private SessionManager sessionManager;

    private String selectedDeviceCategoryId;
    private String selectedDeviceCategoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCategories = view.findViewById(R.id.rvCategories);
        rvTechnicians = view.findViewById(R.id.rvTechnicians);
        rvNews = view.findViewById(R.id.rvNews);
        tvLocation = view.findViewById(R.id.tvLocation);

        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        sessionManager = new SessionManager(requireContext());

        bindUserLocation();
        setupRecyclerViews();
        setupNews();

        showEmptyTechnicians();
        fetchCategories();

        return view;
    }

    private void bindUserLocation() {
        String address = sessionManager.getAddress();

        if (address != null && !address.trim().isEmpty()) {
            tvLocation.setText(address);
        }
        else {
            tvLocation.setText("Alamat belum tersedia");
        }
    }

    private void setupRecyclerViews() {
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvCategories.setNestedScrollingEnabled(false);

        rvTechnicians.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        ));
        rvTechnicians.setNestedScrollingEnabled(false);

        rvNews.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNews.setNestedScrollingEnabled(false);
    }

    private void fetchCategories() {
        apiService.getDeviceCategories().enqueue(new Callback<ApiResponse<List<DeviceCategoryResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<DeviceCategoryResponse>>> call, @NonNull Response<ApiResponse<List<DeviceCategoryResponse>>> response) {
                if (!isAdded()) {
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Gagal memuat kategori.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponse<List<DeviceCategoryResponse>> apiResponse = response.body();

                if (!apiResponse.isSuccess() || apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
                    Toast.makeText(requireContext(), "Kategori belum tersedia.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<DeviceCategoryResponse> categories = apiResponse.getData();

                KategoriAdapter adapter = new KategoriAdapter(categories, category -> {
                    selectedDeviceCategoryId = category.getDeviceCategoryId();
                    selectedDeviceCategoryName = category.getName();

                    fetchTechniciansByCategory(
                            selectedDeviceCategoryId,
                            selectedDeviceCategoryName
                    );
                });

                rvCategories.setAdapter(adapter);

                DeviceCategoryResponse firstCategory = categories.get(0);
                selectedDeviceCategoryId = firstCategory.getDeviceCategoryId();
                selectedDeviceCategoryName = firstCategory.getName();

                fetchTechniciansByCategory(
                        selectedDeviceCategoryId,
                        selectedDeviceCategoryName
                );
            }

            @Override
            public void onFailure(
                    @NonNull Call<ApiResponse<List<DeviceCategoryResponse>>> call,
                    @NonNull Throwable t
            ) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(requireContext(), "Gagal terhubung ke server kategori.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTechniciansByCategory(String deviceCategoryId, String deviceCategoryName) {
        if (deviceCategoryId == null || deviceCategoryId.trim().isEmpty()) {
            showEmptyTechnicians();
            return;
        }

        String authorization = sessionManager.getAuthorizationHeader();

        if (authorization == null || authorization.trim().isEmpty()) {
            showEmptyTechnicians();
            Toast.makeText(requireContext(), "Session login tidak valid. Silakan login ulang.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getCustomerTechnicians(
                authorization,
                deviceCategoryId,
                null,
                ApiContract.CustomerTechnicianSort.RATING
        ).enqueue(new Callback<ApiResponse<List<CustomerTechnicianResponse>>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<List<CustomerTechnicianResponse>>> call,
                    @NonNull Response<ApiResponse<List<CustomerTechnicianResponse>>> response
            ) {
                if (!isAdded()) {
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    showEmptyTechnicians();
                    Toast.makeText(requireContext(), "Gagal memuat teknisi.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiResponse<List<CustomerTechnicianResponse>> apiResponse = response.body();

                if (!apiResponse.isSuccess() || apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
                    showEmptyTechnicians();
                    return;
                }

                TechnicianCarouselAdapter adapter = new TechnicianCarouselAdapter(
                        apiResponse.getData(),
                        deviceCategoryId,
                        deviceCategoryName
                );

                rvTechnicians.setAdapter(adapter);
            }

            @Override
            public void onFailure(
                    @NonNull Call<ApiResponse<List<CustomerTechnicianResponse>>> call,
                    @NonNull Throwable t
            ) {
                if (!isAdded()) {
                    return;
                }

                showEmptyTechnicians();
                Toast.makeText(requireContext(), "Gagal terhubung ke server teknisi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyTechnicians() {
        TechnicianCarouselAdapter adapter = new TechnicianCarouselAdapter(
                new ArrayList<>(),
                selectedDeviceCategoryId,
                selectedDeviceCategoryName
        );

        rvTechnicians.setAdapter(adapter);
    }

    private void setupNews() {
        List<NewsAdapter.NewsItem> newsList = new ArrayList<>();

        newsList.add(new NewsAdapter.NewsItem(
                "Cara Merawat Kulkas Agar Tetap Dingin dan Awet",
                "Kulkas yang dirawat dengan baik tidak hanya menjaga kesegaran makanan, tapi juga hemat listrik...",
                "15 Mei 2026",
                "https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?q=80&w=600&auto=format&fit=crop"
        ));

        newsList.add(new NewsAdapter.NewsItem(
                "Waspada Korsleting Listrik di Rumah Anda!",
                "Ketahui tanda-tanda awal korsleting pada peralatan elektronik untuk mencegah kebakaran...",
                "10 Mei 2026",
                "https://images.unsplash.com/photo-1621905252507-b35492cc74b4?q=80&w=600&auto=format&fit=crop"
        ));

        newsList.add(new NewsAdapter.NewsItem(
                "Tips Memilih AC yang Tepat untuk Ruangan",
                "Bingung memilih AC? Perhatikan ukuran PK dan luas ruangan agar tagihan listrik tidak membengkak...",
                "5 Mei 2026",
                "data:image/jpeg;base64,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"
        ));

        NewsAdapter newsAdapter = new NewsAdapter(newsList, item -> {
            android.content.Intent intent = new android.content.Intent(requireContext(), NewsDetailActivity.class);
            intent.putExtra("EXTRA_TITLE", item.judul);
            intent.putExtra("EXTRA_DESC", item.deskripsi);
            intent.putExtra("EXTRA_DATE", item.tanggal);
            intent.putExtra("EXTRA_IMAGE_URL", item.imageUrl);
            startActivity(intent);
        });
        rvNews.setAdapter(newsAdapter);
    }
}
