package com.lmiot.cameralibrary.SQL;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 创建日期：2017-12-04 17:15
 * 作者:Mr Li
 * 描述:
 */

@Entity
public class CamerBean {

    @Id(autoincrement = true)
    private Long id;
    private String cameraID;
    private String userName;
    private String cameraName;
    private String cameraPassword;
    private boolean psRight;
    @Generated(hash = 1479340984)
    public CamerBean(Long id, String cameraID, String userName, String cameraName,
            String cameraPassword, boolean psRight) {
        this.id = id;
        this.cameraID = cameraID;
        this.userName = userName;
        this.cameraName = cameraName;
        this.cameraPassword = cameraPassword;
        this.psRight = psRight;
    }
    @Generated(hash = 2084347909)
    public CamerBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCameraID() {
        return this.cameraID;
    }
    public void setCameraID(String cameraID) {
        this.cameraID = cameraID;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getCameraName() {
        return this.cameraName;
    }
    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }
    public String getCameraPassword() {
        return this.cameraPassword;
    }
    public void setCameraPassword(String cameraPassword) {
        this.cameraPassword = cameraPassword;
    }
    public boolean getPsRight() {
        return this.psRight;
    }
    public void setPsRight(boolean psRight) {
        this.psRight = psRight;
    }
   


  
}
