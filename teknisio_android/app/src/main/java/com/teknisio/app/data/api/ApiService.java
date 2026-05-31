package com.teknisio.app.data.api;

import com.teknisio.app.data.model.ApiResponse;
import com.teknisio.app.data.model.AuthResponse;
import com.teknisio.app.data.model.CreatePermintaanRequest;
import com.teknisio.app.data.model.KategoriResponse;
import com.teknisio.app.data.model.LoginRequest;
import com.teknisio.app.data.model.LoginResponse;
import com.teknisio.app.data.model.PermintaanResponse;
import com.teknisio.app.data.model.RegisterCustomerRequest;
import com.teknisio.app.data.model.RegisterResponse;
import com.teknisio.app.data.model.RegisterTeknisiRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    /*
     * =========================================================
     * OFFICIAL BACKEND API CONTRACT
     * =========================================================
     * Endpoint resmi backend memakai English endpoint dan
     * response wrapper ApiResponse<T>.
     */

    @POST(ApiContract.Auth.LOGIN)
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST(ApiContract.Auth.REGISTER_CUSTOMER)
    Call<ApiResponse<AuthResponse>> registerCustomerOfficial(@Body RegisterCustomerRequest request);

    @POST(ApiContract.Auth.REGISTER_TECHNICIAN)
    Call<ApiResponse<AuthResponse>> registerTechnicianOfficial(@Body RegisterTeknisiRequest request);


    /*
     * =========================================================
     * LEGACY METHODS
     * =========================================================
     * Jangan dipakai untuk flow baru.
     * Sementara dipertahankan agar UI lama belum langsung rusak.
     */

    @GET("/api/kategori")
    Call<List<KategoriResponse>> getKategori();

    @POST("/api/permintaan")
    Call<PermintaanResponse> createPermintaan(@Body CreatePermintaanRequest request);

    @POST("/api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("/api/auth/register/customer")
    Call<RegisterResponse> registerCustomer(@Body RegisterCustomerRequest request);

    @POST("/api/auth/register/teknisi")
    Call<RegisterResponse> registerTeknisi(@Body RegisterTeknisiRequest request);
}
