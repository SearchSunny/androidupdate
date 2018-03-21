package com.android_update.entity;

/**
 * 检查新版本诊所APP更新
 */

public class CheckNewUpdateInfo {

    private String status;
    private String message;

    private CheckNewUpdate result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CheckNewUpdate getResult() {
        return result;
    }

    public void setResult(CheckNewUpdate result) {
        this.result = result;
    }
}
