package com.lmiot.cameralibrary.Camera_new.bean;

/**
 * Created by ming on 2016/11/30.
 */

public class Response433 {
    /**
     * errcode : 0
     * errmsg : ok
     * HostID : 8000002700243135510f35353833
     * HostTypeCode : 8000
     * HostTypeName : 智能主机1代
     */

    private String errcode;
    private String errmsg;
    private String HostID;
    private String HostTypeCode;
    private String HostTypeName;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getHostID() {
        return HostID;
    }

    public void setHostID(String HostID) {
        this.HostID = HostID;
    }

    public String getHostTypeCode() {
        return HostTypeCode;
    }

    public void setHostTypeCode(String HostTypeCode) {
        this.HostTypeCode = HostTypeCode;
    }

    public String getHostTypeName() {
        return HostTypeName;
    }

    public void setHostTypeName(String HostTypeName) {
        this.HostTypeName = HostTypeName;
    }
}
