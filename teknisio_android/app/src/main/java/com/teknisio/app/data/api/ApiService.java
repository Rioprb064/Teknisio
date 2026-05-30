package com.teknisio.app.data.api;

import com.teknisio.app.data.model.KategoriResponse;
import com.teknisio.app.data.model.PermintaanResponse;
import com.teknisio.app.data.model.CreatePermintaanRequest;
import com.teknisio.app.data.model.LoginRequest;
import com.teknisio.app.data.model.LoginResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // Mengambil daftar kategori elektronik
    @GET("/api/kategori")
    Call<List<KategoriResponse>> getKategori();

    // Mengirim pesanan (permintaan layanan)
    @POST("/api/permintaan")
    Call<PermintaanResponse> createPermintaan(@Body CreatePermintaanRequest request);

    // Otentikasi Login
    @POST("/api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);
}
