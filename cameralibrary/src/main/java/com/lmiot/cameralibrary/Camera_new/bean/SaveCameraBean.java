package com.lmiot.cameralibrary.Camera_new.bean;

import java.util.List;

/**
 * Created by ming on 2016/7/25.
 */
public class SaveCameraBean {
    String sessionID;
    List<CameraRespone.ContentBean> content;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public List<CameraRespone.ContentBean> getContent() {
        return content;
    }

    public void setContent(List<CameraRespone.ContentBean> content) {
        this.content = content;
    }
}
