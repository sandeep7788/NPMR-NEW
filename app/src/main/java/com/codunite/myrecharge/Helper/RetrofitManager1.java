package com.codunite.myrecharge.Helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitManager1 {


    private static Retrofit retrofit = null;
    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (RetrofitManager.class) {
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                retrofit = new Retrofit.Builder()
                        .baseUrl("http://therippco.com/api/")
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
        }
        return retrofit;
    }

    private static OkHttpClient okHttpClient = getUnsafeOkHttpClient().connectTimeout(30,TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            Request.Builder builder = chain.request().newBuilder();
//            builder.addHeader("content-type", "application/json");
   //         builder.addHeader("Access-Control-Allow-Origin", "api");
  //          builder.addHeader("APPKEY","JsfnZGWj20NJMIyg2LDIvQ==");

            return chain.proceed(builder.build());
        }
    }).addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();

    private static Retrofit newRetrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = getUnsafeOkHttpClient().addInterceptor(interceptor).readTimeout(80, TimeUnit.SECONDS).connectTimeout(80, TimeUnit.SECONDS).build();
        Gson gson = new GsonBuilder().serializeNulls().create();


        newRetrofit = new Retrofit.Builder()
                .baseUrl("http://therippco.com/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return newRetrofit;
    }


    public static Retrofit getGoogleClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = getUnsafeOkHttpClient().addInterceptor(interceptor).readTimeout(80, TimeUnit.SECONDS).connectTimeout(80, TimeUnit.SECONDS).build();


        Retrofit tempRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/youtube/v3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return tempRetrofit ;
    }



    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
