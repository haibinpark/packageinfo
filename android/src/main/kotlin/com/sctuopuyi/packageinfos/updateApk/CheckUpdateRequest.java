package com.sctuopuyi.packageinfos.updateApk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengmlo on 2018/3/1.
 */

public class CheckUpdateRequest {

    public final static int VERSION_TYPE_RELEASE = 1;
    public final static int VERSION_TYPE_TEST = 2;

    private int versionCode;
    private String version;
    private int versionType;
    private String osType = "Android";
    private String accountID;
    private String deviceToken;
    private String appId;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionType() {
        return versionType;
    }

    public void setVersionType(int versionType) {
        this.versionType = versionType;
    }

    public String getOSType() {
        return osType;
    }

    public void setOSType(String OSType) {
        this.osType = OSType;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getAppID() {
        return appId;
    }

    public void setAppID(String appID) {
        this.appId = appID;
    }

    public Map<String, String> toQueryMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("versionCode", Integer.toString(versionCode));
        if (version != null) map.put("version", version);
        map.put("versionType", Integer.toString(versionType));

        if (osType != null) map.put("osType", osType);
        if (accountID != null) map.put("accountID", accountID);
        if (deviceToken != null) map.put("deviceToken", deviceToken);
        if (appId != null) map.put("appId", appId);
        return map;
    }
}
