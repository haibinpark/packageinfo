package com.sctuopuyi.packageinfos.updateApk;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 用于显示下载进度的拦截器
 *
 * @author fengmlo
 */
public class ProgressInterceptor implements Interceptor {


	private ProgressListener listener;

	ProgressInterceptor(ProgressListener listener) {
		this.listener = listener;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Response originalResponse = chain.proceed(chain.request());
		return originalResponse.newBuilder()
				.body(new ProgressResponseBody(originalResponse.body(), listener))
				.build();
	}
}
