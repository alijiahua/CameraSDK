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

   SqlUtil.getInstance().initDbHelp(this); //摄像头数据库初始化

        startActivity(new Intent(MainActivity.this, CameraDevices.class));
        ApiUtls.getInstance().setUerName("用户ID"); //设置保存摄像头数据的用户ID
        ApiUtls.getInstance().setMoreItem("隐藏菜单");//使用隐藏菜单，需继承CameraDevices.onMoreItemListener




## 五.参考效果图：
![](https://github.com/lmiot/CameraSDK/blob/master/img/main.png)
![](https://github.com/lmiot/CameraSDK/blob/master/img/add.png)
![](https://github.com/lmiot/CameraSDK/blob/master/img/play.png)
![](https://github.com/lmiot/CameraSDK/blob/master/img/setting.png)





