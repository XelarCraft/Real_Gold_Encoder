package wesa.xelar.realgoldencoder.network;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private final String Base_URL="http5555/";

    public Retrofit getClientLog(){

        Interceptor interceptor = chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        };

        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor();

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(loggingInterceptor).build();

        return new Retrofit.Builder().baseUrl(Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
