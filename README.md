# JoeGif
Android Gif解析
##Introduction
Android Gif图片解析显示。  
已初步实现gif的播放，使用LruCache进行缓存，解决了内存波动。  
![](https://github.com/1030310877/JoeGif/blob/master/gif/demo.gif)  


###正在编写
* 是否循环播放的设置
- 文本扩展块的显示  
* 内存优化（容量大的gif图片，解码后使得app占用非常大的内存）
- JNI方式进行gif解码
