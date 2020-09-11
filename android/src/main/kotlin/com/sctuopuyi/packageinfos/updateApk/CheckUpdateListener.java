package com.sctuopuyi.packageinfos.updateApk;

/**
 * Created by fengmlo on 2018/3/19.
 */

public interface CheckUpdateListener {
    void onCheckUpdate(boolean newVersion, CheckUpdateResponse response);
}
