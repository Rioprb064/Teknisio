package com.teknisio.app.data.repository;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.teknisio.app.data.model.OrderResponse;

public class OrderRepository {
    public interface OrderCallback {
        void onSuccess(OrderResponse response);
        void onError(String error);
    }
    
    public void createOrder(RequestBody data, MultipartBody.Part image, OrderCallback callback) {
        callback.onError("API belum diimplementasikan sepenuhnya");
    }
}
