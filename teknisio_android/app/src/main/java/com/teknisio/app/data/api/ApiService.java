package com.teknisio.app.data.api;

import com.teknisio.app.data.model.ApiResponse;
import com.teknisio.app.data.model.AuthResponse;
import com.teknisio.app.data.model.AuthUserResponse;
import com.teknisio.app.data.model.CancelServiceRequestRequest;
import com.teknisio.app.data.model.CreateServiceRequestRequest;
import com.teknisio.app.data.model.CustomerTechnicianResponse;
import com.teknisio.app.data.model.DeviceCategoryResponse;
import com.teknisio.app.data.model.LoginRequest;
import com.teknisio.app.data.model.RegisterCustomerRequest;
import com.teknisio.app.data.model.RegisterTeknisiRequest;
import com.teknisio.app.data.model.ServiceRequestResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // =========================================================
    // AUTH
    // =========================================================

    @POST(ApiContract.Auth.LOGIN)
    Call<ApiResponse<AuthResponse>> login(
        @Body LoginRequest request
    );

    @POST(ApiContract.Auth.REGISTER_CUSTOMER)
    Call<ApiResponse<AuthResponse>> registerCustomer(
        @Body RegisterCustomerRequest request
    );

    @POST(ApiContract.Auth.REGISTER_TECHNICIAN)
    Call<ApiResponse<AuthResponse>> registerTechnician(
        @Body RegisterTeknisiRequest request
    );

    @GET(ApiContract.Auth.PROFILE)
    Call<ApiResponse<AuthUserResponse>> getProfile(
        @Header("Authorization") String authorization
    );


    // =========================================================
    // DEVICE CATEGORIES — PUBLIC
    // =========================================================

    @GET(ApiContract.DeviceCategories.LIST)
    Call<ApiResponse<List<DeviceCategoryResponse>>> getDeviceCategories();

    @GET(ApiContract.DeviceCategories.DETAIL)
    Call<ApiResponse<DeviceCategoryResponse>> getDeviceCategoryDetail(
        @Path("deviceCategoryId") String deviceCategoryId
    );


    // =========================================================
    // CUSTOMER — TECHNICIAN DISCOVERY
    // Authorization: Bearer token
    // Role: CUSTOMER
    // =========================================================

    @GET(ApiContract.CustomerTechnicians.LIST)
    Call<ApiResponse<List<CustomerTechnicianResponse>>> getCustomerTechnicians(
        @Header("Authorization") String authorization,
        @Query(ApiContract.CustomerTechnicians.QUERY_DEVICE_CATEGORY_ID) String deviceCategoryId,
        @Query(ApiContract.CustomerTechnicians.QUERY_AVAILABILITY_STATUS) String availabilityStatus,
        @Query(ApiContract.CustomerTechnicians.QUERY_SORT) String sort
    );

    @GET(ApiContract.CustomerTechnicians.DETAIL)
    Call<ApiResponse<CustomerTechnicianResponse>> getCustomerTechnicianDetail(
        @Header("Authorization") String authorization,
        @Path("technicianProfileId") String technicianProfileId
    );


    // =========================================================
    // CUSTOMER — SERVICE REQUESTS
    // Authorization: Bearer token
    // Role: CUSTOMER
    // =========================================================

    @POST(ApiContract.CustomerServiceRequests.CREATE)
    Call<ApiResponse<ServiceRequestResponse>> createServiceRequest(
        @Header("Authorization") String authorization,
        @Body CreateServiceRequestRequest request
    );

    @GET(ApiContract.CustomerServiceRequests.LIST)
    Call<ApiResponse<List<ServiceRequestResponse>>> getCustomerServiceRequests(
        @Header("Authorization") String authorization,
        @Query(ApiContract.CustomerServiceRequests.QUERY_STATUS) String status
    );

    @GET(ApiContract.CustomerServiceRequests.DETAIL)
    Call<ApiResponse<ServiceRequestResponse>> getCustomerServiceRequestDetail(
        @Header("Authorization") String authorization,
        @Path("serviceRequestId") String serviceRequestId
    );

    @PATCH(ApiContract.CustomerServiceRequests.CANCEL)
    Call<ApiResponse<ServiceRequestResponse>> cancelCustomerServiceRequest(
        @Header("Authorization") String authorization,
        @Path("serviceRequestId") String serviceRequestId,
        @Body CancelServiceRequestRequest request
    );
}
