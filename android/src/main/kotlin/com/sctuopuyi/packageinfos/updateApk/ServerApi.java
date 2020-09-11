package com.sctuopuyi.packageinfos.updateApk;


import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 用于Retrofit的服务器Api类
 *
 * @author fengmlo
 */
public interface ServerApi {

    @GET("appVersionInfo")
    Observable<BaseHttpResponse<CheckUpdateResponse>> checkUpdate(@QueryMap Map<String, String> request);
}
