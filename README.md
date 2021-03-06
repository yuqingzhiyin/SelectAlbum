SelectAlbum
====
## 简介
SelectAlbum项目主要用于选择照片并获取返回路径，支持拍照返回，包含了library和使用入口demo。选择照片时，支持预览和缩放。
## 使用方法
跳转的时候示例如下,第一个参数表示Context， 第二个表示请求码； 第三个表示是否需要拍照入口， 第四个表示最多可选择数量。

```
AlbumActivity.launch(this,200,true,5);
```
返回的intent data得到路径数据如下，paths就是选择的图片路径：

```
ArrayList<String> paths = data.getStringArrayListExtra(AlbumActivity.REQUEST_DATA);
```
## 依赖库
本项目主要依赖了：
* 'com.github.bumptech.glide:glide:3.7.0'
* 'com.github.chrisbanes:PhotoView:2.0.0'
## 使用方法：
### gradle方式：

1.在根build.gradle添加仓库
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2.添加依赖
```
	dependencies {
	  	implementation 'com.github.yuqingzhiyin:SelectAlbum:v1.0'
	}
```
### maven方式：
1.添加仓库到build文件
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
2.添加依赖：
```
	<dependency>
	    <groupId>com.github.yuqingzhiyin</groupId>
	    <artifactId>SelectAlbum</artifactId>
	    <version>v1.0</version>
	</dependency>
```
## 项目截图
<img src="https://github.com/yuqingzhiyin/SelectAlbum/blob/master/截图_01.png" width="208" height="370"/>
<img src="https://github.com/yuqingzhiyin/SelectAlbum/blob/master/截图_02.png" width="208" height="370"/>
