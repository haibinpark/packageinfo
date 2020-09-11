package com.sctuopuyi.packageinfos.updateApk;

import android.content.Context;

import com.sctuopuyi.packageinfos.BuildConfig;

import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by fengmlo on 2018/3/1.
 */

class Net {

    private static Net net;

    private ServerApi serverApi;

    private Net(Context context, String url, String packageValue) {

        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //错误重连
        builder.retryOnConnectionFailure(true);

        //设置统一的请求头部参数
        Interceptor apikey = chain -> {
            Request request = chain.request();

            request = request.newBuilder()
                    .addHeader("packageCode", packageValue)
                    .build();
            return chain.proceed(request);
        };

        client = builder
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(apikey)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        serverApi = retrofit.create(ServerApi.class);
    }

    static Net init(Context context, String url, String packageTag) {
        if (net == null) {
            net = new Net(context, url,packageTag);
        }
        return net;
    }

    static Net getInstance() {
        if (net == null) {
            throw new RuntimeException("Net hasn't initialization.");
        }
        return net;
    }


    Observable<BaseHttpResponse<CheckUpdateResponse>> checkUpdate(Map<String, String> request) {
        return serverApi.checkUpdate(request);
    }

}
