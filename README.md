# CameraSDK帮助文档

  
  

## 一.首先在项目的gradle中引用：
    	allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}


## 二.其次在dependencies中添加：
     dependencies {
            compile 'com.github.lmiot:CameraSDK:1.0'
        }


## 三.启动摄像头页面：
        /**
         * 进入摄像头页面
         * @param sessionID 用户sessionID
         */
         private void startCamera(String sessionID) {
                         SPUtil.setSessionID(sessionID);
                         startActivity(new Intent(MainActivity.this, CameraDevices.class));
            }

## 四.建议修改目标版本为22：

      targetSdkVersion 22

## 五.参考效果图：
![](https://github.com/lmiot/CameraSDK/blob/master/img/main.png)
![](https://github.com/lmiot/CameraSDK/blob/master/img/add.png)
![](https://github.com/lmiot/CameraSDK/blob/master/img/play.png)
![](https://github.com/lmiot/CameraSDK/blob/master/img/setting.png)

## 六.联系方式：980846919@qq.com


