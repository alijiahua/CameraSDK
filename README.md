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
            compile 'com.github.lmiot:CameraSDK:1.5'
        }

## 三.启动摄像头页面及设置用户名：

        startActivity(new Intent(MainActivity.this, CameraDevices.class));
        DataUtil.setUerName(SPUtil.getUserName(this)); //设置摄像头保存用户名
        注：摄像头数据目前保存在本地，要保存到云端参考SqlUtil中的相关方法




## 五.参考效果图：
![](https://github.com/lmiot/CameraSDK/blob/master/img/main.png)
![](https://github.com/lmiot/CameraSDK/blob/master/img/add.png)
![](https://github.com/lmiot/CameraSDK/blob/master/img/play.png)
![](https://github.com/lmiot/CameraSDK/blob/master/img/setting.png)





