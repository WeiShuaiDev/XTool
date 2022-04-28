# XTool使用说明

![](https://github.com/WeiShuaiDev/XTool/blob/main/screenshots/cover.png)

## 一、功能介绍

🔥 🔥 🔥`App`错误日志、请求数据、`Log`信息抓取，同时每次触发错误，通过手机消息列表弹出，增加浮动按钮入口，作为`Tool`工具总入口。
<div>
	<img src="https://github.com/WeiShuaiDev/XTool/blob/main/screenshots/screenshot_1.jpg" width=20%>
	<img src="https://github.com/WeiShuaiDev/XTool/blob/main/screenshots/screenshot_2.jpg" width=20%>
	<img src="https://github.com/WeiShuaiDev/XTool/blob/main/screenshots/screenshot_3.jpg" width=20%>  
	<img src="https://github.com/WeiShuaiDev/XTool/blob/main/screenshots/screenshot_4.jpg" width=20%>  
</div>
<div>
	<img src="https://github.com/WeiShuaiDev/XTool/blob/main/screenshots/screenshot_details_1.jpg" width=20%>
	<img src="https://github.com/WeiShuaiDev/XTool/blob/main/screenshots/screenshot_details_2.jpg" width=20%>
	<img src="https://github.com/WeiShuaiDev/XTool/blob/main/screenshots/screenshot_details_3.jpg" width=20%>  
    <img src="https://github.com/WeiShuaiDev/XTool/blob/main/screenshots/screenshot_details_4.jpg" width=20%>  
</div>

### 1、引用资源包

```
dependencies {
     implementation 'io.github.weishuaidev:xtool:1.4.0'
}
```

### 2、初始化流程(可以忽略这一步)

默认工具库已经默认成功初始化，同时设置关闭消息通知栏，如果需要开启通知栏，可以在`Application#onCreate()`
方法调用`XToolReporter.enableNotification()`

- 加载资源库

  ```
  XToolReporter.init(getApplication());
  ```

- 设置消息通知栏

  ```
  XToolReporter.disableNotification(); 关闭
  XToolReporter.enableNotification(); 开启
  ```

#### 3、抓取数据入口接入

抓取总共4种数据，分别：Crashes、Exceptions、Http、Log

1. ##### `Crashes`数据，工具库自动会抓取，不需要接入

2. ##### `Exceptions`数据，在程序中的`try{..}catch{..}`把错误信息交给`XToolReporter.logException(Exception)`

   ```
   try{
   
      } catch (e: Exception) {
         //log caught Exception
         XToolReporter.logException(e)
      }
   ```

3. ##### `Https`数据，在`OKHttp`拦截器中增加工具库中`LoggingInterceptor`类

   ```
   val client: OkHttpClient = OkHttpClient.Builder()
                   .addInterceptor(LoggingInterceptor())
                   .build()
   ```

4. ##### `Log`数据，在需要统计位置直接调用下列方法，使用方法跟系统`Log`类似

   ```
   LoggerUtils.i("info")
   
   LoggerUtils.e("error")
   
   LoggerUtils.d("debug")
   
   LoggerUtils.v("verbose")
   
   LoggerUtils.w("warn")
   ```

#### 4、数据查看入口接入

- 获取悬浮按钮权限，并启动悬浮按钮，通过点击悬浮按钮跳转到`ChooseModuleActivity`界面。如果不需要悬浮按钮入口，可以直接跳转到`ChooseModuleActivity`
  ,也不需要申请权限。

  ```
  override fun onCreate(savedInstanceState: Bundle?) {
    View.setOnClickListener {
  			//关闭悬浮按钮
              XToolReporter.disableAndzu()
              //申请悬浮按钮权限
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                  val intent = Intent(
                      Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                      Uri.parse("package:$packageName")
                  )
                  startActivityForResult(intent, 1)
              } else {
              	//初始化悬浮按钮
                  XToolReporter.initBubbles(SampleApplication.appContext)
              }
          }
  }
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          if (requestCode == 1) {
              if (resultCode == RESULT_OK) {
              //初始化悬浮按钮
                  XToolReporter.initBubbles(SampleApplication.appContext)
              } 
          } else {
              super.onActivityResult(requestCode, resultCode, data)
          }
      }
  
  
  ```

- 资源回收

  ```
  override fun onDestroy() {
       super.onDestroy()
       try {
            XToolReporter.recycle()
       } catch (e: Exception) {
            e.printStackTrace()
       }
  }
  ```

## 二、参考资料

[In-App Android Debugging Tool With Enhanced Logging, Networking Info, Crash reporting And More.](https://github.com/isacan/Andzu)

[CrashReporter is a handy tool to capture app crashes and save them in a file](https://github.com/MindorksOpenSource/CrashReporter)

## 赞赏

在开源的同时，解决别人需求，是一件很高兴的事。

> 赠人玫瑰，手留余香

<div align="center">
<img src="https://github.com/WeiSmart/tablayout/blob/master/screenshots/weixin_pay.jpg" width=20%>
<img src="https://github.com/WeiSmart/tablayout/blob/master/screenshots/zifubao_pay.jpg" width=20%>
</div>


---

## About me

- #### Email:linwei9605@gmail.com

- #### Blog: [https://offer.github.io/](https://offer.github.io/)

- #### 掘金: [https://juejin.im/user/59091b030ce46300618529e0](https://juejin.im/user/59091b030ce46300618529e0)

## License

```
   Copyright 2022 offer

      Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.```

```