    package com.example.uiproject.api;

    import com.example.uiproject.admin.model.AddCarRequest;
    import com.example.uiproject.admin.model.Brand;
    import com.example.uiproject.admin.model.Line;
    import com.example.uiproject.admin.model.ResultDTO2;
    import com.example.uiproject.admin.model.UserRequest;
    import com.example.uiproject.entity.CarBrandDTO;
    import com.example.uiproject.entity.CarDTO;
    import com.example.uiproject.entity.BookingRequest;
    import com.example.uiproject.entity.ContractDTO;
    import com.example.uiproject.entity.CustomerDTO;
    import com.example.uiproject.entity.GGLoginRequest;
    import com.example.uiproject.entity.PaymentResDTO;
    import com.example.uiproject.entity.ResultDTO;
    import com.example.uiproject.entity.updateProfileRequest;

    import java.util.List;
    import java.util.Map;
    import okhttp3.MultipartBody;
    import retrofit2.Call;
    import retrofit2.http.Body;
    import retrofit2.http.GET;
    import retrofit2.http.HeaderMap;
    import retrofit2.http.Multipart;
    import retrofit2.http.POST;
    import retrofit2.http.Part;
    import retrofit2.http.Path;
    import retrofit2.http.Query;
    import retrofit2.http.QueryMap;

    public interface ApiService {

        @POST("/api/auth/login")
        Call<Object> login(@Body Map<String,String> loginRequest);

        @POST("/api/auth/loginAdmin")
        Call<Object> loginAdmin(@Body Map<String,String> loginRequest);

        @POST("api/auth/ggLogin")
        Call<Object> loginGG(@Body GGLoginRequest ggLoginRequest);

        @POST("/api/auth/forgot")
        Call<Object> forgot(@Body Map<String,String> forgotRequest);

        @POST("/api/auth/verify-otp")
        Call<Object> otpCheck (@Body Map<String,String> verifyRequest);

        @POST("/api/auth/reset")
        Call<Object> resetPass (@Body Map<String,String> resetRequest);

        @POST("/api/auth/register")
        Call<ResultDTO2> register(@Body UserRequest userRequest);

        @POST("/api/auth/verifyOtp")
        Call<ResultDTO2> verifyOtp(@Body UserRequest userRequest);

        @GET("/api/carbrand")
        Call<List<CarBrandDTO>> getAllCarBrandActive ();

        @GET("/api/car/brand/{idBrand}")
        Call<List<CarDTO>> getAllCarOfBrand (@Path("idBrand") Long idBrand);
        @GET("/api/car")
        Call<List<CarDTO>> getAllCar ();

        @GET("/api/car/findByNameActive")
        Call<List<CarDTO>> getCarByName (@Query("name") String name);

        @POST("/api/car/book")
        Call<Object> book (@Body BookingRequest bookingRequest, @HeaderMap Map<String,Object> headers);
      
        @GET("/api/car/new")
        Call<List<CarDTO>> getNewCar ();
      
        @GET("/api/car/sale")
        Call<List<CarDTO>> getSaleCar ();

        @GET("api/carbrand")
        Call<List<Brand>> getAllBrands();

        @GET("api/carline")
        Call<List<Line>> getAllLines();

        @Multipart
        @POST("/api/upload/multiple")
        Call<List<String>> uploadMultipleImages(@Part List<MultipartBody.Part> images);

        @POST("/api/car/insertCar")
        Call<Void> createCar(@Body AddCarRequest car);

        @GET("/api/car/findCar")
        Call<List<CarDTO>> findCar (@QueryMap Map<String, Object> params);

        @GET("/api/user/profile")
        Call<CustomerDTO> getUser (@HeaderMap Map<String,Object> headers);

        @POST("/api/user/updateProfile")
        Call<ResultDTO> updateProfile (@Body updateProfileRequest request, @HeaderMap Map<String,Object> headers);

        @GET("/api/payment/create_payment")
        Call<PaymentResDTO> getPayment (@Query("idContract") Long idContract,
                                          @Query("price") Long price);

        @GET("/api/contract/getByCustomerId")
        Call<List<ContractDTO>> getContractByCusId (@Query("customerId") Long idUser);
    }
