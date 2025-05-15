package com.example.uiproject.admin.api;

import com.example.uiproject.admin.model.AddCarRequest;
import com.example.uiproject.admin.model.Brand;
import com.example.uiproject.admin.model.CarDTO;
import com.example.uiproject.admin.model.Line;
import com.example.uiproject.admin.model.PaymentDTO;
import com.example.uiproject.admin.model.ResultDTO2;
import com.example.uiproject.admin.model.ContractInfor;
import com.example.uiproject.admin.model.UpdateContractRequest;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiServiceAdmin {
    @GET("api/carbrand")
    Call<List<Brand>> getAllBrands();

    @POST("api/carbrand/insert")
    Call<ResultDTO2<Brand>> addBrand(@Body Brand brand);

    @PUT("api/carbrand/update")
    Call<ResultDTO2<Brand>> updateBrand(@Body Brand brand);

    @DELETE("api/carbrand/delete/{id}")
    Call<ResultDTO2<Brand>> deleteBrand(@Path("id") Long id);
    
    @GET("api/carbrand/{id}")
    Call<Brand> getBrandById(@Path("id") Long id);

    @GET("api/carline")
    Call<List<Line>> getAllLines();

    @Multipart
    @POST("/api/upload/multiple")
    Call<List<String>> uploadMultipleImages(@Part List<MultipartBody.Part> images);

    @GET("/api/car/GetAll")
    Call<List<CarDTO>> getAllCar ();

    @DELETE("/api/car/deleteCar/{id}")
    Call<String> deleteCar(@Path("id") Long id);

    @PUT("/api/car/updateCar")
    Call<String> updateCar(@Body AddCarRequest request);

    @GET("/api/contract/getAll")
    Call<List<ContractInfor>> getAllContract();

    @PUT("/api/contract/update")
    Call<ResultDTO2> updateStatus(@Body UpdateContractRequest request);

    @GET("/api/payment/getAll")
    Call<List<PaymentDTO>> getAllPayment();
}
