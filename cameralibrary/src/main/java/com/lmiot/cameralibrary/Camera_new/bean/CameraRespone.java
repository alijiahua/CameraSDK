package com.lmiot.cameralibrary.Camera_new.bean;

import java.util.List;

/**
 * Created by ming on 2016/7/25.
 */
public class CameraRespone {
    /**
     * errcode : 0
     * errmsg : ok
     * content : [{"bIsMRMode":false,"isCheck":false,"isRecvMsg":true,"isReverse":false,"lLastMsgFreshTime":0,"lLastMsgGetTime":0,"lOnLineStatChaneTime":0,"nDevID":3580,"nID":-1,"nMRPort":0,"nNewMsgCount":0,"nOnLineStat":0,"nPort":8800,"nSaveType":1011,"strDomain":"","strIP":"","strMac":"","strName":"VSTA088828GYZXN","strPassword":"","strUsername":"","nickName":""},{"bIsMRMode":false,"isCheck":false,"isRecvMsg":true,"isReverse":false,"lLastMsgFreshTime":0,"lLastMsgGetTime":0,"lOnLineStatChaneTime":0,"nDevID":3561,"nID":-1,"nMRPort":0,"nNewMsgCount":0,"nOnLineStat":0,"nPort":8800,"nSaveType":1011,"strDomain":"","strIP":"","strMac":"","strName":"EZCO-001150-XKZRE","strPassword":"","strUsername":"","nickName":"EZCO-001150-XKZRE"}]
     */

    private String errcode;
    private String errmsg;
    private List<ContentBean> content;

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

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * bIsMRMode : false
         * isCheck : false
         * isRecvMsg : true
         * isReverse : false
         * lLastMsgFreshTime : 0
         * lLastMsgGetTime : 0
         * lOnLineStatChaneTime : 0
         * nDevID : 3580
         * nID : -1
         * nMRPort : 0
         * nNewMsgCount : 0
         * nOnLineStat : 0
         * nPort : 8800
         * nSaveType : 1011
         * strDomain :
         * strIP :
         * strMac :
         * strName : VSTA088828GYZXN
         * strPassword :
         * strUsername :
         * nickName :
         */

        private boolean bIsMRMode;
        private boolean isCheck;
        private boolean isRecvMsg;
        private boolean isReverse;
        private int lLastMsgFreshTime;
        private int lLastMsgGetTime;
        private int lOnLineStatChaneTime;
        private int nDevID;
        private int nID;
        private int nMRPort;
        private int nNewMsgCount;
        private int nOnLineStat;
        private int nPort;
        private int nSaveType;
        private String strDomain;
        private String strIP;
        private String strMac;
        private String strName;
        private String strPassword;
        private String strUsername;
        private String nickName;

        public boolean isBIsMRMode() {
            return bIsMRMode;
        }

        public void setBIsMRMode(boolean bIsMRMode) {
            this.bIsMRMode = bIsMRMode;
        }

        public boolean isIsCheck() {
            return isCheck;
        }

        public void setIsCheck(boolean isCheck) {
            this.isCheck = isCheck;
        }

        public boolean isIsRecvMsg() {
            return isRecvMsg;
        }

        public void setIsRecvMsg(boolean isRecvMsg) {
            this.isRecvMsg = isRecvMsg;
        }

        public boolean isIsReverse() {
            return isReverse;
        }

        public void setIsReverse(boolean isReverse) {
            this.isReverse = isReverse;
        }

        public int getLLastMsgFreshTime() {
            return lLastMsgFreshTime;
        }

        public void setLLastMsgFreshTime(int lLastMsgFreshTime) {
            this.lLastMsgFreshTime = lLastMsgFreshTime;
        }

        public int getLLastMsgGetTime() {
            return lLastMsgGetTime;
        }

        public void setLLastMsgGetTime(int lLastMsgGetTime) {
            this.lLastMsgGetTime = lLastMsgGetTime;
        }

        public int getLOnLineStatChaneTime() {
            return lOnLineStatChaneTime;
        }

        public void setLOnLineStatChaneTime(int lOnLineStatChaneTime) {
            this.lOnLineStatChaneTime = lOnLineStatChaneTime;
        }

        public int getNDevID() {
            return nDevID;
        }

        public void setNDevID(int nDevID) {
            this.nDevID = nDevID;
        }

        public int getNID() {
            return nID;
        }

        public void setNID(int nID) {
            this.nID = nID;
        }

        public int getNMRPort() {
            return nMRPort;
        }

        public void setNMRPort(int nMRPort) {
            this.nMRPort = nMRPort;
        }

        public int getNNewMsgCount() {
            return nNewMsgCount;
        }

        public void setNNewMsgCount(int nNewMsgCount) {
            this.nNewMsgCount = nNewMsgCount;
        }

        public int getNOnLineStat() {
            return nOnLineStat;
        }

        public void setNOnLineStat(int nOnLineStat) {
            this.nOnLineStat = nOnLineStat;
        }

        public int getNPort() {
            return nPort;
        }

        public void setNPort(int nPort) {
            this.nPort = nPort;
        }

        public int getNSaveType() {
            return nSaveType;
        }

        public void setNSaveType(int nSaveType) {
            this.nSaveType = nSaveType;
        }

        public String getStrDomain() {
            return strDomain;
        }

        public void setStrDomain(String strDomain) {
            this.strDomain = strDomain;
        }

        public String getStrIP() {
            return strIP;
        }

        public void setStrIP(String strIP) {
            this.strIP = strIP;
        }

        public String getStrMac() {
            return strMac;
        }

        public void setStrMac(String strMac) {
            this.strMac = strMac;
        }

        public String getStrName() {
            return strName;
        }

        public void setStrName(String strName) {
            this.strName = strName;
        }

        public String getStrPassword() {
            return strPassword;
        }

        public void setStrPassword(String strPassword) {
            this.strPassword = strPassword;
        }

        public String getStrUsername() {
            return strUsername;
        }

        public void setStrUsername(String strUsername) {
            this.strUsername = strUsername;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }

/*
    private String errcode;
    private String errmsg;
    List<DeviceInfo> content;

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

    public List<DeviceInfo> getContent() {
        return content;
    }

    public void setContent(List<DeviceInfo> content) {
        this.content = content;
    }*/



}
