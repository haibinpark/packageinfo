package com.sctuopuyi.packageinfos.updateApk;

/**
 * Created by fengmlo on 2018/3/1.
 */

public class CheckUpdateResponse {

    private int versionCode;
    private String version;
    private String versionType;
    private String updateStrategy;
    private String updateUrl;
    private String releaseNotes;
    private String currentVersionReleaseDate;
    private float fileSizeBytes;
    private String updateMode;

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

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public String getUpdateStrategy() {
        return updateStrategy;
    }

    public void setUpdateStrategy(String updateStrategy) {
        this.updateStrategy = updateStrategy;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public String getCurrentVersionReleaseDate() {
        return currentVersionReleaseDate;
    }

    public void setCurrentVersionReleaseDate(String currentVersionReleaseDate) {
        this.currentVersionReleaseDate = currentVersionReleaseDate;
    }

    public float getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(float fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    public boolean isForce() {
        return "1".equals(updateStrategy);
    }
}
