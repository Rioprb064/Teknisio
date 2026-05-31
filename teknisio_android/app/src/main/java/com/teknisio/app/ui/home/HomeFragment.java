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
        rvNews.setAdapter(newsAdapter);
    }
}
