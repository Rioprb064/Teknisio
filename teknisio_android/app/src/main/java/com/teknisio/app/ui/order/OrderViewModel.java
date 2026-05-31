package com.teknisio.app.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.teknisio.app.data.repository.OrderRepository;
import com.teknisio.app.data.model.OrderResponse;

public class OrderViewModel extends ViewModel {

    private final OrderRepository repository;

    // LiveData untuk memantau status loading dan hasil response
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<OrderResponse> orderResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public OrderViewModel(OrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<OrderResponse> getOrderResult() { return orderResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    /**
     * Fungsi untuk mensubmit order ke backend
     */
    public void submitOrder(String categoryId, String damageDesc, String date, String timeSlot, File imageFile) {
        isLoading.setValue(true);

        // 1. Siapkan data text (JSON) sebagai RequestBody
        String orderJson = String.format("{\"categoryId\":\"%s\", \"damageDescription\":\"%s\", \"orderDate\":\"%s\", \"timeslot\":\"%s\"}", 
                                          categoryId, damageDesc, date, timeSlot);
        RequestBody orderDataPart = RequestBody.create(MediaType.parse("application/json"), orderJson);

        // 2. Siapkan file gambar sebagai MultipartBody.Part (jika ada)
        MultipartBody.Part imagePart = null;
        if (imageFile != null && imageFile.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("damagePhoto", imageFile.getName(), requestFile);
        }

        // 3. Panggil repository (Retrofit call)
        repository.createOrder(orderDataPart, imagePart, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(OrderResponse response) {
                isLoading.postValue(false);
                orderResult.postValue(response);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
}
