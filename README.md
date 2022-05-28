# NCMusic

小明平时喜欢听歌，听歌喜欢用网易云，因为觉得网易云用户体验good good good，俺也觉得。  
然后今年年初在公司项目尝试接入Compose。为了更加熟悉Compose，俺就寻思着撸个小项目练练手，既然网易云用户体验good good good，那就仿写几个网易云的页面试试水吧。接着就发现有一些页面的交互效果实现起来并没有很简单，例如我的页面列表滑动时和TabLayout的联动以及下拉到顶部后无缝过渡到个人中心页面、歌曲播放页面切歌的效果等。  
  
项目采用单Activity+ComposeNavigation进行路由跳转，接口数据来源于[Binaryify](https://github.com/Binaryify)
大佬的[NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi)。  (PS:接口请求速度有点慢，另外手机号密码登录有时候会报错，得看缘分)  
使用MVVM开发模式，封装了页面状态组件ViewStateComponent   
结合pagging3，封装通用列表组件ViewStateListPagingComponent，支持首次加载及列表下拉刷新/上拉加载更多时header/footer的各种状态自动切换，
修改官方SwipeRefreshLayout并自定义header，添加下拉刷新成功/失败的状态     
支持主题切换   
...

# 主要开源框架  
* [accompanist-pager](https://google.github.io/accompanist/pager/)

* [coil](https://github.com/coil-kt/coil)

* [mmkv](https://github.com/Tencent/MMKV)

* [compose-collapsing-toolbar](https://github.com/onebone/compose-collapsing-toolbar)

* [exoPlayer](https://github.com/google/ExoPlayer)

* [hilt](https://developer.android.google.cn/training/dependency-injection/hilt-jetpack#compose)

* [paging3](https://developer.android.google.cn/topic/libraries/architecture/paging/v3-overview)

* [navigation-compose](https://developer.android.google.cn/jetpack/compose/navigation)

* [accompanist-systemuicontroller](https://google.github.io/accompanist/systemuicontroller/)  

# APK下载地址
[https://www.pgyer.com/ku0P](https://www.pgyer.com/ku0P)

# 效果图
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/首页.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/歌单列表.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/音乐播放.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/歌词.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/评论.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/音乐播放通知栏.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/视频列表.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/视频播放.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/主题切换.gif) 




