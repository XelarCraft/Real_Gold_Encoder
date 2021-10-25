package wesa.xelar.realgoldencoder.network;


import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Xelar on 8/21/2016.
 */
public interface APIService {

    @GET("cardAPI/")
    Call<ResponseBody> getData(@Query("id") String id,
                               @Query("key") String key);

    @POST("register/updateProfile")
    Call<ResponseBody> updateProfileData(@Body() JsonObject jsonObject);

    @Multipart
    @POST("register/importDocuments")
    Call<ResponseBody> uploadCircleProfile(@Part("securityKey") RequestBody securityKey,
                                           @Part("id") RequestBody userID,
                                           @Part("fileType") RequestBody fileType,
                                           @Part MultipartBody.Part profPic);
}
