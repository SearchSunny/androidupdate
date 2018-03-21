package com.android_update.entity;

/**
 *
 */

public class CheckNewUpdate {

    public String content;
    public String createTime;
    public String downloadUrl;
    public String hasNew;
    public String mustUpdate;
    public String name;
    public String version;
    public String versionSize;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String isHasNew() {
        return hasNew;
    }

    public void setHasNew(String hasNew) {
        this.hasNew = hasNew;
    }

    public String isMustUpdate() {
        return mustUpdate;
    }

    public void setMustUpdate(String mustUpdate) {
        this.mustUpdate = mustUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionSize() {
        return versionSize;
    }

    public void setVersionSize(String versionSize) {
        this.versionSize = versionSize;
    }
}
