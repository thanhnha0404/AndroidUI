    package com.example.uiproject.api;

    import com.example.uiproject.admin.model.AddCarRequest;
    import com.example.uiproject.admin.model.Brand;
    import com.example.uiproject.admin.model.Line;
    import com.example.uiproject.entity.CarBrandDTO;
    import com.example.uiproject.entity.CarDTO;

    import java.util.List;
    import java.util.Map;

    import okhttp3.MultipartBody;
    import okhttp3.ResponseBody;
    import retrofit2.Call;
    import retrofit2.http.Body;
    import retrofit2.http.GET;
    import retrofit2.http.Multipart;
    import retrofit2.http.POST;
    import retrofit2.http.Part;
    import retrofit2.http.Path;
    import retrofit2.http.Query;

    public interface ApiService {

        @POST("/api/auth/login")
        Call<Object> login(@Body Map<String,String> loginRequest);


        @POST("/api/auth/forgot")
        Call<Object> forgot(@Body Map<String,String> forgotRequest);

        @POST("/api/auth/otp")
        Call<Object> otpCheck (@Body Map<String,String> verifyRequest);

        @POST("/api/auth/reset")
        Call<Object> resetPass (@Body Map<String,String> resetRequest);

        @GET("/api/carbrand")
        Call<List<CarBrandDTO>> getAllCarBrandActive ();

        @GET("/api/car/brand/{idBrand}")
        Call<List<CarDTO>> getAllCarOfBrand (@Path("idBrand") Long idBrand);
        @GET("/api/car")
        Call<List<CarDTO>> getAllCar ();
        @GET("/api/car/new")
        Call<List<CarDTO>> getNewCar ();
        @GET("/api/car/sale")
        Call<List<CarDTO>> getSaleCar ();

        @GET("/api/carbrand")
        Call<List<Brand>> getAllBrands();

        @GET("/api/carline")
        Call<List<Line>> getAllLines();

        @Multipart
        @POST("/api/upload/multiple")
        Call<List<String>> uploadMultipleImages(@Part List<MultipartBody.Part> images);

        @POST("/api/car/insertCar")
        Call<Void> createCar(@Body AddCarRequest car);

    }
