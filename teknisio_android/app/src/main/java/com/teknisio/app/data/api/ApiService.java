package com.teknisio.app.data.api;

import com.teknisio.app.data.model.KategoriResponse;
import com.teknisio.app.data.model.PermintaanResponse;
import com.teknisio.app.data.model.CreatePermintaanRequest;

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
}
