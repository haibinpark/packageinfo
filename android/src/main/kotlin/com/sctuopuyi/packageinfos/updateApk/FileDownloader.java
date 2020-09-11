package com.sctuopuyi.packageinfos.updateApk;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by fengmlo on 2018/3/1.
 */

public class FileDownloader {

    public static Observable<Response> download(String url, ProgressListener progressListener) {

        final Request request = new Request.Builder()
                .url(url)
                .build();

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addNetworkInterceptor(new ProgressInterceptor(progressListener))
                .build();

        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        subscriber.onNext(response);
                        subscriber.onCompleted();
                    }
                });
            }
        });

    }

}
