SelectAlbum
====
## 简介
SelectAlbum项目主要用于选择照片并获取返回路径，支持拍照返回，包含了library和使用入口demo。
## 使用方法
跳转的时候示例如下,第一个参数表示Context， 第二个表示请求码； 第三个表示是否需要拍照入口， 第四个表示最多可选择数量。
...Java
AlbumActivity.launch(this,200,true,5);
返回的intent data得到路径数据如下，paths就是选择的图片路径：
...Java
ArrayList<String> paths = data.getStringArrayListExtra(AlbumActivity.REQUEST_DATA);