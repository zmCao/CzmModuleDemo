package com.czm.module.common.rxjava_retrofit;

import android.text.TextUtils;

import com.czm.module.common.base.Constant;
import com.czm.module.common.rxjava_retrofit.convert.CustomGsonConverterFactory;
import com.czm.module.common.utils.NetworkUtils;
import com.czm.module.common.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RetrofitManager {
    private volatile static RetrofitManager instance;
    //    private ApiService apiService;
    private Retrofit mRetrofit;
    /*用户设置的BASE_URL*/
    private static String BASE_URL = "";
    private Builder mBuilder;

    private RetrofitManager() {

    }

    public static RetrofitManager getInstance() {
        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    public RetrofitManager.Builder getBuilder() {
        return mBuilder;
    }

    public <T> T createApi(Class<T> tClass) {
        return mRetrofit.create(tClass);
    }

    private void setBuilder(RetrofitManager.Builder builder) {
        this.mBuilder = builder;
    }

    private void initRetrofit() {
        if (mRetrofit == null) {
            synchronized (RetrofitManager.class) {
                OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();
                if (Utils.isAppDebug()) {
                    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                    //可以设置请求过滤的水平,body,basic,headers
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    okHttpClient.addInterceptor(httpLoggingInterceptor);
                }
                File cacheFile = new File(Constant.PATH_CACHE);
                Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb 缓存的大小
                Interceptor cacheInterceptor = chain -> {
                    Request request = chain.request();
                    if (!NetworkUtils.isConnected()) {
                        request = request.newBuilder()
                                .cacheControl(CacheControl.FORCE_CACHE)
                                .build();
                    }
                    Response response = chain.proceed(request);
                    if (NetworkUtils.isConnected()) {
                        int maxAge = 0;
                        // 有网络时, 不缓存, 最大保存时长为0
                        response.newBuilder()
                                .header("Cache-Control", "public, max-age=" + maxAge)
                                .removeHeader("Pragma")
                                .build();
                    } else {
                        // 无网络时，设置超时为4周
                        int maxStale = 60 * 60 * 24 * 28;
                        response.newBuilder()
                                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .removeHeader("Pragma")
                                .build();
                    }
                    return response;
                };
                Interceptor tokenInterceptor = new Interceptor() {
                    @Override
                    public synchronized Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request authorised = originalRequest.newBuilder()
                                .header("token", mBuilder.sToken)
                                .build();

                        Response response = chain.proceed(authorised);
                        if (response.code() == 401) {//返回为token失效
//                            refreshToken();//重新获取token，此处的刷新token需要同步执行以防止异步到来的问题
                            Request newRequest = originalRequest.newBuilder()
                                    .header("token", mBuilder.sToken)
                                    .build();
                            return chain.proceed(newRequest);
                        }

                        return response;
                    }
                };
                okHttpClient.addInterceptor(cacheInterceptor)
                        .addInterceptor(tokenInterceptor)
                        .cache(cache)
                        .connectTimeout(60L, TimeUnit.SECONDS)
                        .readTimeout(60L, TimeUnit.SECONDS)
                        .writeTimeout(60L, TimeUnit.SECONDS);

                mRetrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient.build())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(CustomGsonConverterFactory.create())
                        .build();
            }
        }
    }

    /**
     * Build a new RetrofitManager.
     */
    public static final class Builder {
        private String builderBaseUrl = "";
        private String sToken;

        public Builder() {
        }

        /**
         * 请求地址的baseUrl，最后会被赋值给RetrofitManager的静态变量BASE_URL；
         *
         * @param baseUrl 请求地址的baseUrl
         */
        public RetrofitManager.Builder baseUrl(String baseUrl) {
            this.builderBaseUrl = baseUrl;
            return this;
        }

        public RetrofitManager.Builder token(String token) {
            this.sToken = token;
            return this;
        }

        public RetrofitManager build() {
            if (!TextUtils.isEmpty(builderBaseUrl)) {
                BASE_URL = builderBaseUrl;
            }
            RetrofitManager client = RetrofitManager.getInstance();
            client.initRetrofit();
            client.setBuilder(this);
            return client;
        }
    }
}
