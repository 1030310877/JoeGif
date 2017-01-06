# JoeGif
Android Gif解析
##Introduction
Android Gif图片解析显示。  
已初步实现gif的播放，使用LruCache进行缓存，解决了内存波动。  
![](https://github.com/1030310877/JoeGif/blob/master/gif/demo.gif)  
使用GifImageView方便加载Gif图片：
```
    <com.joe.giflibrary.GifImageView
        android:id="@+id/img"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        app:syncShow="true"
        app:srcId="@drawable/loading" />
```
srcId:Gif对应的图片资源  
syncShow:是否同步显示图片（边解码边显示）
lowMemory:是否以低内存形式处理（只能处理较小的图片，效果不佳）
###正在编写
* 是否循环播放的设置
- 文本扩展块的显示  
* 内存优化（容量大的gif图片，解码后使得app占用非常大的内存）
- JNI方式进行gif解码
