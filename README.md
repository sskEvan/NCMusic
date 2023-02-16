# NCMusic

小明平时喜欢听歌，听歌喜欢用网易云，因为觉得网易云用户体验good good good，俺也觉得。  
然后今年年初在公司项目尝试接入Compose。为了更加熟悉Compose，俺就寻思着撸个小项目练练手，既然网易云用户体验good good good，那就仿写几个网易云的页面试试水吧。接着就发现有一些页面的交互效果实现起来并没有很简单，例如我的页面列表滑动时和TabLayout的联动以及下拉到顶部后无缝过渡到个人中心页面、歌曲播放页面切歌的效果等。  
项目采用单Activity+ComposeNavigation进行路由跳转，接口数据来源于[Binaryify](https://github.com/Binaryify)
大佬的[NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi);        
使用MVVM开发模式，封装了页面状态组件ViewStateComponent;        
Compose版本为1.3.0，升级Compose版本为1.3.0后，发现官方支持瀑布流了，把云村的视频列表改成瀑布流样式，发现以前网上抄的上拉分页方式在瀑布流列表中不能很好的工作，决定重新自定义一个RefreshLayout组件来支持Compose的下拉刷新、上拉加载更多。并结合paging3封装ViewStateListPagingComponent(列表样式)、ViewStateStaggeredGridPagingComponent(瀑布流样式)，统一处理列表页首次加载页面状态的切换，以及触发下拉刷新/上拉加载更多动作时header/footer各种状态的切换。使用伪代码如下： 
<pre><code>
ViewStateListPagingComponent(key = "your key",    
    loadDataBlock = { // 获取列表数据源 }) { itemDatas ->    
        itemsIndexed(itemDatas) { index, item ->    
            // 具体列表item样式    
        }    
    } </code></pre>
                
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
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/登录.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/首页.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/歌单列表.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/音乐播放.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/歌词.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/评论.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/音乐播放通知栏.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/视频列表.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/视频播放.gif)
![img](https://github.com/sskEvan/NCMusic/blob/master/gif/主题切换.gif) 




